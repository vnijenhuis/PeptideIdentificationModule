/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  * 
 */
package object.matcher;

import collections.PeptideCollection;
import collections.ProteinPeptideCollection;
import java.io.FileNotFoundException;
import java.io.IOException;
import objects.Peptide;
import objects.ProteinPeptide;

/**
 * Matches peptides from a txt file to a collection of proteins (uniprot for example).
 * @author vnijenhuis
 */
public class PeptideToProteinPeptideMatcher {
    /**
     * Matches peptide sequences to a protein database.
     * @param peptides collection of DB seach psm peptides.
     * @param proteinPeptides collection of protein-peptide objects.
     * @return array of peptides.
     * @throws FileNotFoundException file not found.
     * @throws IOException can't open file.
     */
    public final ProteinPeptideCollection matchPeptides(final PeptideCollection peptides,
            final ProteinPeptideCollection proteinPeptides) throws FileNotFoundException, IOException {
        ProteinPeptideCollection proteinPeptideMatches = new ProteinPeptideCollection();
        System.out.println("Starting to match peptides to protein-peptide entries.");
        for (ProteinPeptide proteinPeptide : proteinPeptides.getProteinPeptideMatches()) {
            boolean newMatch = false;
            for (Peptide peptide : peptides.getPeptides()) {
                //Test if peptide sequences are equal to each other.
                if (proteinPeptide.getSequence().equals(peptide.getSequence())) {
                    newMatch = true;
                    break;
                }
            }
            //Adds matches to a new collection.
            if (newMatch) {
                proteinPeptideMatches.addProteinPeptideMatch(proteinPeptide);
            }
        }
        System.out.println("Finished matching " + proteinPeptideMatches.getProteinPeptideMatches().size() + " peptides!");
        //Returns the new collection.
        return proteinPeptideMatches;
    }
}
