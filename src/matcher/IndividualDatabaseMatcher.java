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
public class IndividualDatabaseMatcher {
    /**
     * Matches peptide sequences to a combined database of individual protein sequences.
     * @param proteinPeptides collection of ProteinPeptide objects.
     * @param proteins collection of Protein objects.
     * @return ProteinPeptideCollection with adjusted values.
     */
    public final ProteinPeptideCollection matchToIndividual(ProteinPeptideCollection proteinPeptides, ProteinCollection proteins) {
        System.out.println("Matching sequences to individual database.");
        int count = 0;
        for (ProteinPeptide proteinPeptide: proteinPeptides.getProteinPeptideMatches()) {
            String sequence = proteinPeptide.getSequence().replaceAll("\\(\\+[0-9]+\\.[0-9]+\\)", "");
            count += 1;
            Integer oneMatch = 0;
            //Test if a protein sequence contains the peptide sequence.
            for (Protein protein: proteins.getProteins()) {
                if (protein.getSequence().contains(sequence)) {
                    oneMatch += 1;
                }
            }
            //Set uniqueness depending on the oneMatch counter.
            if (oneMatch == 1) {
                //If only one match has been found: set flag to Y(es)
                if (proteinPeptide.getUniqueToFasta().equals("") || proteinPeptide.getUniqueToFasta().equals("N")) {
                    proteinPeptide.setUniqueToFasta("Y");
                }
                //Set flag to N(o) if more/less then one match was found.
            } else {
                if (!proteinPeptide.getUniqueToFasta().contains("Y")) {
                    proteinPeptide.setUniqueToFasta("N");
                }
            }
            if (count %1000 == 0) {
                System.out.println("Matched " + count + " peptides to the individual database.");
            }
        }
        System.out.println("Matched " + proteinPeptides.getProteinPeptideMatches().size()
                + " peptides to the individual database.");
        return proteinPeptides;
    }
}
