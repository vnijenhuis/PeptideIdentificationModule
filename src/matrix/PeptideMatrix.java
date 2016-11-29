/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matrix;

import collections.MatrixEntryCollection;
import collections.ProteinPeptideCollection;
import java.util.ArrayList;
import java.util.Collections;
import objects.MatrixEntry;
import objects.ProteinPeptide;

/**
 * Creates a MatrixEntryCollection based on given parameters.
 *
 * @author Vikthor
 */
public class PeptideMatrix {

    /**
     * Creates a MatrixEntryCollection based on the peptide sequence and corresponding protein group.
     *
     * @param proteinPeptideCollection collection of ProteinPeptide objects.
     * @param sampleSize amount of samples.
     * @return collection of MatrixEntry objects which are used to create the peptide matrix.
     */
    public final MatrixEntryCollection createPeptideMatrixBasedOnProteinGroup(final ProteinPeptideCollection proteinPeptideCollection, final Integer sampleSize) {
        MatrixEntryCollection matrixEntryCollection = new MatrixEntryCollection();
        proteinPeptideCollection.sortOnPeptideSequence();
        for (ProteinPeptide proteinPeptide: proteinPeptideCollection.getProteinPeptideMatches()) {
            for (int groupIndex = 0; groupIndex < proteinPeptide.getProteinGroupList().size() - 1; groupIndex++) {
                String proteinGroup = proteinPeptide.getProteinGroupList().get(groupIndex);
                ArrayList<Integer> sampleIndexList = new ArrayList<>();
                ArrayList<Double> scoreIndexList = new ArrayList<>();
                for (int k = 0; k < sampleSize; k++) {
                    sampleIndexList.add(0);
                    scoreIndexList.add(0.0);
                }
                Boolean newEntry = true;
                if (!matrixEntryCollection.getMatrixEntries().isEmpty()) {
                    outer: for (MatrixEntry matrixEntry: matrixEntryCollection.getMatrixEntries()) {
                        //Replace modification mass values to get a clean peptide sequence.
                        String filteredMatrixSequence = matrixEntry.getSequence().replaceAll("\\(\\+?\\-?[0-9]+\\.[0-9]+\\)", "");
                        String filteredProteinPeptideSequence = proteinPeptide.getSequence().replaceAll("\\(\\+?\\-?[0-9]+\\.[0-9]+\\)", "");
                        if (filteredMatrixSequence.matches(filteredProteinPeptideSequence)) {
                            for (String currentGroup: matrixEntry.getProteinGroupList()) {
                                if (currentGroup.matches(proteinGroup)) {
                                    ArrayList<Double> proteinGroupScoreList = proteinPeptide.getScoreList().get(groupIndex);
                                    //Get highest score and spectra count.
                                    Double highestScore = Collections.max(proteinGroupScoreList);
                                    Integer spectraCount = proteinPeptide.getCountList().get(groupIndex);
                                    Integer index = proteinPeptide.getSampleIndexNumber();
                                    //Add values to correct index.
                                    matrixEntry.addCountToSampleIndex(index, spectraCount);
                                    matrixEntry.setScoreAtIndex(index, highestScore);
                                    matrixEntry.setUniqueToGroup(false);
                                    //Check if spectra is unique to one sample.
                                    int counter = 0;
                                    for (Integer count: matrixEntry.getSampleIndexList()) {
                                        if (count > 0) {
                                            counter++;
                                        }
                                        if (counter > 1) {
                                            matrixEntry.setUniqueToSampleDatabase(false);
                                        }
                                    }
                                    //Add accession ids to list.
                                    if (!matrixEntry.getAccessionList().isEmpty()) {
                                        for (String accession: proteinPeptide.getCombinedAccessionList().get(groupIndex)) {
                                            if (!matrixEntry.getAccessionList().contains(accession)) {
                                                matrixEntry.addAccession(accession);
                                            }
                                        }
                                    } else {
                                        matrixEntry.getAccessionList().addAll(proteinPeptide.getCombinedAccessionList().get(groupIndex));
                                    }
                                    Collections.sort(matrixEntry.getAccessionList());
                                    newEntry = false;
                                    break outer;
                                }
                            }
                        }
                    }
                }
                if (newEntry) {
                    //Create new matrix entry.
                    ArrayList<String> proteinGroupAccessionList = proteinPeptide.getCombinedAccessionList().get(groupIndex);
                    ArrayList<String> proteinGroupList = new ArrayList<>();
                    proteinGroupList.add(proteinGroup);
                    Collections.sort(proteinGroupAccessionList);
                    MatrixEntry newMatrixEntry = new MatrixEntry(proteinGroupList, proteinGroupAccessionList, proteinPeptide.getSequence(), sampleIndexList, proteinPeptide.getMass(), proteinPeptide.getLength(), proteinPeptide.getUniqueToGroup(), proteinPeptide.getUniqueToDatabase(), proteinPeptide.getDataset(), sampleSize, scoreIndexList);
                    ArrayList<Double> proteinGroupScoreList = proteinPeptide.getScoreList().get(groupIndex);
                    //Get highest score and spectra count for given sample.
                    Double highestScore = Collections.max(proteinGroupScoreList);
                    Integer count = proteinPeptide.getCountList().get(groupIndex);
                    Integer index = proteinPeptide.getSampleIndexNumber();
                    //Add to correct index.
                    newMatrixEntry.addCountToSampleIndex(index, count);
                    newMatrixEntry.setScoreAtIndex(index, highestScore);
                    //Set uniqueness values.
                    newMatrixEntry.setUniqueToGroup(true);
                    newMatrixEntry.setUniqueToSampleDatabase(proteinPeptide.getUniqueToDatabase());
                    matrixEntryCollection.addMatrixEntry(newMatrixEntry);
                }
            }
        }
        return matrixEntryCollection;
    }

    /**
     * Creates unique matrix entries solely based on the peptide sequence. Each peptide sequence now has data of multiple protein groups per row for the given sequence.
     * 
     * @param proteinPeptideCollection collection of ProteinPeptide objects.
     * @param sampleSize amount of samples.
     * @return collection of MatrixEntry objects which are used to create the peptide matrix.
     */
    public final MatrixEntryCollection createPeptideMatrixBasedOnSequence(final ProteinPeptideCollection proteinPeptideCollection, final Integer sampleSize) {
        MatrixEntryCollection matrixEntryCollection = new MatrixEntryCollection();
        proteinPeptideCollection.sortOnPeptideSequence();
        for (ProteinPeptide proteinPeptide: proteinPeptideCollection.getProteinPeptideMatches()) {
            for (int groupIndex = 0; groupIndex < proteinPeptide.getProteinGroupList().size() - 1; groupIndex++) {
                ArrayList<Integer> sampleIndexList = new ArrayList<>();
                ArrayList<Double> scoreIndexList = new ArrayList<>();
                for (int k = 0; k < sampleSize; k++) {
                    sampleIndexList.add(0);
                    scoreIndexList.add(0.0);
                }
                Boolean newEntry = true;
                if (!matrixEntryCollection.getMatrixEntries().isEmpty()) {
                    for (MatrixEntry matrixEntry: matrixEntryCollection.getMatrixEntries()) {
                        //Replace modification mass values to get a clean peptide sequence.
                        String filteredMatrixSequence = matrixEntry.getSequence().replaceAll("\\(\\+?\\-?[0-9]+\\.[0-9]+\\)", "");
                        String filteredProteinPeptideSequence = proteinPeptide.getSequence().replaceAll("\\(\\+?\\-?[0-9]+\\.[0-9]+\\)", "");
                        //Match sequences, check for overlapping data.
                        if (filteredMatrixSequence.matches(filteredProteinPeptideSequence)) {
                            //If a match happens: add count and score to the right index.
                            ArrayList<Double> proteinGroupScoreList = proteinPeptide.getScoreList().get(groupIndex);
                            Double highestScore = Collections.max(proteinGroupScoreList);
                            Integer spectraCount = proteinPeptide.getCountList().get(groupIndex);
                            Integer index = proteinPeptide.getSampleIndexNumber();
                            matrixEntry.addCountToSampleIndex(index, spectraCount);
                            matrixEntry.setScoreAtIndex(index, highestScore);
                            if (!matrixEntry.getProteinGroupList().contains(proteinPeptide.getProteinGroupList().get(groupIndex))) {
                                //Match means multiple protein groups, so unique is false.
                                matrixEntry.setUniqueToGroup(false);
                                //add protein group to list.
                                matrixEntry.addProteinGroup(proteinPeptide.getProteinGroupList().get(groupIndex));
                            }
                            //check if spectra count in more then one sample.
                            int counter = 0;
                            for (Integer count: matrixEntry.getSampleIndexList()) {
                                if (count > 0) {
                                    counter++;
                                }
                                if (counter > 1) {
                                    matrixEntry.setUniqueToSampleDatabase(false);
                                }
                            }
                            //Add new accessions to the list.
                            if (!matrixEntry.getAccessionList().isEmpty()) {
                                for (String accession: proteinPeptide.getCombinedAccessionList().get(groupIndex)) {
                                    if (!matrixEntry.getAccessionList().contains(accession)) {
                                        matrixEntry.addAccession(accession);
                                    }
                                }
                            } else {
                                matrixEntry.getAccessionList().addAll(proteinPeptide.getCombinedAccessionList().get(groupIndex));
                            }
                            //sort the accession list for better overview.
                            Collections.sort(matrixEntry.getAccessionList());
                            newEntry = false;
                            break;
                        }
                    }
                }
                if (newEntry) {
                    //Create a new entry for the matrix.
                    ArrayList<String> proteinGroupAccessionList = proteinPeptide.getCombinedAccessionList().get(groupIndex);
                    Collections.sort(proteinGroupAccessionList);
                    MatrixEntry newMatrixEntry = new MatrixEntry(proteinPeptide.getProteinGroupList(), proteinGroupAccessionList, proteinPeptide.getSequence(), sampleIndexList, proteinPeptide.getMass(), proteinPeptide.getLength(), proteinPeptide.getUniqueToGroup(), proteinPeptide.getUniqueToDatabase(), proteinPeptide.getDataset(), sampleSize, scoreIndexList);
                    ArrayList<Double> proteinGroupScoreList = proteinPeptide.getScoreList().get(groupIndex);
                    //Determine highest score.
                    Double highestScore = Collections.max(proteinGroupScoreList);
                    Integer count = 0;
                    //Determine total spectra count.
                    for (Integer sampleCount: proteinPeptide.getCountList()) {
                        count += sampleCount;
                    }
                    //Add values to correct index.
                    Integer index = proteinPeptide.getSampleIndexNumber();
                    newMatrixEntry.addCountToSampleIndex(index, count);
                    newMatrixEntry.setScoreAtIndex(index, highestScore);
                    //set uniqueness values..
                    if (proteinPeptide.getProteinGroupList().size() == 1) {
                        newMatrixEntry.setUniqueToGroup(true);
                    } else {
                        newMatrixEntry.setUniqueToGroup(false);
                    }
                    newMatrixEntry.setUniqueToSampleDatabase(proteinPeptide.getUniqueToDatabase());
                    matrixEntryCollection.addMatrixEntry(newMatrixEntry);
                }
            }
        }
        return matrixEntryCollection;
    }
}
