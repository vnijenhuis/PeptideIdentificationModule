/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum matrix quality control  * 
 */
package tools;

import collections.ProteinCollection;
import objects.Protein;
import collections.ProteinPeptideCollection;
import java.io.FileNotFoundException;
import java.io.IOException;
import objects.ProteinPeptide;

/**
 * Matches peptides from a txt file to a collection of proteins (uniprot for example).
 * @author vnijenhuis
 */
public class UniprotDatabaseMatcher {
    /**
     * Matches peptide sequences to a protein database.
     * @param proteins collection of database protein sequences.
     * @param proteinPeptides collection of protein-peptide objects.
     * @return protein peptide objects and their match with the database.
     * @throws FileNotFoundException file not found.
     * @throws IOException can't open file.
     */
    public final ProteinPeptideCollection matchToDatabases(final ProteinCollection proteins,
            final ProteinPeptideCollection proteinPeptides) throws FileNotFoundException, IOException {
        int cnt = 0;
        //New ProteinPeptide Collection for non matched entries.
        ProteinPeptideCollection newCollection = new ProteinPeptideCollection();
        System.out.println("Starting to match peptides to protein database.");
        for (ProteinPeptide proteinPeptide : proteinPeptides.getProteinPeptideMatches()) {
            cnt += 1;
            boolean noMatch = true;
            for (Protein protein : proteins.getProteins()) {
                // Cast object to protein and check if peptide is present
                String sequence = proteinPeptide.getSequence().replaceAll("\\(\\+[0-9]+\\.[0-9]+\\)", "");
                if (protein.getSequence().contains(sequence)) {
                        noMatch = false;
                        break;
                    }
                }
                if (noMatch) {
                    newCollection.addProteinPeptideMatch(proteinPeptide);
                }
            if (cnt % 1000 == 0) {
                System.out.println("Matched " + cnt + " peptide sequences to database.");
            }
        }
        System.out.println("Finished matching " + proteinPeptides.getProteinPeptideMatches().size() + " peptides!");
        System.out.println("Found " + newCollection.getProteinPeptideMatches().size()
                + " peptides that did not match the database.");
        //Returns the new collection of ProteinPeptide objects.
        return newCollection;
    }
}
