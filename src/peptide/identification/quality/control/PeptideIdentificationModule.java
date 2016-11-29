/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum matrix quality control  *
 */
package peptide.identification.quality.control;

import collections.ProteinCollection;
import collections.ProteinPeptideCollection;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import matcher.ReferenceDatabaseMatcher;
import collection.creator.ProteinPeptideFileReader;
import collections.MatrixEntryCollection;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import matcher.PublicDatabaseMatcher;
import matrix.MatrixToCsvWriter;
import matrix.PeptideMatrix;
import tools.InputTools;

/**
 * A peptide identification quality control module.
 * Checks the quality of peptide mass spectrum output of COPD and Control samples.
 * Accounts for uniqueness to an individual database group, removes sequences known by uniprot,
 * and counts the occurrences of each peptide per sample.
 * @author vnijenhuis
 */
public class PeptideIdentificationModule {
    /**
     * @param args the command line arguments.
     * @throws org.apache.commons.cli.ParseException exception encountered while processing
     * command line options. Please check the input.
     * 
     * @throws java.io.IOException could not open or find the specified file or directory.
     * @throws InterruptedException process was interrupted.
     * @throws ExecutionException could not execute the call function.
     */
    public static void main(String[] args) throws ParseException, IOException, InterruptedException, ExecutionException {
        PeptideIdentificationModule peptideIdentification = new PeptideIdentificationModule();
        peptideIdentification.startQualityControl(args);
    }

    /**
     * Options for the command line interface.
     */
    private final Options commandlineOptions;

    /**
     * Private constructor to define primary functions.
     * Defines command line argument options.
     * Calls classes and functions to be used with this module.
     */
    private PeptideIdentificationModule() {
        //Creates all commandline options and their descriptions.
        //Help function.
        commandlineOptions = new Options();
        Option help = Option.builder("help")
                .desc("Help function to display all options.")
                .optionalArg(true)
                .build();
        commandlineOptions.addOption(help);
        Option spectrumMatch = Option.builder("spectrumMatch")
                .hasArg()
                .desc("Name of the psm file (DB search PSM.csv).")
                .build();
        commandlineOptions.addOption(spectrumMatch);
        //protein-peptide relations file name.
        Option proteinPeptide = Option.builder("proteinPeptides")
                .hasArg()
                .desc("Name of the protein-peptide file (protein-peptides.csv).")
                .build();
        commandlineOptions.addOption(proteinPeptide);
        //PSM file name.
        //Path to the database(s). Should contain folders per database. (uniprot/ensemble etc.)
        Option publicDatabases = Option.builder("publicDatabases")
                .hasArg()
                .desc("Path to the database folder (/home/name/Databases/uniprot.fasta.gz)")
                .build();
        commandlineOptions.addOption(publicDatabases);
        //A string that is present in the database(s). (eg. fasta.gz reads all fasta.gz files, uniprot reads the uniprot db file.
        Option referenceDatabases = Option.builder("referenceDatabases")
                .hasArg()
                .desc("Path and name of the combined database fasta. (/home/name/Fastsa/COPD-19-DB.fa)")
                .build();
        commandlineOptions.addOption(referenceDatabases);
        Option output = Option.builder("output")
                .hasArg()
                .desc("Path to the folder to create the output file.")
                .build();
        commandlineOptions.addOption(output);
        //Amount of threads to use.
        Option ensembl = Option.builder("removeEnsemblHits")
                .hasArg()
                .desc("")
                .build();
        commandlineOptions.addOption(ensembl);
        //Amount of threads to use.
        Option threads = Option.builder("threads")
                .hasArg()
                .desc("Amount of threads to use for multithreading. (Default 2)")
                .build();
        commandlineOptions.addOption(threads);
    }

    /**
     * Starts the Quality Control process and checks command line input.
     * @param args command line arguments.
     * @throws FileNotFoundException file was not found/does not exist.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is
     * already opened by another program.
     * @throws InterruptedException process was interrupted.
     * @throws ExecutionException could not execute the call function.
     */
    private void startQualityControl(String[] args) throws ParseException, IOException,
            InterruptedException, ExecutionException {
        //Creates a new commandline parser.
        CommandLineParser parser = new BasicParser();
        //Adds allocates option values to variable.
        CommandLine cmd = parser.parse(commandlineOptions, args);
        //Help function.
        if (args[0].contains("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Quality Control", commandlineOptions );
        } else {            //Allocate command line input to variables.
            String proteinPeptideTextFile = cmd.getOptionValue("proteinPeptides");
            String filterDatabase = cmd.getOptionValue("publicDatabases");
            String databaseFile = cmd.getOptionValue("referenceDatabases");
            String outputDirectory = cmd.getOptionValue("output");
            Boolean removeEnsemblHits = true;
            if (cmd.getOptionValue("removeEnsemblHits").toLowerCase().matches("(true|y|t)")) {
                removeEnsemblHits = true;
            } else if (cmd.getOptionValue("removeEnsemblHits").toLowerCase().matches("(false|n|f)")) {
                removeEnsemblHits = false;
            } else {
                System.out.println("WARNING: invalid argument given to -removeEnsemlHits. Please check your input: " + cmd.getOptionValue("removeEnsemblHits"));
                System.out.println("Argument is automatically set to FALSE.");
            }
            //Set the amount of threads to be used.
            Integer threads = getThreads(cmd);
            //Determine path separator.
            String separator = getSeparator();
            InputTools inputCheck = new InputTools();
            inputCheck.isDirectory(outputDirectory);
            //Create a list of database entry files.
            EntryFileReader reader = new EntryFileReader();
            ArrayList<String> publicDatabaseList = reader.readMainTextFile(databaseFile);
            //Create a map of database files. Key is database name, value is an ArrayList of files.
            LinkedHashMap<String, ArrayList<String>> publicDatabaseMap = reader.createDatabaseHashMap(publicDatabaseList, separator);
            ArrayList<String> referenceDatabaseList = reader.readMainTextFile(filterDatabase);
            //Create a map of database files. Key is database name, value is an ArrayList of files.
            LinkedHashMap<String, ArrayList<String>> referenceDatabaseMap = reader.createDatabaseHashMap(referenceDatabaseList, separator);
            //Read input file
            if (inputCheck.isTxtFile(proteinPeptideTextFile)) {
                ArrayList<String> proteinPeptideFileList = reader.readMainTextFile(proteinPeptideTextFile);
                LinkedHashMap<String, ArrayList<String>> proteinPeptideFileMap = reader.createCsvHashMap(proteinPeptideFileList, separator);
                startProteinPeptideDatabaseIdentification(outputDirectory, proteinPeptideFileMap, publicDatabaseMap, referenceDatabaseMap, removeEnsemblHits, threads);
            } else {
                System.out.println("A provided input file was incorrect. Please check if " + proteinPeptideTextFile + " are existing text files.");
            }
        }
    }

    /**
     * Starts the quality control procedure.
     * Output is written to a .csv file depending on the dataset and RNASeq type.
     * @param outputPath outputpath for the matrix csv file.
     * @param proteinPeptideFileMap
     * @param publicDatabaseMap
     * @param referenceDatabaseMap
     * @param removeEnsemblHits
     * @param threads amount of threads.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is
     * already opened by another program.
     * @throws InterruptedException process was interrupted.
     * @throws ExecutionException could not execute the call function.
     */
    public final void startProteinPeptideDatabaseIdentification(String outputPath, final LinkedHashMap<String, ArrayList<String>> proteinPeptideFileMap,
            final LinkedHashMap<String, ArrayList<String>> referenceDatabaseMap, final LinkedHashMap<String, ArrayList<String>> publicDatabaseMap,
            final Boolean removeEnsemblHits, final Integer threads)
            throws IOException, InterruptedException, ExecutionException {
        System.out.println("Starting peptide database identification of PeptideShaker mzid data...");
        String separator = getSeparator();
        ArrayList<String> sampleList = new ArrayList<>();
        ProteinSequenceDatabaseMap proteinMap = new ProteinSequenceDatabaseMap();
        HashMap<String, ArrayList<ProteinCollection>> publicProteinCollectionMap = proteinMap.createProteinSequenceDatabaseMap(publicDatabaseMap);
        HashMap<String, ArrayList<ProteinCollection>> referenceProteinCollectionMap = proteinMap.createProteinSequenceDatabaseMap(referenceDatabaseMap);
        ProteinCollection publicProteinCollection = getPublicProteinDatabase(publicProteinCollectionMap);
        ArrayList<String> datasetKeys = new ArrayList<>();
        for (String rnaSeq : referenceDatabaseMap.keySet()) {
            datasetKeys.add(rnaSeq);
        }
        Integer sampleSize = 0;
        for (Map.Entry<String, ArrayList<String>> entry : proteinPeptideFileMap.entrySet()) {
            if (sampleSize <= entry.getValue().size()) {
                sampleSize = entry.getValue().size();
            }
        }
        ProteinPeptideCollection finalProteinPeptideCollection = new ProteinPeptideCollection();
        //Go through index of datasets.
        for (int currentIndex = 0; currentIndex < datasetKeys.size(); currentIndex++) {
            String datasetName = datasetKeys.get(currentIndex);
            //Get dataset name for file output purpose and getting correct database for each sample.
            for (Integer currentSample = 0; currentSample < sampleSize; currentSample++) {
                //Get correct sample.
                ArrayList<String> proteinPeptideFiles = proteinPeptideFileMap.get(datasetName);
                for (String file: proteinPeptideFiles) {
                    String[] folders = file.split(separator);
                    String sampleFile = folders[folders.length-2];
                    if (!sampleList.contains(sampleFile)) {
                        sampleList.add(sampleFile);
                    }
                }
                //Read and process protein-peptide file.
                ProteinPeptideFileReader reader = new ProteinPeptideFileReader();
                ProteinPeptideCollection proteinPeptideCollection = reader.createCollection(proteinPeptideFiles.get(currentSample), datasetName, currentSample, removeEnsemblHits);
                //Match to public database to remove known sequences.
                PublicDatabaseMatcher proteinPeptideMatcher = new PublicDatabaseMatcher(null, null);
                ProteinPeptideCollection filteredProteinPeptideCollection = proteinPeptideMatcher.getMatchedProteinPeptides(proteinPeptideCollection, publicProteinCollection, threads);
                //Matches protein-peptide data to sample database to ensure correct hits and to flag uniqueness.
                for (Entry<String, ArrayList<ProteinCollection>> datasetEntry: referenceProteinCollectionMap.entrySet()) {
                    if (datasetEntry.getKey().contains(datasetName)) {
                        ProteinCollection referenceProteinCollection = datasetEntry.getValue().get(currentSample);
                        ReferenceDatabaseMatcher referenceDatabaseMatcher = new ReferenceDatabaseMatcher(null, null);
                        ProteinPeptideCollection referenceProteinPeptideCollection = referenceDatabaseMatcher.getMatchedProteinPeptides(filteredProteinPeptideCollection, referenceProteinCollection, threads);
                        finalProteinPeptideCollection.getProteinPeptideMatches().addAll(referenceProteinPeptideCollection.getProteinPeptideMatches());
                    }
                }
            }
            //Create output file, ensures that duplicate is not overwritten.
            String finalFilePath = outputPath + datasetName + "_Comparison_By_Sequence_ProteinGroup.csv";
            File file = new File(finalFilePath);
            while (file.exists()) {
                Integer count = 1;
                finalFilePath = outputPath + datasetName + "_Comparison_By_Sequence_ProteinGroup(" + count + ").csv";
                file = new File(finalFilePath);
                count++;
            }
            PeptideMatrix peptideMatrix = new PeptideMatrix();
            MatrixToCsvWriter write = new MatrixToCsvWriter();
            //Create matrices and write them to given output directory.
            MatrixEntryCollection basedOnProteinGroupMatrixEntryCollection = peptideMatrix.createPeptideMatrixBasedOnProteinGroup(finalProteinPeptideCollection, sampleSize);
            write.writeDatasetCsv(basedOnProteinGroupMatrixEntryCollection, sampleList, finalFilePath);
            //Create output file, ensures that duplicate is not overwritten.
            finalFilePath = outputPath + datasetName + "_Comparison_By_Sequence.csv";
            file = new File(finalFilePath);
            while (file.exists()) {
                Integer count = 1;
                finalFilePath = outputPath + datasetName + "_Comparison_By_Sequence(" + count + ").csv";
                file = new File(finalFilePath);
                count++;
            }
            MatrixEntryCollection basedOnSequenceMatrixEntryCollection = peptideMatrix.createPeptideMatrixBasedOnSequence(finalProteinPeptideCollection, sampleSize);
            write.writeDatasetCsv(basedOnSequenceMatrixEntryCollection, sampleList, finalFilePath);
        }
    }

    
    /**
     * Returns the amount of threads used for multithreading.
     *
     * @param cmd commandline arguments.
     * @return amount of threads as Integer.
     */
    private Integer getThreads(CommandLine cmd) {
        Integer threads = 1;
        if (cmd.hasOption("threads")) {
            try {
                threads = Integer.parseInt(cmd.getOptionValue("threads"));
            } catch (Exception e) {
                System.out.println("Please enter a number as input instead of " + cmd.getOptionValue("threads")
                        + ".\nCurrent input results in error: " + e.getMessage());
            }
        }
        return threads;
    }

    /**
     * Returns the folder separator based on the system environment.
     * 
     * @return separator as String.
     */
    private String getSeparator() {
        String os = System.getProperties().getProperty("os.name").toLowerCase();
        String separator = "";
        if (os.contains("windows")) {
            separator = "\\\\"; //windows
        } else if (os.contains("linux") || os.contains("unix") || os.contains("macos")) {
            separator = "/"; //linux and MacOS
        }
        return separator;
    }

    /**
     * Gets the correct public database.
     * 
     * @param proteinDataMap map of protein collections.
     * @return returns a combined collection of proteins from each given public protein database.
     */
    private ProteinCollection getPublicProteinDatabase(final HashMap<String, ArrayList<ProteinCollection>> proteinDataMap) {
        ProteinCollection proteinCollection = new ProteinCollection();
        for (Map.Entry<String, ArrayList<ProteinCollection>>mapEntry : proteinDataMap.entrySet()) {
            for (ProteinCollection collection: mapEntry.getValue()) {
                //Match key to the current index of the size. -1 for single database files.
                proteinCollection.getProteins().addAll(collection.getProteins());
            }
        }
  
        return proteinCollection;
    }
}
