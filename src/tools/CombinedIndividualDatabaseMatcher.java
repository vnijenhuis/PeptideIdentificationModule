/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum matrix quality control  * 
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
     * Matches peptide sequences to a combined database of individual protein sequences.
     * @param proteinPeptides collection of ProteinPeptide objects.
     * @param proteins collection of Protein objects.
     * @return ProteinPeptideCollection with adjusted values.
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
                //If only one match has been found: set flag to Y(es)
                if (proteinPeptide.getUniqueCombined().equals("") || proteinPeptide.getUniqueCombined().equals("N")) {
                    proteinPeptide.setUniqueCombined("Y");    
                }
                //Set flag to N(o) if more/less then one match was found.
            } else {
                if (!proteinPeptide.getUniqueCombined().contains("Y")) {
                    proteinPeptide.setUniqueCombined("N");
                }
            }
        }
        return proteinPeptides;
    }
}
