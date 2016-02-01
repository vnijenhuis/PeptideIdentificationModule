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
            final HashSet<ArrayList<String>> peptideMatrix, final Integer sampleSize) {
        //Go throigh all arrays for each proteinPeptide object.
        for (ArrayList<String> array: peptideMatrix) {
            for (ProteinPeptide proteinPeptide: proteinPeptides.getProteinPeptideMatches()) {
                //Check if ProteinPeptide exists inside the matrix.
                if (array.get(0).equals(proteinPeptide.getProteinGroup()) 
                        && array.get(1).equals(proteinPeptide.getAccession())
                        && array.get(2).equals(proteinPeptide.getSequence())) {
                    int lastIndex = array.size()-1;
                    //Add Healthy sample data.
                    if (proteinPeptide.getSample().contains("Healthy")) {
                        if (!array.get(6).contains(proteinPeptide.getSample())) {
                            array.set(6, array.get(6) + "|" + proteinPeptide.getCounter().toString());
                            array.set(7, array.get(7) + ";" + proteinPeptide.getSample() + "|" + proteinPeptide.getCoverage().toString());
                            array.set(lastIndex, array.get(lastIndex) + proteinPeptide.getCounter());
                        }
                    //Add COPD sample data
                    } else if (proteinPeptide.getSample().contains("COPD")) {
                        if (!array.get(6).contains(proteinPeptide.getSample())) {
                            array.set(6, array.get(6) + ";" + "|" + proteinPeptide.getCounter().toString());
                            array.set(7, array.get(7) + ";" + proteinPeptide.getSample() + "|" + proteinPeptide.getCoverage().toString());
                            array.set(lastIndex, array.get(lastIndex) + proteinPeptide.getCounter());
                        }
                    }
                }
            }
        }
    return peptideMatrix;
    }
}
