/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

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
        System.out.println("Starting to match peptides to protein database.");
        for (ProteinPeptide proteinPeptide : proteinPeptides.getProteinPeptideMatches()) {
            boolean newMatch = false;
            for (Peptide peptide : peptides.getPeptides()) {
                // Cast object to protein and check if peptide is present
                if (proteinPeptide.getSequence().equals(peptide.getSequence())) {
                    newMatch = true;
                    break;
                }
            }
            if (newMatch) {
                proteinPeptideMatches.addProteinPeptideMatch(proteinPeptide);
            }
        }
        System.out.println("Finished matching " + proteinPeptideMatches.getProteinPeptideMatches().size() + " peptides!");
        return proteinPeptideMatches;
    }
}
