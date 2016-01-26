/*
 * @author vnijenhuis
 * @project peptide spectrum matrix quality control  * 
 * @copyrights vnijenhuis, Dr. P.I. Horvatovich  * 
 */
package tools;

import collections.ProteinCollection;
import collections.ProteinPeptideCollection;
import java.util.ArrayList;
import java.util.HashSet;
import objects.Protein;
import objects.ProteinPeptide;

/**
 * Matches the peptideMatrix sequences to databases.
 * For example: uniprot, ensemble, all individual proteins.
 * @author vnijenhuis
 */
public class CombinedIndividualDatabaseMatcher {
    /**
     * 
     * @param proteinPeptides
     * @param proteins
     * @return 
     */
    public final ProteinPeptideCollection matchToIndividuals(ProteinPeptideCollection proteinPeptides, ProteinCollection proteins) {
        System.out.println("Matching sequences to combined inidividual database.");
        for (ProteinPeptide proteinPeptide: proteinPeptides.getPeptideMatches()) {
            Integer oneMatch = 0;
            for (Protein protein: proteins.getProteins()) {
                if (protein.getSequence().contains(proteinPeptide.getSequence())) {
                    oneMatch += 1;
                }
            }
            if (oneMatch == 1) {
                proteinPeptide.setUniqueCombined((proteinPeptide.getSample() + "Y/" + proteinPeptide.getSample()));
                System.out.println("Matched: " + oneMatch + " | " + proteinPeptide.toString());
            } else {
                System.out.println("Else: " + oneMatch + " | " + proteinPeptide.toString());
                proteinPeptide.setUniqueCombined("N");
                break;
            }
        }
        return proteinPeptides;
    }
}
