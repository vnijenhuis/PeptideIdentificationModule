/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  * 
 */
package matrix;

import collections.ProteinPeptideCollection;
import java.util.ArrayList;
import java.util.HashSet;
import objects.ProteinPeptide;

/**
 * Sets count and coverage values for the protein-peptide matrix.
 * @author vnijenhuis
 */
public class SetMatrixValues {
    /**
     * Adds count and coverage values to the matrix.
     * @param proteinPeptides protein peptide collection.
     * @param peptideMatrix matrix of peptides.
     * @param sampleSize amount of samples.
     * @param datasets array with datasets.
     * @return HashSet with count and coverage values.
     */
    public final HashSet<ArrayList<String>> setValues(final ProteinPeptideCollection proteinPeptides,
            final HashSet<ArrayList<String>> peptideMatrix, final Integer sampleSize, ArrayList<String> datasets) {
        //Go throigh all arrays for each proteinPeptide object.
        int individualUnique = datasets.size();
        for (ArrayList<String> array: peptideMatrix) {
            for (ProteinPeptide proteinPeptide: proteinPeptides.getProteinPeptideMatches()) {
                //Check if ProteinPeptide exists inside the matrix.
                if (array.get(2).equals(proteinPeptide.getSequence())) {
                    //Add Healthy sample data.
                    int arraySize = array.size();
                    String sample = proteinPeptide.getSample();
                    //Index is based on columns. +5 for the standard columns (group, accession, sequence,
                    //unique to group, unique to combined, dataset, + individualUnique to place uniqueness of an
                    //individual for each dataset. Samplesize and sample.substring is used to place counter/coverage of
                    //each sample on the right place.
                    if (sample.contains("Healthy")) {
                        int sampleIndex = Integer.parseInt(sample.substring(7));
                        int cntIndex = (sampleIndex + 5 + individualUnique);
                        int covIndex = (sampleIndex + 5 + individualUnique + sampleSize);
                        Integer count = (Integer.parseInt(array.get(cntIndex)) + proteinPeptide.getCounter());
                        array.set(cntIndex, count.toString());
                        if (array.get(covIndex).equals("0")) {
                            array.set(covIndex, proteinPeptide.getCoverage());
                        } else {
                            array.set(covIndex, array.get(covIndex) + "|" + proteinPeptide.getCoverage());
                        }
                    //Add COPD sample data
                    } else if (sample.contains("COPD")) {
                        int sampleIndex = Integer.parseInt(sample.substring(4));
                        int cntIndex = (sampleIndex + 5 + individualUnique + sampleSize/2);
                        int covIndex = (sampleIndex + 5 + individualUnique + sampleSize + sampleSize/2);
                        Integer count = (Integer.parseInt(array.get(cntIndex)) + proteinPeptide.getCounter());
                        array.set(cntIndex, count.toString());
                        if (array.get(covIndex).equals("0")) {
                            array.set(covIndex, proteinPeptide.getCoverage());
                        } else if (!array.get(covIndex).contains(proteinPeptide.getCoverage())){
                            array.set(covIndex, array.get(covIndex) + "|" + proteinPeptide.getCoverage());
                        }
                    }
                    //Add counter per dataset.
                    for (int i = 0; i < datasets.size(); i++) {
                        if (proteinPeptide.getDataset().equals(datasets.get(i))) {
                            int index = (arraySize-datasets.size())+i;
                            Integer count = Integer.parseInt(array.get(index)) + proteinPeptide.getCounter();
                            array.set(index, count.toString());  
                        }
                    }
                }
            }
        }
    return peptideMatrix;
    }
}
