/*
 * @author vnijenhuis
 * @project peptide spectrum matrix quality control  * 
 * @copyrights vnijenhuis, Dr. P.I. Horvatovich  * 
 */
package tools;

import collections.ProteinCollection;
import java.util.ArrayList;
import java.util.HashSet;
import objects.Protein;

/**
 * Matches the peptideMatrix sequences to databases.
 * For example: uniprot, ensemble, all individual proteins.
 * @author vnijenhuis
 */
public class DatabaseMatcher {
    /**
     * 
     * @param peptideMatrix
     * @param proteins
     * @return 
     */
    public final HashSet<ArrayList<String>> matchToDatabase(HashSet<ArrayList<String>> peptideMatrix, HashSet<ProteinCollection> proteins) {
        HashSet<ArrayList<String>> finalMatrix = new HashSet<>();
        for (ArrayList<String> array: peptideMatrix) {
            Integer oneMatch = 0;
            for (ProteinCollection collection: proteins) {
                for (Protein protein: collection.getProteins()) {
                    if (protein.getSequence().contains(array.get(2))) {
                        oneMatch += 1;
                    }
                }
                if (oneMatch == 1) {
                    array.set(0, "Y");
                } else {
                    array.set(0, "N");
                }
            }
        }
        return finalMatrix;
    }
}
