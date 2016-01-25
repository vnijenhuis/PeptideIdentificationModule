/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import collections.PeptideCollection;
import collections.ProteinCollection;
import objects.Protein;
import collections.ProteinPeptideCollection;
import java.io.FileNotFoundException;
import java.io.IOException;
import objects.Peptide;
import objects.ProteinPeptide;

/**
 * Matches peptides from a txt file to a collection of proteins (uniprot for example).
 * @author vnijenhuis
 */
public class SequenceToDatabaseMatcher {
    /**
     * Matches peptide sequences to a protein database.
     * @param proteins collection of database protein sequences.
     * @param proteinPeptides collection of protein-peptide objects.
     * @return protein peptide objects and their match with the database.
     * @throws FileNotFoundException file not found.
     * @throws IOException can't open file.
     */
    public final ProteinPeptideCollection matchPeptides(final ProteinCollection proteins,
            final ProteinPeptideCollection proteinPeptides) throws FileNotFoundException, IOException {
        int cnt = 0;
        System.out.println("Starting to match peptides to protein database.");
        for (ProteinPeptide proteinPeptide : proteinPeptides.getPeptideMatches()) {
            cnt += 1;
            for (Protein protein : proteins.getProteins()) {
                // Cast object to protein and check if peptide is present
                String sequence = proteinPeptide.getSequence().replaceAll("\\(\\+[0-9]+\\.[0-9]+\\)", "");
                if (protein.getSequence().contains(sequence)) {
                    proteinPeptide.setUniqueFlag((proteinPeptide.getFlag() + 1));
                }
            }
            if (cnt % 1000 == 0) {
                System.out.println("Matched " + cnt + " peptide sequences to database.");
            }
        }
        System.out.println("Finished matching " + proteinPeptides.getPeptideMatches().size() + " peptides!");
        return proteinPeptides;
    }
}
