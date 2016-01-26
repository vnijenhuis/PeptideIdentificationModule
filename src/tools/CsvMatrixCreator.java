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
public class CsvMatrixCreator {
    public final HashSet<ArrayList<String>> createMatrix(final ProteinPeptideCollection proteinPeptides, 
            final HashSet<ArrayList<String>> peptideMatrix, final Integer size) {
        ArrayList<String> data;
        //Make an array of each proteinPeptide object.
        for (ProteinPeptide proteinPeptide: proteinPeptides.getPeptideMatches()) {
            data = new ArrayList<>();
            data.add(proteinPeptide.getProteinGroup());
            data.add(proteinPeptide.getAccession());
            data.add(proteinPeptide.getSequence());
            data.add(proteinPeptide.getUniqueGroup());
            data.add(proteinPeptide.getFlag());
            data.add(proteinPeptide.getUniqueCombined());
            data.add(proteinPeptide.getDataset());
            for (int i = 0; i <size; i++) {
                data.add("0");
                data.add("0");
            }
            boolean newArray = true;
            //Checks if the matrix exists already.
            if (!peptideMatrix.isEmpty()) {
                for (ArrayList<String> array: peptideMatrix) {
                    if (array.get(0).equals(data.get(0))
                            && array.get(1).equals(data.get(1))
                            && array.get(2).equals(data.get(2))) {
                        newArray = false;
                        break;
                    }
                } 
                if (newArray) {
                    peptideMatrix.add(data);
                }
            } else {
                peptideMatrix.add(data);
            }
        }
        return peptideMatrix;
    }
}

