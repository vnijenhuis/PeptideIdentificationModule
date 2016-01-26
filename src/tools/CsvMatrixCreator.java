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
    public final HashSet<ArrayList<String>> createMatrix(final ProteinPeptideCollection proteinPeptides, final Integer size) {
        ArrayList<String> newMatch;
        HashSet<ArrayList<String>> proteinPeptideMatrix = new HashSet<>();
        //Make an array of each proteinPeptide object.
        System.out.println("Creating protein-peptide matrix...");
        for (ProteinPeptide proteinPeptide: proteinPeptides.getPeptideMatches()) {
            newMatch = new ArrayList<>();
            newMatch.add(proteinPeptide.getProteinGroup());
            newMatch.add(proteinPeptide.getAccession());
            newMatch.add(proteinPeptide.getSequence());
            newMatch.add(proteinPeptide.getUniqueGroup());
            newMatch.add(proteinPeptide.getUniqueCombined());
            newMatch.add(proteinPeptide.getDataset());
            for (int i = 0; i <size; i++) {
                newMatch.add("0");
                newMatch.add("0");
            }
            boolean newArray = true;
            //Checks if the matrix exists already.
            if (!proteinPeptideMatrix.isEmpty()) {
                for (ArrayList<String> match: proteinPeptideMatrix) {
                    if (match.get(0).equals(newMatch.get(0))
                            && match.get(1).equals(newMatch.get(1))
                            && match.get(2).equals(newMatch.get(2))) {
                        newArray = false;
                        break;
                    }
                }
                //Add new array to the hashset.
                if (newArray) {
                    proteinPeptideMatrix.add(newMatch);
                }
                //Add first array to the hashset.
            } else {
                proteinPeptideMatrix.add(newMatch);
            }
        }
        return proteinPeptideMatrix;
    }
}
