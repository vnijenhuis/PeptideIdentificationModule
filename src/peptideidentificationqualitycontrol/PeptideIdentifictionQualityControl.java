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
import matrix.CsvMatrixCreator;
import matrix.CsvWriter;
import collectioncreator.PeptideCollectionCreator;
import matcher.DatabaseMatcher;
import matcher.PeptideToProteinPeptideMatcher;
import collectioncreator.ProteinCollectionCreator;
import collectioncreator.ProteinPeptideCollectionCreator;
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
    private ArrayList<String> indivDbFiles;

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
    private final CsvMatrixCreator createMatrix;
    
    /**
     * Sets the matrix count/coverage values.
     */
    private final SetMatrixValues matrix;
    
    /**
     * List of databases such as uniprot.
     */
    private ArrayList<String> databases;
    
    /**
     * Collection of protein objects created from the databases array.
     */
    private ProteinCollection database;
    
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
    private ProteinCollection individualDatabase;
    
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
        Option path = Option.builder("p")
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
                .desc("Path to the database folder (/home/name/Databases/)"
                        + "\n Path should contain folders with .fasta.gz files.")
                .build();
        options.addOption(dbPath);
        //A string that is present in the database(s). (eg. fasta.gz reads all fasta.gz files, uniprot reads the uniprot db file.
        Option dbName= Option.builder("dbn")
                .hasArg()
                .desc("Database file name, or a part of that name. (uniprot)")
                .build();
        options.addOption(dbName);
        Option individualDB = Option.builder("idb")
                .hasArg()
                .desc("Individual database file name. (proteins.fasta)")
                .build();
        options.addOption(individualDB);
        Option output = Option.builder("op")
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
        createMatrix = new CsvMatrixCreator();
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
        indivDbFiles = new ArrayList<>();
        databases = new ArrayList<>();
        Integer sampleSize = 0;
        if (args[0].contains("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Quality Control", options );
        } else {
            //Allocate command line input to variables.
            String[] path = cmd.getOptionValues("p");
            String psmFile = cmd.getOptionValue("psm");
            String proteinPeptideFile = cmd.getOptionValue("pp");
            String databasePath = cmd.getOptionValue("db");
            String dbName = cmd.getOptionValue("dbn");
            String indivDbFile = cmd.getOptionValue("idb");
            String outputPath = cmd.getOptionValue("op");
            //Check output path.
            File checkPath = new File(outputPath);
            if (!checkPath.isDirectory()) {
                throw new IllegalArgumentException("Paramter -o requires a valid path "
                    + "to write data to. \nYou provided an invalid path:" + checkPath);
            }
            //Creates lists of the given files.
            for (String folder: path) {
                SampleSizeGenerator sizeGenerator = new SampleSizeGenerator();
                Integer size = sizeGenerator.getSamples(folder);
                psmFiles = input.checkFileValidity(folder, psmFile, psmFiles);
                proPepFiles = input.checkFileValidity(folder, proteinPeptideFile, proPepFiles);
                indivDbFiles = input.checkFileValidity(folder, indivDbFile, indivDbFiles);
                if (size > sampleSize) {
                    sampleSize = size;
                }
            }
            //usually uniprot.
            databases = input.checkFileValidity(databasePath, dbName, databases);
            PeptideQualityControl(psmFiles, proPepFiles, indivDbFiles, databases, outputPath, sampleSize);
        }
    }

    /**
     * Starts the quality control procedure.
     * Output is written to a .csv file depending on the dataset and RNASeq type.
     * @param psmFiles DB seach psm.csv file of each sample.
     * @param proPepFiles protein-peptides.csv file of each sample
     * @param indivDbFiles individual database files of each sample.
     * @param databases protein database(s) such as uniprot.
     * @param outputPath outputpath for the matrix csv file.
     * @param sampleSize size of the samples. (10x COPD and 9x Healthy = sample size of 20 (healthy 10 is empty)
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is 
     * already opened by another program.
     */
    public final void PeptideQualityControl(ArrayList<String> psmFiles, final ArrayList<String> proPepFiles,
            final ArrayList<String> indivDbFiles, final ArrayList<String> databases, final String outputPath,
            final Integer sampleSize)  throws IOException {
        //Gets the separator for files of the current system.
        String pattern = Pattern.quote(File.separator);
        ArrayList<String> datasets = new ArrayList<>();
        ProteinPeptideCollection finalCollection = new ProteinPeptideCollection();
        String rnaSeq = "";
        //Creates a database collection
        database = new ProteinCollection();
        database = databaseCollection.createCollection(databases, database);
        //Creates a database of the combined individual proteins.
        combinedDatabase = new ProteinCollection();
        combinedDatabase = databaseCollection.createCollection(indivDbFiles, combinedDatabase);
        for (int sample = 0; sample < psmFiles.size(); sample++) {
            String[] path = psmFiles.get(sample).split(pattern);
            String dataset = "";
            for (String folder : path) {
                if (folder.toUpperCase().contains("2D") || folder.toUpperCase().contains("1D")) {
                    dataset = folder;
                }
                //RNAseq name: usually can change between RNASeqUnique and RNASeqCommon
                if (folder.toUpperCase().contains("RNASEQ")) {
                    rnaSeq = folder;
                }
            }
            proteinPeptides = new ProteinPeptideCollection();
            //Loads unique peptide sequences from DB search psm.csv.
            peptides = peptideCollection.createCollection(psmFiles.get(sample));
            //Match to uniprot. This removes peptides that match in a database sequence.
            peptides = databaseMatcher.matchToDatabases(database, peptides);
            //Creates protein peptide collection from protein-peptides.csv.
            proteinPeptides = proteinPeptideCollection.createCollection(proPepFiles.get(sample));
            //Matches peptides to protein-peptide relationship data.
            proteinPeptides = proteinPeptideMatching.matchPeptides(peptides, proteinPeptides);
            //Match to the combined individual database. Flags sequences that occur once inside this database.
            proteinPeptides = combinedDatabaseMatcher.matchToIndividuals(proteinPeptides, combinedDatabase);
            //Creates an array with the individual proteins.fasta file for individual db matching.
            ArrayList<String> individualFile = new ArrayList<>();
            individualFile.add(indivDbFiles.get(sample));
            //Match to the individual database. Flags sequences that occur once inside this database.
            individualDatabase = new ProteinCollection();
            individualDatabase = databaseCollection.createCollection(individualFile, individualDatabase);
            proteinPeptides = individualDatabaseMatcher.matchToIndividual(proteinPeptides, individualDatabase);
            //Adds all proteinPeptides to a single collection.
            finalCollection.getProteinPeptideMatches().addAll(proteinPeptides.getProteinPeptideMatches());
            if (!datasets.contains(dataset)) {
                datasets.add(dataset);
            }
        }
        //Create a matrix of all final ProteinPeptide objects.
        proteinPeptideMatrix = new HashSet<>();
        proteinPeptideMatrix = createMatrix.createMatrix(finalCollection, sampleSize, datasets);
        //Writes count & coverage(-10lgP) values into the matrix.
        proteinPeptideMatrix = matrix.setValues(finalCollection, proteinPeptideMatrix, sampleSize, datasets);
        //Write data to a .csv file.
        fileWriter.generateCsvFile(proteinPeptideMatrix, outputPath, datasets, rnaSeq, sampleSize);
    }
}
