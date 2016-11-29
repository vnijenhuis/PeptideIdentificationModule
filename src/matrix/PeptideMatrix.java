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
                                    Double highestScore = Collections.max(proteinGroupScoreList);
                                    Integer count = proteinPeptide.getCountList().get(groupIndex);
                                    Integer index = proteinPeptide.getSampleIndexNumber();
                                    matrixEntry.addCountToSampleIndex(index, count);
                                    matrixEntry.setScoreAtIndex(index, highestScore);
                                    matrixEntry.setUniqueToGroup(false);
                                    if (matrixEntry.isUniqueToSampleDatabase() == true) {
                                        matrixEntry.setUniqueToSampleDatabase(proteinPeptide.getUniqueToDatabase());
                                    }
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
                    ArrayList<String> proteinGroupAccessionList = proteinPeptide.getCombinedAccessionList().get(groupIndex);
                    ArrayList<String> proteinGroupList = new ArrayList<>();
                    proteinGroupList.add(proteinGroup);
                    Collections.sort(proteinGroupAccessionList);
                    MatrixEntry newMatrixEntry = new MatrixEntry(proteinGroupList, proteinGroupAccessionList, proteinPeptide.getSequence(), sampleIndexList, proteinPeptide.getMass(), proteinPeptide.getLength(), proteinPeptide.getUniqueToGroup(), proteinPeptide.getUniqueToDatabase(), proteinPeptide.getDataset(), sampleSize, scoreIndexList);
                    ArrayList<Double> proteinGroupScoreList = proteinPeptide.getScoreList().get(groupIndex);
                    Double highestScore = Collections.max(proteinGroupScoreList);
                    Integer count = proteinPeptide.getCountList().get(groupIndex);
                    Integer index = proteinPeptide.getSampleIndexNumber();
                    newMatrixEntry.addCountToSampleIndex(index, count);
                    newMatrixEntry.setScoreAtIndex(index, highestScore);
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
                            ArrayList<Double> proteinGroupScoreList = proteinPeptide.getScoreList().get(groupIndex);
                            Double highestScore = Collections.max(proteinGroupScoreList);
                            Integer count = proteinPeptide.getCountList().get(groupIndex);
                            Integer index = proteinPeptide.getSampleIndexNumber();
                            matrixEntry.addCountToSampleIndex(index, count);
                            matrixEntry.setScoreAtIndex(index, highestScore);
                            matrixEntry.setUniqueToGroup(false);
                            if (!matrixEntry.getProteinGroupList().contains(proteinPeptide.getProteinGroupList().get(groupIndex))) {
                                matrixEntry.addProteinGroup(proteinPeptide.getProteinGroupList().get(groupIndex));
                            }
                            if (matrixEntry.isUniqueToSampleDatabase() == true) {
                                matrixEntry.setUniqueToSampleDatabase(proteinPeptide.getUniqueToDatabase());
                            }
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
                            break;
                        }
                    }
                }
                if (newEntry) {
                    ArrayList<String> proteinGroupAccessionList = proteinPeptide.getCombinedAccessionList().get(groupIndex);
                    Collections.sort(proteinGroupAccessionList);
                    MatrixEntry newMatrixEntry = new MatrixEntry(proteinPeptide.getProteinGroupList(), proteinGroupAccessionList, proteinPeptide.getSequence(), sampleIndexList, proteinPeptide.getMass(), proteinPeptide.getLength(), proteinPeptide.getUniqueToGroup(), proteinPeptide.getUniqueToDatabase(), proteinPeptide.getDataset(), sampleSize, scoreIndexList);
                    ArrayList<Double> proteinGroupScoreList = proteinPeptide.getScoreList().get(groupIndex);
                    Double highestScore = Collections.max(proteinGroupScoreList);
                    Integer count = 0;
                    for (Integer sampleCount: proteinPeptide.getCountList()) {
                        count += sampleCount;
                    }
                    Integer index = proteinPeptide.getSampleIndexNumber();
                    newMatrixEntry.addCountToSampleIndex(index, count);
                    newMatrixEntry.setScoreAtIndex(index, highestScore);
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
