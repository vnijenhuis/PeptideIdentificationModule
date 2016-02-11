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
                //Check if ProteinPeptide exists inside the matrix by matching peptide sequences.
                if (array.get(2).equals(proteinPeptide.getSequence())) {
                    //Add Healthy sample data.
                    int arraySize = array.size();
                    int sampleIndex = 0;
                    int countIndex = 0;
                    int coverageIndex = 0;
                    String sample = proteinPeptide.getSample();
                    //Index is based on columns. +5 for the standard columns (group, accession, sequence,
                    //unique to group, unique to combined, dataset, + individualUnique to place uniqueness of an
                    //individual for each dataset. Samplesize and sample.substring is used to place counter/coverage of
                    //each sample on the right place.
                    //Determine Healthy Indices.
                    if (sample.contains("Healthy")) {
                        sampleIndex = Integer.parseInt(sample.substring(7));
                        countIndex = (sampleIndex + 5 + individualUnique);
                        coverageIndex = (sampleIndex + 5 + individualUnique + sampleSize);
                    //Determine COPD indices
                    } else if (sample.contains("COPD")) {
                        sampleIndex = Integer.parseInt(sample.substring(4));
                        countIndex = (sampleIndex + 5 + individualUnique + sampleSize/2);
                        coverageIndex = (sampleIndex + 5 + individualUnique + sampleSize + sampleSize/2);
                    }
                    // Sets the data to the given array index value.
                    Integer count = (Integer.parseInt(array.get(countIndex)) + proteinPeptide.getCounter());
                    array.set(countIndex, count.toString());
                    if (array.get(coverageIndex).equals("0")) {
                        array.set(coverageIndex, proteinPeptide.getCoverage());
                    } else if (!array.get(coverageIndex).contains(proteinPeptide.getCoverage())){
                        array.set(coverageIndex, array.get(coverageIndex) + "|" + proteinPeptide.getCoverage());
                    }
                    //Add counter per dataset.
                    for (int i = 0; i < datasets.size(); i++) {
                        if (proteinPeptide.getDataset().equals(datasets.get(i))) {
                            int index = (arraySize-datasets.size())+i;
                            Integer dataCount = Integer.parseInt(array.get(index)) + proteinPeptide.getCounter();
                            array.set(index, dataCount.toString());  
                        }
                    }
                }
            }
        }
    //Returns the set of arrays.
    return peptideMatrix;
    }
}
