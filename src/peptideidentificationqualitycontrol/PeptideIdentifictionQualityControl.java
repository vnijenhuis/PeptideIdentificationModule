/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum matrix quality control  *
 */
package peptideidentificationqualitycontrol;

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
import matrix.UniqueSequenceCreator;
import matrix.CsvWriter;
import collectioncreator.PeptideCollectionCreator;
import matcher.DatabaseMatcher;
import matcher.PeptideToProteinPeptideMatcher;
import collectioncreator.ProteinCollectionCreator;
import collectioncreator.ProteinPeptideCollectionCreator;
import java.util.HashMap;
import tools.SampleSizeGenerator;
import matrix.SetMatrixValues;
import matcher.IndividualDatabaseMatcher;
import tools.ValidFileChecker;

/**
 * A peptide identification quality control module.
 * Checks the quality of peptide mass spectrum output of COPD and Control samples.
 * Accounts for uniqueness to an individual database group, removes sequences known by uniprot,
 * and counts the occurrences of each peptide per sample.
 * @author vnijenhuis
 */
public class PeptideIdentifictionQualityControl {
    /**
     * @param args the command line arguments.
     * @throws org.apache.commons.cli.ParseException exception encountered while processing
     * command line options. Please check the input.
     * 
     * @throws java.io.IOException could not open or find the specified file or directory.
     */
    public static void main(String[] args) throws ParseException, IOException {
        PeptideIdentifictionQualityControl peptideIdentification = new PeptideIdentifictionQualityControl();
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
    private final UniqueSequenceCreator createMatrix;

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
     * Private constructor to define primary functions.
     * Defines command line argument options.
     * Calls classes and functions to be used with this module.
     */
    private PeptideIdentifictionQualityControl() {
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
        Option dbName= Option.builder("cdb")
                .hasArg()
                .desc("Path and name of the combined database fasta. (/home/name/Fastsa/COPD-19-DB.fa)")
                .build();
        options.addOption(dbName);
        //Path to the fasta files.
        Option individualDB = Option.builder("fasta")
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
        createMatrix = new UniqueSequenceCreator();
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
     */
    private void startQualityControl(String[] args) throws ParseException, IOException {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);
        psmFiles = new ArrayList<>();
        proPepFiles = new ArrayList<>();
        fastaFiles = new ArrayList<>();
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
            String fastas = cmd.getOptionValue("fasta");
            String outputPath = cmd.getOptionValue("out");
            //Check if files/directories exist.
            String output = outputPath.substring(0, outputPath.lastIndexOf(File.separator));
            input.isDirectory(fastas);
            input.isDirectory(output);
            input.isFile(database);
            input.isFile(combDatabase);
            //Creates lists of the given files.
            Integer copdSampleSize = 0;
            Integer healthySampleSize = 0;
            fastaFiles = input.getFastaDatabaseFiles(fastas, fastaFiles);
            for (String folder: path) {
                input.isDirectory(folder);
                SampleSizeGenerator sizeGenerator = new SampleSizeGenerator();
                ArrayList<Integer> sampleSize = sizeGenerator.getSamples(folder);
                //Creates a list of peptide psm files.
                psmFiles = input.checkFileValidity(folder, psmFile, psmFiles);
                //Creates a list of protein-peptide files.
                proPepFiles = input.checkFileValidity(folder, proteinPeptideFile, proPepFiles);
                //Gets highest healthy sample size
                if (sampleSize.get(0) > healthySampleSize) {
                    healthySampleSize = sampleSize.get(0);
                }
                //Gets the highest copd sample size.
                if (sampleSize.get(1) > copdSampleSize) {
                    copdSampleSize = sampleSize.get(1);
                }
            }
            PeptideQualityControl(outputPath, copdSampleSize, healthySampleSize);
        }
    }

    /**
     * Starts the quality control procedure.
     * Output is written to a .csv file depending on the dataset and RNASeq type.
     * @param outputPath outputpath for the matrix csv file.
     * @param healthySampleSize sampleSize of healthy samples.
     * @param copdSampleSize sampleSize of COPD samples.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is
     * already opened by another program.
     */
    public final void PeptideQualityControl( final String outputPath,
            final Integer healthySampleSize, final Integer copdSampleSize)  throws IOException {
        //Gets the separator for files of the current system.
        String pattern = Pattern.quote(File.separator);
        ArrayList<String> datasets = new ArrayList<>();
        ArrayList<String> samples = new ArrayList<>();
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
            String dataset = "";
            ArrayList<String> sampleFiles = new ArrayList<>();
            //Determine dataset count for 1D and 2D.
            for (String folder : path) {
                if (folder.toUpperCase().contains("2D") || folder.toUpperCase().contains("1D")) {
                    dataset = folder;
                    if (!datasets.contains(dataset)) {
                        datasets.add(dataset);
                        datasetCount += 1;
                        datasetNumbers.put(dataset, datasetCount);
                    }
                }
                //Gather sample names.
                if (folder.toUpperCase().matches("COPD_?\\d{1,}")) {
                    sampleFiles.add(folder);
                    sampleFiles.add(folder.subSequence(0, 4) + "_" + folder.substring(4));
                } else if (folder.matches("Healthy_?\\d{1,}")) {
                    sampleFiles.add(folder);
                    sampleFiles.add(folder.subSequence(0, 7) + "_" + folder.substring(7));
                }
            }
            //Creates a string with a fasta file corresponding to the sample.
            String sampleFile = matchSample(sampleFiles);
            proteinPeptides = new ProteinPeptideCollection();
            //Loads unique peptide sequences from DB search psm.csv.
            peptides = peptideCollection.createCollection(psmFiles.get(sample));
            //Match to uniprot. This removes peptides that match in a database sequence.
            peptides = databaseMatcher.matchToDatabases(proteinDatabase, peptides);
            //Creates protein peptide collection from protein-peptides.csv.
            proteinPeptides = proteinPeptideCollection.createCollection(proPepFiles.get(sample));
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
        Integer sampleSize = 0;
        Integer sampleValueIndex = 0;
        if (copdSampleSize > healthySampleSize) {
            sampleSize = copdSampleSize*2;
            sampleValueIndex = copdSampleSize;
        } else {
            sampleSize = healthySampleSize*2;
            sampleValueIndex = healthySampleSize;
        }
        samples.add("Healthy");
        samples.add("COPD");
        //Create a matrix of all final ProteinPeptide objects.
        proteinPeptideMatrix = new HashSet<>();
        proteinPeptideMatrix = createMatrix.createMatrix(finalCollection, sampleSize, datasets, datasetNumbers, samples);
        //Writes count & coverage(-10lgP) values into the matrix.
        proteinPeptideMatrix = matrix.setValues(finalCollection, proteinPeptideMatrix, sampleValueIndex, datasets, datasetNumbers, samples);
        //Write data to a .csv file.
        fileWriter.generateCsvFile(proteinPeptideMatrix, outputPath, datasets, samples, sampleSize);
    }

    /**
     * Matches the sample name to the database fasta files.
     * @param sampleType name of the sample COPD1/COPD_1/Healthy1/Healthy_1
     * @return matched database file.
     */
    private String matchSample(ArrayList<String> sampleType) {
        //Used to match sample and sample database.
        String data = "";
        Boolean isFasta = false;
        for (String fasta: fastaFiles) {
            for (String sample: sampleType) {
                if (fasta.contains(sample)) {
                    data = fasta;
                    isFasta = true;
                    break;
                }
            }
        } if (!isFasta) {
            System.out.println("WARNING: samples in list " + sampleType + " has no matching fasta file.");
        }
        return data;
    }
}
