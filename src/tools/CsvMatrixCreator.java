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
 * Creates a HashSet of arrays which serves as a matrix.
 * @author vnijenhuis
 */
public class CsvMatrixCreator {
    public final HashSet<ArrayList<String>> createMatrix(final ProteinPeptideCollection proteinPeptides,
            final Integer size, final ArrayList<String> datasets) {
        ArrayList<String> newEntry;
        HashSet<ArrayList<String>> proteinPeptideMatrix = new HashSet<>();
        //Make an array of each proteinPeptide object.
        for (ProteinPeptide proteinPeptide: proteinPeptides.getProteinPeptideMatches()) {
            newEntry = new ArrayList<>();
            newEntry.add(proteinPeptide.getProteinGroup());
            newEntry.add(proteinPeptide.getAccession());
            newEntry.add(proteinPeptide.getSequence());
            newEntry.add(proteinPeptide.getUniqueGroup());
            newEntry.add(proteinPeptide.getUniqueCombined());
            newEntry.add(proteinPeptide.getDataset());
            //Add values for each sample: 1 value for count, 1 value for coverage.
            //Starting values are zero.
            for (int i = 0; i <size; i++) {
                newEntry.add("0");
                newEntry.add("0");
            }
            for (String dataset : datasets) {
                newEntry.add("0");
            }
            boolean newArray = true;
            //Checks if the matrix exists already.
            if (!proteinPeptideMatrix.isEmpty()) {
                for (ArrayList<String> entry: proteinPeptideMatrix) {
                    //Match sequences of each entry.
                    if (entry.get(2).equals(proteinPeptide.getSequence())) {
                        System.out.println(entry.get(2));
                        if (!entry.get(0).contains(proteinPeptide.getProteinGroup())) {
                            entry.set(0, entry.get(0) + "|" + proteinPeptide.getProteinGroup());
                        }
                        if (!entry.get(1).contains(proteinPeptide.getAccession())) {
                            entry.set(1, entry.get(1) + "|" + proteinPeptide.getAccession());
                        }
                        if (!entry.get(5).contains(proteinPeptide.getDataset())) {
                            entry.set(5, entry.get(5) + "|" + proteinPeptide.getDataset());
                        }
                        System.out.println(entry);
                        newArray = false;
                        break;
                    }
                }
                //Add new array to the hashset.
                if (newArray) {
                    proteinPeptideMatrix.add(newEntry);
                }
                //Add first array to the hashset.
            } else {
                proteinPeptideMatrix.add(newEntry);
            }
        }
        //Returns the matrix.
        return proteinPeptideMatrix;
    }
}

