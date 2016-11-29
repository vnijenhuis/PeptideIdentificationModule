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
import java.util.ArrayList;
import java.util.regex.Pattern;
import objects.ProteinPeptide;

/**
 * Creates a protein-peptide collection.
 *
 * @author vnijenhuis
 */
public class ProteinPeptideFileReader {
    /**
     * Creates a collection of the protein-peptide.csv file;
     * @param file protein-peptide.csv file.
     * @param dataset name of the dataset.
     * @param sampleNumber sample index number.
     * @param removeEnsemblHits flag that shows if ensemble hits should be removed.
     * @return collection of ProteinPeptide objects.
     * @throws FileNotFoundException file was not found/does not exist.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is
     * already opened by another program.
     */
    public final ProteinPeptideCollection createCollection(final String file, final String dataset, final Integer sampleNumber, final Boolean removeEnsemblHits)
            throws FileNotFoundException, IOException {
        ProteinPeptideCollection proteinPeptides = new ProteinPeptideCollection();
        //Creates dataset and patient names depending on the map names.
        String pattern = Pattern.quote(File.separator);
        String[] path = file.split(pattern);
        String sample = path[path.length-2];
        System.out.println("Collecting protein-peptides from " + file + "...");
        // Load the file.
        FileReader fr = new FileReader(file);
        BufferedReader bffFr = new BufferedReader(fr);
        String line;
        Integer count = 1;
        //Some indixes may vary, so a check is needed to find their position inside a file.
        int groupIndex = 0;
        int accessionIndex = 0;
        int peptideIndex = 0;
        int scoreIndex = 0;
        int massIndex = 0;
        int lengthIndex = 0;
        Boolean firstLine = true;
        //Read the file.
        int lineCount = 0;
        while ((line = bffFr.readLine()) != null) {
            lineCount++;
            if (firstLine) {
                String[] data = line.split(",");
                //Determine indices by using names on first line.
                for (int i = 0; i < data.length; i++) {
                    String lowerCaseData = data[i].toLowerCase();
                    if (lowerCaseData.contains("protein group")) {
                        groupIndex = i;
                    } else if (data[i].toLowerCase().contains("protein accession")) {
                        accessionIndex = i;
                    } else if (data[i].toLowerCase().matches("peptide sequence") || data[i].toLowerCase().matches("peptide")) {
                        peptideIndex = i;
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
            ArrayList<String> proteinGroupList = new ArrayList<>();
            proteinGroupList.add(proteinGroup);
            String accession = data[accessionIndex];
            ArrayList<ArrayList<String>> combinedAccessionList = new ArrayList<>();
            ArrayList<String> accessionList = new ArrayList<>();
            accessionList.add(accession);
            combinedAccessionList.add(accessionList);
            String sequence = data[peptideIndex];
            String mass = data[massIndex];
            String givenLength = data[lengthIndex];
            Integer length = Integer.parseInt(givenLength);
            //Remove first and last 2 indices.
            sequence = sequence.replaceAll("\\.[A-Z]$", "");
            sequence = sequence.replaceAll("^[A-Z]\\.", "");
            boolean newEntry = true;
            //Possibility to remove (+15.99) values from sequences
            String filteredSequence = sequence.replaceAll("\\(\\+?\\-?[0-9]+\\.[0-9]+\\)", "");
            //Set get data according to indices.
            String score = data[scoreIndex];
            Double peptideScore = Double.parseDouble(score);
            ArrayList<ArrayList<Double>> combinedScoreList = new ArrayList<>();
            ArrayList<Double> scoreList = new ArrayList<>();
            scoreList.add(peptideScore);
            combinedScoreList.add(scoreList);
            scoreList.add(peptideScore);
            ArrayList<Integer> peptideCountList = new ArrayList<>();
            peptideCountList.add(count);
            //Create new ProteinPeptide object.
            if (removeEnsemblHits) {
                if (accession.matches("ENST[0-9]+_?.*") || accession.toUpperCase().contains("DECOY")) {
                    accession = "";
                }
            }
            if (!accession.isEmpty() || !accession.equals("")) {
                ProteinPeptide newProteinPeptide = new ProteinPeptide(proteinGroupList, combinedAccessionList, sequence, sample, sampleNumber, mass, length, false, false, dataset, peptideCountList, combinedScoreList);
                //Creates a proteinPeptide object with data per sample.
                if (!proteinPeptides.getProteinPeptideMatches().isEmpty()) {
                    for (ProteinPeptide proteinPeptide: proteinPeptides.getProteinPeptideMatches()) {
                        String proteinPeptideSequence = proteinPeptide.getSequence().replaceAll("\\(\\+?\\-?[0-9]+\\.[0-9]+\\)", "");
                        if (proteinPeptideSequence.matches(filteredSequence)) {
                            newEntry = false;
                            if (proteinPeptide.getProteinGroupList().contains(proteinGroup)) {
                                int index = proteinPeptide.getProteinGroupList().indexOf(proteinGroup);
                                proteinPeptide.addToCount(index, count);
                                if (!proteinPeptide.getCombinedAccessionList().get(index).contains(accession)) {
                                    proteinPeptide.getCombinedAccessionList().get(index).add(accession);
                                    proteinPeptide.getScoreList().get(index).add(peptideScore);
                                    break;
                                }
                            } else {
                                //New entry for given sequence.
                                proteinPeptide.getCountList().add(count);
                                proteinPeptide.addProteinGroup(proteinGroup);
                                proteinPeptide.getCombinedAccessionList().add(accessionList);
                                proteinPeptide.getScoreList().add(scoreList);
                                break;
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
            if (lineCount % 2000 == 0) {
                System.out.println("Processed " + lineCount + " protein-peptide entries from " + sample + " " + dataset + "!");
            }
        }
        System.out.println("Collected " + proteinPeptides.getProteinPeptideMatches().size()
                + " unique protein-peptide objects from " + sample + " " + dataset + "!");
        return proteinPeptides;
    }
}
