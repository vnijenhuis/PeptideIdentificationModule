/*
 * @author vnijenhuis
 * @project peptide spectrum matrix quality control  * 
 * @copyrights vnijenhuis, Dr. P.I. Horvatovich  * 
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
            final HashSet<ArrayList<String>> peptideMatrix, final Integer sampleSize) {
        //Go throigh all arrays for each proteinPeptide object.
        for (ArrayList<String> array: peptideMatrix) {
            for (ProteinPeptide proteinPeptide: proteinPeptides.getPeptideMatches()) {
                //Check if ProteinPeptide exists inside the matrix.
                if (array.get(0).equals(proteinPeptide.getProteinGroup()) 
                        && array.get(1).equals(proteinPeptide.getAccession())
                        && array.get(2).equals(proteinPeptide.getSequence())) {
                    //Add Healthy sample data.
                    if (proteinPeptide.getSample().contains("Healthy")) {
                        int cntIndex = (Integer.parseInt(proteinPeptide.getSample().substring(7))*2 + 4);
                        int covIndex = (Integer.parseInt(proteinPeptide.getSample().substring(7))*2 + 5);
                        array.set(cntIndex, proteinPeptide.getCounter().toString());
                        array.set(covIndex, proteinPeptide.getCoverage().toString());
                        break;
                    //Add COPD sample data
                    } else if (proteinPeptide.getSample().contains("COPD")) {
                        int cntIndex = (Integer.parseInt(proteinPeptide.getSample().substring(4))*2 + 4 + sampleSize);
                        int covIndex = (Integer.parseInt(proteinPeptide.getSample().substring(4))*2 + 5 + sampleSize);
                        System.out.println(array.size());
                        System.out.println(cntIndex);
                        System.out.println(covIndex);
                        System.out.println(array);
                        array.set(cntIndex, proteinPeptide.getCounter().toString());
                        array.set(covIndex, proteinPeptide.getCoverage().toString());
                        break;
                    }
                }
            }
        }
    return peptideMatrix;
    }
}
