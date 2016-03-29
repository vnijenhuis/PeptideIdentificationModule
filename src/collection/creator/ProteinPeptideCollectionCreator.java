/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package collection.creator;

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
     * @param dataset name of the dataset.
     * @return collection of ProteinPeptide objects.
     * @throws FileNotFoundException file was not found/does not exist.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is
     * already opened by another program.
     */
    public final ProteinPeptideCollection createCollection(final String file, final String dataset)
            throws FileNotFoundException, IOException {
        ProteinPeptideCollection proteinPeptides = new ProteinPeptideCollection();
        //Creates dataset and patient names depending on the map names.
        String pattern = Pattern.quote(File.separator);
        String[] path = file.split(pattern);
        String sample = "";
        for (String folder : path) {
            //Match sample names.
            if (folder.toLowerCase().matches("^(copd|healthy|control)_?\\d{1,}$")) {
                sample = folder;
                //Match dataset names.
            }
        }
        System.out.println("Collecting protein-peptides from " + sample + " " + dataset + "...");
        // Load the file.
        FileReader fr = new FileReader(file);
        BufferedReader bffFr = new BufferedReader(fr);
        String line;
        Integer count = 1;
        String uniqueCombined = "";
        String uniqueIndividual = "";
        //Some indixes may vary, so a check is needed to find their position inside a file.
        int groupIndex = 0;
        int accessionIndex = 0;
        int peptideIndex = 0;
        int uniqueIndex = 0;
        int scoreIndex = 0;
        int massIndex = 0;
        int lengthIndex = 0;
        Boolean firstLine = true;
        //Read the file.
        while ((line = bffFr.readLine()) != null) {
            if (firstLine) {
                String[] data = line.split(",");
                //Determine indices by using names on first line.
                for (int i = 0; i < data.length; i++) {
                    if (data[i].toLowerCase().contains("protein group")) {
                        groupIndex = i;
                    } else if (data[i].toLowerCase().contains("accession")) {
                        accessionIndex = i;
                    } else if (data[i].toLowerCase().contains("sequence")) {
                        peptideIndex = i;
                    } else if (data[i].toLowerCase().contains("unique")) {
                        uniqueIndex = i;
                    } else if (data[i].toLowerCase().contains("-10lgp")) {
                        scoreIndex = i;
                    }  else if (data[i].toLowerCase().equals("mass")) {
                        massIndex = i;
                    } else if (data[i].toLowerCase().equals("length")) {
                        lengthIndex = i;
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
            String mass = data[massIndex];
            String length = data[lengthIndex];
            //Remove first and last 2 indices.
            sequence = sequence.replaceAll("\\.[A-Z]$", "");
            sequence = sequence.replaceAll("^[A-Z]\\.", "");
            //Possibility to remove (+15.99) values from sequences
//            sequence = sequence.replaceAll("\\(\\+[0-9]+\\.[0-9]+\\)", "");
            //Set get data according to indices.
            String uniqueToGroup = data[uniqueIndex];
            String score = data[scoreIndex];
            boolean newEntry = true;
            //Create new ProteinPeptide object.
            ProteinPeptide newProteinPeptide = new ProteinPeptide(proteinGroup, accession, sequence, sample, mass,
                    length, uniqueToGroup, uniqueCombined, uniqueIndividual, dataset, count, score);
            //Creates a proteinPeptide object with data per sample.
            if (!proteinPeptides.getProteinPeptideMatches().isEmpty()) {
                for (ProteinPeptide proteinPeptide: proteinPeptides.getProteinPeptideMatches()) {
                    if (proteinPeptide.getSequence().equals(sequence)) {
                        newEntry = false;
                        proteinPeptide.setCounter(count);
                        //Collects all protein groups per sequence for a sample.
                        if (!proteinPeptide.getProteinGroup().contains(proteinGroup)) {
                            proteinPeptide.addProteinGroup(proteinGroup);
                        }
                        //Collects all accession IDs per sequence for a sample.
                        if (proteinPeptide.getAccession().contains("|")) {
                            Boolean newAcc = true;
                            String[] accessionList = proteinPeptide.getAccession().split("|");
                            for (String acc: accessionList) {
                                if (acc.equals(accession)) {
                                    newAcc = false;
                                }
                            }
                            //Adds new accession to the ProteinPeptide object.
                            if (newAcc) {
                                proteinPeptide.addAccession(accession);
                            }
                        } else if (!proteinPeptide.getAccession().equals(accession)) {
                            proteinPeptide.addAccession(accession);
                        }
                        //Collects all -10lgP scores per sequence for a sample.
                        if (!proteinPeptide.getScore().contains(score)) {
                            proteinPeptide.addScore(score);
                        }
                    }
                }
                //If no match was found: add new entry to collection.
                if (newEntry) {
                    proteinPeptides.addProteinPeptideMatch(newProteinPeptide);
                }
            } else {
                //Adds first match to the collection.
                proteinPeptides.addProteinPeptideMatch(newProteinPeptide);
            }
        }
        System.out.println("Collected " + proteinPeptides.getProteinPeptideMatches().size()
                + " protein-peptides from " + sample + " " + dataset + "!");
        return proteinPeptides;
    }
}
