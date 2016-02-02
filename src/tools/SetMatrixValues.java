/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum matrix quality control  * 
 */
package tools;

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
     * @param proteinPeptides
     * @param peptideMatrix matrix of peptides.
     * @param sampleSize amount of samples.
     * @return HashSet with count and coverage values.
     */
    public final HashSet<ArrayList<String>> setValues(final ProteinPeptideCollection proteinPeptides,
            final HashSet<ArrayList<String>> peptideMatrix, final Integer sampleSize, ArrayList<String> datasets) {
        //Go throigh all arrays for each proteinPeptide object.
        for (ArrayList<String> array: peptideMatrix) {
            for (ProteinPeptide proteinPeptide: proteinPeptides.getProteinPeptideMatches()) {
                //Check if ProteinPeptide exists inside the matrix.
                if (array.get(1).equals(proteinPeptide.getAccession()) && array.get(2).equals(proteinPeptide.getSequence())) {
                    //Add Healthy sample data.
                    int arraySize = array.size();
                    if (proteinPeptide.getSample().contains("Healthy")) {
                        int cntIndex = (Integer.parseInt(proteinPeptide.getSample().substring(7)) + 5);
                        int covIndex = (Integer.parseInt(proteinPeptide.getSample().substring(7)) + 5 + sampleSize);
                        array.set(cntIndex, proteinPeptide.getCounter().toString());
                        array.set(covIndex, proteinPeptide.getCoverage().toString());
                    //Add COPD sample data
                    } else if (proteinPeptide.getSample().contains("COPD")) {
                        int cntIndex = (Integer.parseInt(proteinPeptide.getSample().substring(4)) + 5 + sampleSize/2);
                        int covIndex = (Integer.parseInt(proteinPeptide.getSample().substring(4)) + 5 + sampleSize + sampleSize/2);
                        array.set(cntIndex, proteinPeptide.getCounter().toString());
                        array.set(covIndex, proteinPeptide.getCoverage().toString());
                    }
                    for (int i = 0; i < datasets.size(); i++) {
                        if (proteinPeptide.getDataset().equals(datasets.get(i))) {
                            int index = (arraySize-datasets.size())+i;
                            Integer count = Integer.parseInt(array.get(index))+proteinPeptide.getCounter();
                            array.set(index, count.toString());  
                        }
                    }
                }
            }
        }
    return peptideMatrix;
    }
}
