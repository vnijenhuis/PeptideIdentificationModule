/*
 * @author vnijenhuis
 * @project peptide spectrum matrix quality control  * 
 * @copyrights vnijenhuis, Dr. P.I. Horvatovich  * 
 */
package tools;

import collections.ProteinCollection;
import collections.ProteinPeptideCollection;
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
        for (ProteinPeptide proteinPeptide: proteinPeptides.getProteinPeptideMatches()) {
            Integer oneMatch = 0;
            //Test if a protein sequence contains the peptide sequence.
            for (Protein protein: proteins.getProteins()) {
                if (protein.getSequence().contains(proteinPeptide.getSequence())) {
                    oneMatch += 1;
                }
            }
            //Set uniqueness depending on the oneMatch counter.
            if (oneMatch == 1) {
                if (proteinPeptide.getUniqueCombined().equals("") || proteinPeptide.getUniqueCombined().equals("N")) {
                    proteinPeptide.setUniqueCombined(("Y;" + proteinPeptide.getSample()));    
                } else if (!proteinPeptide.getUniqueCombined().contains(proteinPeptide.getSample())) {
                    proteinPeptide.setUniqueCombined((proteinPeptide.getSample() + "|Y;" + proteinPeptide.getSample()));    
                } 
            } else {
                proteinPeptide.setUniqueCombined("N");

            }
        }
        return proteinPeptides;
    }
}
