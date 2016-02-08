/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum matrix quality control  * 
 */
package tools;

import collections.ProteinPeptideCollection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import objects.ProteinPeptide;

/**
 * Creates a protein-peptide collection.
 * @author vnijenhuis
 */
public class ProteinPeptideCollectionCreator {
    /**
     * Creates a collection of the protein-peptide.csv file;
     * @param file protein-peptide.csv file.
     * @return collection of ProteinPeptide objects.
     * @throws FileNotFoundException couldn't find the protein-peptide.csv file.
     * @throws IOException could not find or open the specified file or directory.
     */
    public final ProteinPeptideCollection createCollection(final String file) throws FileNotFoundException, IOException {
        ProteinPeptideCollection proteinPeptides = new ProteinPeptideCollection();
        //Creates dataset and patient names depending on the map names.
        String pattern = Pattern.quote(File.separator);
        String[] path = file.split(pattern);
        String sample = "";
        String dataset = "";
        for (int i = 0; i < path.length; i++) {
            if (path[i].toLowerCase().contains("copd") || path[i].toLowerCase().contains("healthy")) {
                sample = path[i];
            } else if (path[i].toUpperCase().contains("2D") || path[i].toUpperCase().contains("1D")) {
                dataset = path[i];
            }
        }
        System.out.println("Collecting protein-peptides from " + sample + " " + dataset + "...");
        // Load the file.
        FileReader fr = new FileReader(file);
        BufferedReader bffFr = new BufferedReader(fr);
        String line;
        Integer count = 1;
        String uniqueCombined = "";
        //Some indixes may vary, so a check is needed to find their position inside a file.
        int groupIndex = 0;
        int accessionIndex = 0;
        int peptideIndex = 0;
        int uniqueIndex = 0;
        int coverageIndex = 0;
        Boolean firstLine = true;
        //Read the file. Determine indices by using first line parameters.
        while ((line = bffFr.readLine()) != null) {
            if (firstLine) {
                String[] data = line.split(",");
                for (int i = 0; i < data.length; i++) {
                    if (data[i].toLowerCase().contains("protein group")) {
                        groupIndex = i;
                    } else if (data[i].toLowerCase().contains("accession")) {
                        accessionIndex = i;
                    } else if (data[i].toLowerCase().contains("peptide")) {
                        peptideIndex = i; 
                    } else if (data[i].toLowerCase().contains("unique")) {
                        uniqueIndex = i;
                    } else if (data[i].toLowerCase().contains("lgp")) {
                        coverageIndex = i;
                    }
                }
                firstLine = false;
                line = bffFr.readLine();
            }
            //Assign data to variables.
            String[] data = line.split(",");
            String proteinGroup = data[groupIndex];
            String accession = data[accessionIndex];
            String sequence = data[peptideIndex];
            //Remove first and last 2 indices.
            sequence = sequence.replaceAll("\\.[A-Z]$", "");
            sequence = sequence.replaceAll("^[A-Z]\\.", "");
            //Possibility to remove (+15.99) values from sequences
//            sequence = sequence.replaceAll("\\(\\+[0-9]+\\.[0-9]+\\)", "");
            String uniqueToGroup = data[uniqueIndex];
            String coverage = data[coverageIndex];
            boolean newPeptide = true;
            ProteinPeptide match = new ProteinPeptide(proteinGroup, accession, sequence, sample, uniqueToGroup,
                    uniqueCombined, dataset, count, coverage);
            //Creates a proteinPeptide object with data per sample.
            if (!proteinPeptides.getProteinPeptideMatches().isEmpty()) {
                for (ProteinPeptide proteinPeptide: proteinPeptides.getProteinPeptideMatches()) {
                    if (proteinPeptide.getSequence().equals(sequence)) {
                        newPeptide = false;
                        proteinPeptide.setCounter(count);
                        //Collects all protein groups per sequence for a sample.
                        if (!proteinPeptide.getProteinGroup().contains(proteinGroup)) {
                            proteinPeptide.setProteinGroup(proteinGroup);
                        }
                        //Collects all accession IDs per sequence for a sample.
                        if (!proteinPeptide.getAccession().contains(accession)) {
                            proteinPeptide.setAccession(accession);
                            if (sequence.equals("LVQDVSGPLR")) {
                            System.out.println(proteinPeptide.getAccession());
                            proteinPeptide.setAccession(accession);
                            System.out.println(proteinPeptide.getAccession());
                            }
                        }
                        //Collects all -10lgP scores per sequence for a sample.
                        if (!proteinPeptide.getCoverage().contains(coverage)) {
                            proteinPeptide.setCoverage(coverage);
                        }
                    }
                }
                //If no match was found: add new entry to collection.
                if (newPeptide) {
                    proteinPeptides.addProteinPeptideMatch(match);
                }
            } else {
                //Adds first match to the collection.
                proteinPeptides.addProteinPeptideMatch(match);
            }
        }
        System.out.println("Collected " + proteinPeptides.getProteinPeptideMatches().size() + " protein-peptides from " + file);
        return proteinPeptides;
    }
}
