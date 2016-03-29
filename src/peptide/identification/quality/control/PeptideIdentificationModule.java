/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum matrix quality control  *
 */
package peptide.identification.quality.control;

import collections.PeptideCollection;
import collections.ProteinCollection;
import collections.ProteinPeptideCollection;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import matcher.CombinedIndividualDatabaseMatcher;
import matrix.UniqueMatrixRowCreator;
import matrix.CsvWriter;
import collection.creator.PeptideCollectionCreator;
import matcher.DatabaseMatcher;
import matcher.PeptideToProteinPeptideMatcher;
import collection.creator.ProteinCollectionCreator;
import collection.creator.ProteinPeptideCollectionCreator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import tools.SampleSizeGenerator;
import matrix.SetMatrixValues;
import matcher.IndividualDatabaseMatcher;
import matcher.MultiThreadDatabaseMatcher;
import tools.ValidFileChecker;

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
    private final Options options;

    /**
     * Checks input files.
     */
    private final ValidFileChecker input;

    /**
     * List of DB seach psm.csv files.
     */
    private ArrayList<String> psmFiles;

    /**
     * List of protein-peptide.csv files.
     */
    private ArrayList<String> proPepFiles;

    /**
     * List of individual database fasta files.
     */
    private ArrayList<String> fastaFiles;

    /**
     * Creates a PeptideCollection of the DB search psm.csv data.
     */
    private final PeptideCollectionCreator peptideCollection;

    /**
     * Collection of peptide objects.
     */
    private PeptideCollection peptides;

    /**
     * Creates a Protein object Collection of a database.
     */
    private final ProteinCollectionCreator databaseCollection;

    /**
     * Creates a ProteinPeptide object collection of protein-peptides.csv.
     */
    private final ProteinPeptideCollectionCreator proteinPeptideCollection;

    /**
     * Collection of ProteinPeptide objects.
     */
    private ProteinPeptideCollection proteinPeptides;

    /**
     * Matches Peptide objects to ProteinPeptide objects.
     */
    private final PeptideToProteinPeptideMatcher proteinPeptideMatching;

    /**
     * Matches peptides to a protein database.
     */
    private final DatabaseMatcher databaseMatcher;

    /**
     * Writes data to a .csv file.
     */
    private final CsvWriter fileWriter;

    /**
     * Creates a set of arrays as matrix.
     */
    private final  UniqueMatrixRowCreator createMatrix;

    /**
     * Sets the matrix count/coverage values.
     */
    private final SetMatrixValues matrix;

    /**
     * List of databases such as uniprot.
     */
    private String database;

    /**
     * Collection of protein objects created from the databases array.
     */
    private ProteinCollection proteinDatabase;

    /**
     * Collection of protein objects created from the combined database array.
     */
    private ProteinCollection combinedDatabase;

    /**
     * Matches collection of peptides of a sample to the collection of individual proteins of the same sample.
     */
    private final IndividualDatabaseMatcher individualDatabaseMatcher;

    /**
     * Matches collection of peptides to the collection of combined individual proteins.
     */
    private final CombinedIndividualDatabaseMatcher combinedDatabaseMatcher;

    /**
     * Set of arrays containing protein-peptide relationship data.
     */
    private HashSet<ArrayList<String>> proteinPeptideMatrix;

    /**
     * Collection of protein objects of the individual database of a sample.
     */
    private ProteinCollection fastaDatabase;

    /**
     * List of combined databases.
     */
    private String combDatabase;

    /**
     * Matches peptides to the protein database using multi-threading.
     */
    private MultiThreadDatabaseMatcher multithreadMatcher;

    /**
     * Private constructor to define primary functions.
     * Defines command line argument options.
     * Calls classes and functions to be used with this module.
     */
    private PeptideIdentificationModule() {
        //Creates all commandline options and their descriptions.
        //Help function.
        options = new Options();
        Option help = Option.builder("help")
                .desc("Help function to display all options.")
                .optionalArg(true)
                .build();
        options.addOption(help);
        //Path(s) to the dataset(s)
        Option path = Option.builder("in")
                .hasArgs()
                .desc("Path to the dataset (/home/name/1D25/commonRNAseq/)."
                        + "\n Path should contain folders with sample names. (COPD1 etc.)")
                .build();
        options.addOption(path);
        //protein-peptide relations file name.
        Option proteinPeptide = Option.builder("pp")
                .hasArg()
                .desc("Name of the protein-peptide file (protein-peptides.csv).")
                .build();
        options.addOption(proteinPeptide);
        //PSM file name.
        Option psm = Option.builder("psm")
                .hasArg()
                .desc("Name of the psm file (DB search PSM.csv).")
                .build();
        options.addOption(psm);
        //Path to the database(s). Should contain folders per database. (uniprot/ensemble etc.)
        Option dbPath = Option.builder("db")
                .hasArg()
                .desc("Path to the database folder (/home/name/Databases/uniprot.fasta.gz)")
                .build();
        options.addOption(dbPath);
        //A string that is present in the database(s). (eg. fasta.gz reads all fasta.gz files, uniprot reads the uniprot db file.
        Option dbName = Option.builder("cdb")
                .hasArg()
                .desc("Path and name of the combined database fasta. (/home/name/Fastsa/COPD-19-DB.fa)")
                .build();
        options.addOption(dbName);
        //Path to the fasta files.
        Option individualDB = Option.builder("idb")
                .hasArg()
                .desc("Path to the individual fasta files. (/home/name/Fastas/)\nFastas folder should contain files.")
                .build();
        options.addOption(individualDB);
        //Path to put the output file to.
        Option output = Option.builder("out")
                .hasArg()
                .desc("Path to the folder to create the output file.")
                .build();
        options.addOption(output);
        //Add sample names.
        Option target = Option.builder("target")
                .hasArg()
                .desc("Give the name of used samples. Currently only supports 2 arguments. (CASE SENSITIVE!)")
                .build();
        options.addOption(target);
        Option control = Option.builder("control")
                .hasArg()
                .desc("Give the name of used samples. Currently only supports 2 arguments. (CASE SENSITIVE!)")
                .build();
        options.addOption(control);
        //Amount of threads to use.
        Option thread = Option.builder("threads")
                .hasArg()
                .optionalArg(true)
                .desc("Amount of threads to use for multithreading. (Default 2)")
                .build();
        options.addOption(thread);
        //Checks files.
        input = new ValidFileChecker();
        //Creates peptide collections.
        peptideCollection = new PeptideCollectionCreator();
        //Creates protein collections.
        databaseCollection = new ProteinCollectionCreator();
        //Creates protein-peptide matching collections.
        proteinPeptideCollection = new ProteinPeptideCollectionCreator();
        //Flags uniqueCombined column of each protein-peptide object.
        proteinPeptideMatching = new PeptideToProteinPeptideMatcher();
        //creates a collection of protein-peptide objects that are not matched to a database(uniprot) protein sequence.
        databaseMatcher = new DatabaseMatcher();
        //matches the remaining protein-peptide objects to the combined individual database.
        combinedDatabaseMatcher = new CombinedIndividualDatabaseMatcher();
        //matches the remaining protein-peptide objects to the individual database.
        individualDatabaseMatcher = new IndividualDatabaseMatcher();
        //Creates a hashset of arrays as matrix.
        createMatrix = new UniqueMatrixRowCreator();
        //Sets the values per matrix.
        matrix = new SetMatrixValues();
        //Writes data to file.
        fileWriter = new CsvWriter();
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
        CommandLine cmd = parser.parse(options, args);
        //Creates multiple new lists.
        ArrayList<String> sampleList = new ArrayList<>();
        psmFiles = new ArrayList<>();
        proPepFiles = new ArrayList<>();
        fastaFiles = new ArrayList<>();
        //Help function.
        if (args[0].contains("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Quality Control", options );
        } else {
            //Allocate command line input to variables.
            String[] path = cmd.getOptionValues("in");
            String psmFile = cmd.getOptionValue("psm");
            String proteinPeptideFile = cmd.getOptionValue("pp");
            database = cmd.getOptionValue("db");
            combDatabase = cmd.getOptionValue("cdb");
            String fastas = cmd.getOptionValue("idb");
            String outputPath = cmd.getOptionValue("out");
            //Check if sample names are given.
            //Allocate amount of threads to use for multithreading.
            String thread = "";
            Integer threads = 1;
            if (cmd.hasOption("threads")) {
                thread = cmd.getOptionValue("threads"); 
                if (thread.matches("^[0-9]{1,}$")) {
                    threads = Integer.parseInt(thread);
                }
            }
            //Check if output path exists.
            String output = outputPath.substring(0, outputPath.lastIndexOf(File.separator));
            input.isDirectory(output);
            input.isDirectory(fastas);
            input.isFasta(database);
            input.isFasta(combDatabase);
            //Creates lists of the given files.
            Integer targetSampleSize = 0;
            Integer controlSampleSize = 0;
            //Add files to lists according to the given folder and file name.
            for (String folder: path) {
                input.isDirectory(folder);
                //Creates a list of peptide psm files.
                psmFiles = input.checkFileValidity(folder, psmFile, psmFiles);
                System.out.println(psmFiles);
                //Creates a list of protein-peptide files.
                proPepFiles = input.checkFileValidity(folder, proteinPeptideFile, proPepFiles);
                for (String file: proPepFiles) {
                    String sample = file.split("\\\\")[4];
                    sample = sample.replaceAll("\\d", "");
                    if (!sampleList.contains(sample)) {
                        sampleList.add(sample);
                    }
                }
                //Gets highest healthy sample size
                SampleSizeGenerator sizeGenerator = new SampleSizeGenerator();
                ArrayList<Integer> sampleSize = sizeGenerator.getSamples(folder, sampleList);
                if (sampleSize.get(0) > controlSampleSize) {
                    controlSampleSize = sampleSize.get(0);
                }
                //Gets the highest copd sample size.
                if (sampleSize.get(1) > targetSampleSize) {
                    targetSampleSize = sampleSize.get(1);
                }
            }
            fastaFiles = input.getFastaDatabaseFiles(fastas, fastaFiles, sampleList);
            //Checks if both a target (COPD) and a control sample name has been given.
            //Starts peptide identification
            StartPeptideIdentification(outputPath, targetSampleSize, controlSampleSize, threads, sampleList);
        }
    }

    /**
     * Starts the quality control procedure.
     * Output is written to a .csv file depending on the dataset and RNASeq type.
     * @param outputPath outputpath for the matrix csv file.
     * @param controlSampleSize sample size of healthy/control samples.
     * @param targetSampleSize sample size of COPD samples.
     * @param threads amount of threads.
     * @param sampleList list of samples.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is
     * already opened by another program.
     * @throws InterruptedException process was interrupted.
     * @throws ExecutionException could not execute the call function.
     */
    public final void StartPeptideIdentification( final String outputPath, final Integer controlSampleSize,
            final Integer targetSampleSize, final Integer threads, final ArrayList<String> sampleList)
            throws IOException, InterruptedException, ExecutionException {
        //Gets the separator for files of the current system.
        String pattern = Pattern.quote(File.separator);
        ArrayList<String> datasets = new ArrayList<>();
        HashMap<String, Integer> datasetNumbers = new HashMap<>();
        ProteinPeptideCollection finalCollection = new ProteinPeptideCollection();
        //Creates a database collection
        proteinDatabase = new ProteinCollection();
        proteinDatabase = databaseCollection.createCollection(database, proteinDatabase);
        //Creates a database of the combined individual proteins.
        combinedDatabase = new ProteinCollection();
        combinedDatabase = databaseCollection.createCollection(combDatabase, combinedDatabase);
        int datasetCount = 0;
        //Loop through all sample files.
        for (int sample = 0; sample < psmFiles.size(); sample++) {
            String[] path = psmFiles.get(sample).split(pattern);
            ArrayList<String> sampleFiles = new ArrayList<>();
            //Determine dataset count for 1D and 2D.
            String dataset = path[path.length-4];
            Boolean newDataset = true;
            for (String folder : path) {
                if (!datasetNumbers.isEmpty()) {
                    for (Entry set : datasetNumbers.entrySet()) {
                        if (set.getKey().equals(dataset)) {
                            newDataset = false;
                        }
                    }
                    if (newDataset) {
                        datasets.add(dataset);
                        datasetCount += 1;
                        datasetNumbers.put(dataset, datasetCount);  
                    }
                } else {
                    datasets.add(dataset);
                    datasetCount += 1;
                    datasetNumbers.put(dataset, datasetCount);
                }
                //Gathers sample names to match to the individual database.fasta files.
                if (folder.matches("(" + sampleList.get(1) + ")_?\\d{1,}")) {
                    sampleFiles.add(folder);
                    sampleFiles.add(folder.subSequence(0, 4) + "_" + folder.substring(4));
                } else if (folder.matches("(" + sampleList.get(0) + ")_?\\d{1,}")) {
                    sampleFiles.add(folder);
                    sampleFiles.add(folder.subSequence(0, 7) + "_" + folder.substring(7));
                }
            }
            //Creates a string with a fasta file corresponding to the sample.
            String sampleFile = matchSample(sampleFiles);
            //Loads unique peptide sequences from DB search psm.csv.
            peptides = new PeptideCollection();
            peptides = peptideCollection.createCollection(psmFiles.get(sample), dataset);
            //Matches peptides without multi-threading.
//            peptides = databaseMatcher.matchToDatabases(proteinDatabase, peptides);
            //Matches peptides to uniprot (or other given database). Makes use of multithread.
            multithreadMatcher = new MultiThreadDatabaseMatcher(peptides, proteinDatabase);
            peptides = multithreadMatcher.getMatchedPeptides(peptides, proteinDatabase, threads);
            //Creates protein peptide collection from protein-peptides.csv.
            proteinPeptides = new ProteinPeptideCollection();
            proteinPeptides = proteinPeptideCollection.createCollection(proPepFiles.get(sample), dataset);
            //Matches peptides to protein-peptide relationship data.
            proteinPeptides = proteinPeptideMatching.matchPeptides(peptides, proteinPeptides);
            //Match to the combined individual database. Flags sequences that occur once inside this database.
            proteinPeptides = combinedDatabaseMatcher.matchToCombined(proteinPeptides, combinedDatabase);
            //Match to the fasta database. Flags sequences that occur once inside this database.
            fastaDatabase = new ProteinCollection();
            fastaDatabase = databaseCollection.createCollection(sampleFile, fastaDatabase);
            proteinPeptides = individualDatabaseMatcher.matchToIndividual(proteinPeptides, fastaDatabase);
            //Adds all proteinPeptides to a single collection.
            finalCollection.getProteinPeptideMatches().addAll(proteinPeptides.getProteinPeptideMatches());
        }
        //Deterime sample size and index.
        Integer sampleSize = 0;
        Integer sampleValueIndex = 0;
        if (targetSampleSize > controlSampleSize) {
            sampleSize = targetSampleSize*2;
            sampleValueIndex = targetSampleSize;
        } else {
            sampleSize = controlSampleSize*2;
            sampleValueIndex = controlSampleSize;
        }        
        //Create a matrix of all final ProteinPeptide objects.
        proteinPeptideMatrix = new HashSet<>();
        proteinPeptideMatrix = createMatrix.createMatrix(finalCollection, sampleSize, datasets, datasetNumbers, sampleList);
        //Writes count & coverage(-10lgP) values into the matrix.
        proteinPeptideMatrix = matrix.setValues(finalCollection, proteinPeptideMatrix, sampleValueIndex, datasets, datasetNumbers, sampleList);
        //Write data to a .csv file.
        fileWriter.generateCsvFile(proteinPeptideMatrix, outputPath, datasets, sampleList, sampleSize, datasetNumbers);
    }

    /**
     * Matches the sample name to the database fasta files.
     * @param sampleType name of the sample: COPD1/COPD_1/Control/Control_1.
     * @return matched database file.
     */
    private String matchSample(ArrayList<String> sampleType) {
        //Used to match sample and sample database.
        String data = "";
        Boolean isFasta = false;
        //List of individual database fasta files is matched to sample names.
        //If a name matched the peptides can matched to the database file
        for (String fasta: fastaFiles) {
            for (String sample: sampleType) {
                if (fasta.contains(sample)) {
                    data = fasta;
                    isFasta = true;
                    break;
                }
            }
        } if (!isFasta) {
            System.out.println("WARNING: the sample(s) in list " + sampleType + " has/have no matching fasta file.");
        }
        //If a name matched the peptides can matched to the database file
        //Otherwise a warning is displayed.
        return data;
    }
}
