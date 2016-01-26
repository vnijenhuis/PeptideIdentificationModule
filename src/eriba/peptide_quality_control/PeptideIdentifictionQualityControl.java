/*
 * @author vnijenhuis
 * @project peptide spectrum matrix quality control  * 
 * @copyrights vnijenhuis, Dr. P.I. Horvatovich  * 
 */
package eriba.peptide_quality_control;

import collections.PeptideCollection;
import collections.ProteinCollection;
import collections.ProteinPeptideCollection;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import objects.Protein;
import objects.ProteinPeptide;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import tools.DatabaseMatcher;
import tools.CsvMatrixCreator;
import tools.DataToCsvWriter;
import tools.PeptideCollectionCreator;
import tools.UniprotDatabaseMatcher;
import tools.PeptideToProteinPeptideMatcher;
import tools.ProteinCollectionCreator;
import tools.ProteinPeptideCollectionCreator;
import tools.SetMatrixValues;
import tools.ValidFileChecker;

/**
 *
 * @author f103013
 */
public class PeptideIdentifictionQualityControl {

    /**
     * @param args the command line arguments
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
    private final UniprotDatabaseMatcher sequenceMatcher;
    private final DataToCsvWriter fileWriter;
    private final CsvMatrixCreator createMatrix;
    private final SetMatrixValues matrix;
    private final DatabaseMatcher matchCombinedDatabase;
    private ArrayList<String> databases;
    private ProteinCollection database;
    private HashSet<ProteinCollection> combinedProteins;
    private HashSet<ProteinCollection> databaseProteins;
    private ProteinCollection combinedDatabase;
    
    /**
     * Private constructor to define primary functions.
     */
    private PeptideIdentifictionQualityControl() {
        options = new Options();
        Option help = Option.builder("help")
                .desc("Help function")
                .optionalArg(true)
                .build();
        options.addOption(help);
        Option path = Option.builder("path")
                .hasArg()
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
        Option individualDB = Option.builder("idb")
                .hasArg()
                .desc("Individual database file name.")
                .build();
        options.addOption(individualDB);
        Option output = Option.builder("o")
                .hasArg()
                .desc("Individual database file name.")
                .build();
        options.addOption(output);
        input = new ValidFileChecker();
        peptideCollection = new PeptideCollectionCreator();
        databaseCollection = new ProteinCollectionCreator();
        proteinPeptideCollection = new ProteinPeptideCollectionCreator();
        proteinPeptideMatching = new PeptideToProteinPeptideMatcher();
        sequenceMatcher = new UniprotDatabaseMatcher();
        createMatrix = new CsvMatrixCreator();
        matrix = new SetMatrixValues();
        matchCombinedDatabase = new DatabaseMatcher();
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
            String path = cmd.getOptionValue("path");
            String psmFile = cmd.getOptionValue("psm");
            String proPepFile = cmd.getOptionValue("pp");
            String databasePath = cmd.getOptionValue("db");
            String indivDbFile = cmd.getOptionValue("idb");
            String outputPath = cmd.getOptionValue("o");
            File checkPath = new File(outputPath);
            if (!checkPath.isDirectory()) {
            throw new IllegalArgumentException("Paramter -o requires a valid path "
                    + "to write data to. \nYou provided an invalid path:" + checkPath);
            }
            psmFiles = input.checkFileValidity(path, psmFile);
            proPepFiles = input.checkFileValidity(path, proPepFile);
            indivDbFiles = input.checkFileValidity(path, indivDbFile);
            System.out.println(databasePath);
            databases = input.checkFileValidity(databasePath, "uniprot");
            System.out.println(databases);
            PeptideQualityControl(psmFiles, proPepFiles, indivDbFiles, databases, outputPath);
        }
    }

    /**
     * Starts the quality control procedure.
     * @param psmFiles DB seach psm.csv file of each sample.
     * @param proPepFiles protein-peptides.csv file of each sample
     * @param indivDbFiles individual database files of each sample.
     * @param databases protein database(s) such as uniprot.
     * @param outputPath outputpath for the matrix csv file.
     * @throws IOException could not open/find the specified file or directory.
     */
    public final void PeptideQualityControl(ArrayList<String> psmFiles, final ArrayList<String> proPepFiles,
            final ArrayList<String> indivDbFiles, final ArrayList<String> databases, final String outputPath)  throws IOException {
//        Integer sampleSize = psmFiles.size();
        Integer sampleSize = 1;
        String[] path = psmFiles.get(1).split("\\\\");
        String dataSet = path[path.length-4];
        HashSet<ArrayList<String>> peptideMatrix = new HashSet<>();
        //Creates a uniprot (and possibly other) database collection.
        database = new ProteinCollection();
        database = databaseCollection.createCollection(databases, database);
        combinedDatabase = new ProteinCollection();
        combinedDatabase = databaseCollection.createCollection(indivDbFiles, combinedDatabase);
        for (int sample = 0; sample < sampleSize; sample++) {
            String[] samplePath = psmFiles.get(sample).split("\\\\");
            String patient = samplePath[samplePath.length-2];
            peptides = peptideCollection.createCollection(psmFiles.get(sample));
            //Makes protein peptide objects, remove flag, add patient ID
            proteinPeptides = proteinPeptideCollection.createCollection(proPepFiles.get(sample));
            System.out.println(proteinPeptides.getPeptideMatches());
            //Matches protein peptide to db peptides, keeps single matches and counts occurences etc.
//            proteinPeptides = proteinPeptideMatching.matchPeptides(peptides, proteinPeptides);
//            System.out.println(proteinPeptides.getPeptideMatches());
//            // Match to uniprot. This removes proteinPeptides that match in a database sequence.
//            proteinPeptides = sequenceMatcher.matchProteinPeptides(database, proteinPeptides);
//            System.out.println(proteinPeptides.getPeptideMatches());
//            //Match to the individual database. Flags sequences that occur once inside this database.
//            proteinPeptides = sequenceMatcher.matchProteinPeptides(combinedDatabase, proteinPeptides);
//            //Creates a matrix.
//            createMatrix.createMatrix(proteinPeptides, peptideMatrix, sampleSize);
//            matrix.setValues(proteinPeptides, peptideMatrix, patient, sampleSize);
        }
//        peptideMatrix = matchCombinedDatabase.matchToDatabase(peptideMatrix, databaseProteins);
//        peptideMatrix = matchCombinedDatabase.matchToDatabase(peptideMatrix, combinedProteins);
//        fileWriter.generateCsvFile(peptideMatrix, outputPath, dataSet, sampleSize);
    }
}
