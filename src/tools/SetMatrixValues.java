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
     * @param patient patient ID such as COPD1, Healthy 5 etc.
     * @param sampleSize amount of samples.
     * @return HashSet with count and coverage values.
     */
    public final HashSet<ArrayList<String>> setValues(final ProteinPeptideCollection proteinPeptides,
            final HashSet<ArrayList<String>> peptideMatrix, final String patient, final Integer sampleSize) {
        for (ArrayList<String> array: peptideMatrix) {
            for (ProteinPeptide proteinPeptide: proteinPeptides.getPeptideMatches()) {
                //Check if ProteinPeptide exists inside the matrix.
                if (array.get(0).equals(proteinPeptide.getProteinGroup()) 
                        && array.get(1).equals(proteinPeptide.getAccession())
                        && array.get(2).equals(proteinPeptide.getSequence())) {
                    //Sets count and coverage values for this patient.
                    if (patient.contains("Healthy")) {
                        int cntIndex = (Integer.parseInt(patient.substring(7))*2 + 4);
                        int covIndex = (Integer.parseInt(patient.substring(7))*2 + 5);
                        array.set(cntIndex, proteinPeptide.getCounter().toString());
                        array.set(covIndex, proteinPeptide.getCoverage().toString());
                    } else if (patient.contains("COPD")) {
                        int cntIndex = (Integer.parseInt(patient.substring(4))*2 + 4);
                        int covIndex = (Integer.parseInt(patient.substring(4))*2 + 5);
                        array.set(cntIndex, proteinPeptide.getCounter().toString());
                        array.set(covIndex, proteinPeptide.getCoverage().toString());
                    }
                }
            }
        }
    return peptideMatrix;
    }
}
