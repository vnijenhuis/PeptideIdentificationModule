/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package matcher;

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
     * @throws FileNotFoundException file was not found/does not exist.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is
     * already opened by another program.
     */
    public final ProteinPeptideCollection matchPeptides(final PeptideCollection peptides,
            final ProteinPeptideCollection proteinPeptides) throws FileNotFoundException, IOException {
        ProteinPeptideCollection proteinPeptideMatches = new ProteinPeptideCollection();
        System.out.println("Starting to match peptides to protein-peptide entries.");
        int count = 0;
        for (ProteinPeptide proteinPeptide : proteinPeptides.getProteinPeptideMatches()) {
            count += 1;
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
            if (count %1000 == 0) {
                System.out.println("Matched " + count + " peptides to the protein-peptides file.");
            }
        }
        System.out.println("Matched " + count + " peptides to the protein-peptides file.");
        //Returns the new collection.
        return proteinPeptideMatches;
    }
}
