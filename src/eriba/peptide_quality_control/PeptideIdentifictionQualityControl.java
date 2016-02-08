/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum matrix quality control  * 
 */
package eriba.peptide_quality_control;

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
import tools.CombinedIndividualDatabaseMatcher;
import tools.CsvMatrixCreator;
import tools.DataToCsvWriter;
import tools.PeptideCollectionCreator;
import tools.UniprotDatabaseMatcher;
import tools.PeptideToProteinPeptideMatcher;
import tools.ProteinCollectionCreator;
import tools.ProteinPeptideCollectionCreator;
import tools.SampleSizeGenerator;
import tools.SetMatrixValues;
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
     * List of database fasta files (uniprot, ensemble etc).
     */
    private ArrayList<String> dbFiles;

    /**
     * Creates a PeptideCollection of the DB search psm.csv data.
     */
    private final PeptideCollectionCreator peptideCollection;
    private PeptideCollection peptides;
    private final ProteinCollectionCreator databaseCollection;
    private ProteinCollection proteins;
    private final ProteinPeptideCollectionCreator proteinPeptideCollection;
    private ProteinPeptideCollection proteinPeptides;
    private final PeptideToProteinPeptideMatcher proteinPeptideMatching;
    private final UniprotDatabaseMatcher databaseMatcher;
    private final DataToCsvWriter fileWriter;
    private final CsvMatrixCreator createMatrix;
    private final SetMatrixValues matrix;
    private ArrayList<String> databases;
    private ProteinCollection database;
    private HashSet<ProteinCollection> combinedProteins;
    private HashSet<ProteinCollection> databaseProteins;
    private ProteinCollection combinedDatabase;
    private final CombinedIndividualDatabaseMatcher individualDatabaseMatcher;
    private HashSet<ArrayList<String>> proteinPeptideMatrix;
    
    /**
     * Private constructor to define primary functions.
     * Defines command line argument options.
     * Calls classes and functions to be used with this module.
     */
    private PeptideIdentifictionQualityControl() {
        options = new Options();
        Option help = Option.builder("help")
                .desc("Help function")
                .optionalArg(true)
                .build();
        options.addOption(help);
        Option path = Option.builder("path")
                .hasArgs()
                .desc("Path to the dataset (map with COPD/Healthy samples).")
                .build();
        options.addOption(path);
        Option proteinPeptide = Option.builder("pp")
                .hasArg()
                .desc("Name of the psm file.")
                .build();
        options.addOption(proteinPeptide);
        Option psm = Option.builder("psm")
                .hasArg()
                .desc("Name of the psm file.")
                .build();
        options.addOption(psm);
        Option dbPath = Option.builder("db")
                .hasArg()
                .desc("Path to the uniprot database.")
                .build();
        options.addOption(dbPath);
        Option dbName= Option.builder("dbn")
                .hasArg()
                .desc("Individual database file name.")
                .build();
        options.addOption(dbName);
        Option individualDB = Option.builder("idb")
                .hasArg()
                .desc("Individual database file name.")
                .build();
        options.addOption(individualDB);
        Option output = Option.builder("op")
                .hasArg()
                .desc("Individual database file name.")
                .build();
        options.addOption(output);
        Option rnaSeq = Option.builder("on")
                .hasArg()
                .desc("RNASeqCommon or RNAseqUnique name for output file.")
                .build();
        options.addOption(rnaSeq);
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
        databaseMatcher = new UniprotDatabaseMatcher();
        //
        individualDatabaseMatcher = new CombinedIndividualDatabaseMatcher();
        //Creates a hashset of arrays as matrix.
        createMatrix = new CsvMatrixCreator();
        //Sets the values per matrix.
        matrix = new SetMatrixValues();
        //Writes data to file.
        fileWriter = new DataToCsvWriter();
    }

    /**
     * Starts the Quality Control process and checks command line input.
     * @param args command line arguments.
     * @throws ParseException encountered an exception in the commandline arguments.
     * @throws IOException could not open or find the specified file or directory.
     */
    private void startQualityControl(String[] args) throws ParseException, IOException {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);
        if (args[0].contains("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Quality Control", options );
        } else {
            //Allocate command line input to variables.
            String[] path = cmd.getOptionValues("path");
            String psmFile = cmd.getOptionValue("psm");
            String proPepFile = cmd.getOptionValue("pp");
            String databasePath = cmd.getOptionValue("db");
            String dbName = cmd.getOptionValue("dbn");
            String indivDbFile = cmd.getOptionValue("idb");
            String outputPath = cmd.getOptionValue("op");
            String rnaSeq = cmd.getOptionValue("on");
            //Check all files and paths.
            File checkPath = new File(outputPath);
            if (!checkPath.isDirectory()) {
                throw new IllegalArgumentException("Paramter -o requires a valid path "
                    + "to write data to. \nYou provided an invalid path:" + checkPath);
            }
            psmFiles = new ArrayList<>();
            proPepFiles = new ArrayList<>();
            indivDbFiles = new ArrayList<>();
            Integer sampleSize = 0;
            for (String folder: path) {
                SampleSizeGenerator sizeGenerator = new SampleSizeGenerator();
                Integer size = sizeGenerator.getSamples(folder);
                psmFiles = input.checkFileValidity(folder, psmFile, psmFiles);
                proPepFiles = input.checkFileValidity(folder, proPepFile, proPepFiles);
                indivDbFiles = input.checkFileValidity(folder, indivDbFile, indivDbFiles);
                if (size > sampleSize) {
                    sampleSize = size;
                }
            }
            //usually uniprot.
            databases = new ArrayList<>();
            databases = input.checkFileValidity(databasePath, dbName, databases);
            PeptideQualityControl(psmFiles, proPepFiles, indivDbFiles, databases, outputPath, rnaSeq, sampleSize);
        }
    }

    /**
     * Starts the quality control procedure.
     * @param psmFiles DB seach psm.csv file of each sample.
     * @param proPepFiles protein-peptides.csv file of each sample
     * @param indivDbFiles individual database files of each sample.
     * @param databases protein database(s) such as uniprot.
     * @param outputPath outputpath for the matrix csv file.
     * @param rnaSeq RNAseq name for output file.
     * @param sampleSize size of the samples. (10x COPD and 9x Healthy = sample size of 20 (healthy 10 is empty)
     * @throws IOException could not open/find the specified file or directory.
     */
    public final void PeptideQualityControl(ArrayList<String> psmFiles, final ArrayList<String> proPepFiles,
            final ArrayList<String> indivDbFiles, final ArrayList<String> databases, final String outputPath,
            final String rnaSeq, final Integer sampleSize)  throws IOException {
        //Gets the separator for files of the current system.
        String pattern = Pattern.quote(File.separator);
        ArrayList<String> datasets = new ArrayList<>();
        ProteinPeptideCollection finalCollection = new ProteinPeptideCollection();
        //Creates a uniprot (and possibly other) database collection.
        database = new ProteinCollection();
        database = databaseCollection.createCollection(databases, database);
        combinedDatabase = new ProteinCollection();
        combinedDatabase = databaseCollection.createCollection(indivDbFiles, combinedDatabase);
        for (int sample = 0; sample < psmFiles.size(); sample++) {
            String[] path = psmFiles.get(sample).split(pattern);
            String dataset = "";
            for (int i = 0; i < path.length; i++) {
                if (path[i].toUpperCase().contains("2D") || path[i].toUpperCase().contains("1D")) {
                    dataset = path[i];
                }
            }
            proteinPeptides = new ProteinPeptideCollection();
            //Loads unique peptide sequences from DB search psm.csv.
            peptides = peptideCollection.createCollection(psmFiles.get(sample));
            //Makes protein peptide objects, remove flag, add patient ID
            proteinPeptides = proteinPeptideCollection.createCollection(proPepFiles.get(sample));
            //Matches protein peptide to db peptides, keeps single matches and counts occurences etc.
            proteinPeptides = proteinPeptideMatching.matchPeptides(peptides, proteinPeptides);
            // Match to uniprot. This removes proteinPeptides that match in a database sequence.
            proteinPeptides = databaseMatcher.matchToDatabases(database, proteinPeptides);
            //Match to the individual database. Flags sequences that occur once inside this database.
            proteinPeptides = individualDatabaseMatcher.matchToIndividuals(proteinPeptides, combinedDatabase);
            finalCollection.getProteinPeptideMatches().addAll(proteinPeptides.getProteinPeptideMatches());
            if (!datasets.contains(dataset)) {
                datasets.add(dataset);
            }
        }
        //Create a matrix of all final ProteinPeptide objects.
        proteinPeptideMatrix = new HashSet<>();
        proteinPeptideMatrix = createMatrix.createMatrix(finalCollection, sampleSize, datasets);
        proteinPeptideMatrix = matrix.setValues(finalCollection, proteinPeptideMatrix, sampleSize, datasets);
        //Write data to unknown_dataset_peptide_matrix.csv.
        fileWriter.generateCsvFile(proteinPeptideMatrix, outputPath, datasets, rnaSeq, sampleSize);
    }
}
