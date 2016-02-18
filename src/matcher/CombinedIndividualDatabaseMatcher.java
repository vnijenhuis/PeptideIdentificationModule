/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package matcher;

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
    public final ProteinPeptideCollection matchToCombined(ProteinPeptideCollection proteinPeptides,
            ProteinCollection proteins) {
        System.out.println("Matching sequences to combined database.");
        int count = 0;
        for (ProteinPeptide proteinPeptide: proteinPeptides.getProteinPeptideMatches()) {
            count += 1;
            Integer oneMatch = 0;
            //Test if a protein sequence contains the peptide sequence.
            for (Protein protein: proteins.getProteins()) {
                if (protein.getSequence().contains(proteinPeptide.getSequence())) {
                    oneMatch += 1;
                }
            }
            //Set unique to Y if one match was found.
            if (oneMatch == 1) {
                //If only one match has been found: set flag to Y(es)
                if (proteinPeptide.getUniqueToCombined().equals("") || proteinPeptide.getUniqueToCombined().equals("N")) {
                    proteinPeptide.setUniqueToCombined("Y");
                }
                //Set flag to N(o) if more/less then one match was found.
            } else {
                if (!proteinPeptide.getUniqueToCombined().contains("Y")) {
                    proteinPeptide.setUniqueToCombined("N");
                }
            }
            if (count %1000 == 0) {
                System.out.println("Matched " + count + " peptides to the combined database.");
            }
        }
        System.out.println("Matched " + proteinPeptides.getProteinPeptideMatches().size()
                + " peptides to the combined database.");
        //Return proteinPeptide collection with adjusted uniqueness values.
        return proteinPeptides;
    }
}
