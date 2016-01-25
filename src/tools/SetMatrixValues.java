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
 *
 * @author f103013
 */
public class SetMatrixValues {
        /**
     * Adds count and coverage values to the matrix.
     * @param proteinPeptides
     * @param peptideMatrix matrix of peptides.
     * @param patient patient ID such as COPD1, Healthy 5 etc.
     * @param size sample size.
     * @return HashSet with count and coverage values.
     */
    public final HashSet<ArrayList<String>> setValues(final ProteinPeptideCollection proteinPeptides,
            final HashSet<ArrayList<String>> peptideMatrix, final String patient, final Integer sampleSize) {
        for (ArrayList<String> array: peptideMatrix) {
            for (ProteinPeptide proteinPeptide: proteinPeptides.getPeptideMatches()) {
                if (array.get(0).equals(proteinPeptide.getProteinGroup()) 
                        && array.get(1).equals(proteinPeptide.getAccession())
                        && array.get(2).equals(proteinPeptide.getSequence())) {
                    if (patient.contains("Healthy")) {
                        int cntIndex = (Integer.parseInt(patient.substring(7))*2 + 6);
                        int covIndex = (Integer.parseInt(patient.substring(7))*2 + 7);
                        array.set(cntIndex, proteinPeptide.getCounter().toString());
                        array.set(covIndex, proteinPeptide.getCoverage().toString());
                    } else if (patient.contains("COPD")) {
                        int cntIndex = (Integer.parseInt(patient.substring(4))*2 + 6 + sampleSize);
                        int covIndex = (Integer.parseInt(patient.substring(4))*2 + 7 + sampleSize);
                        array.set(cntIndex, proteinPeptide.getCounter().toString());
                        array.set(covIndex, proteinPeptide.getCoverage().toString());
                    }
                }
            }
        }
    return peptideMatrix;
    }
}
