/*
 * @author vnijenhuis
 * @project peptide spectrum matrix quality control  * 
 * @copyrights vnijenhuis, Dr. P.I. Horvatovich  * 
 */
package collections;

import java.util.HashSet;
import objects.ProteinPeptide;

/**
 * Creates a ProteinPeptide match collection.
 * @author vnijenhuis
 */
public class ProteinPeptideCollection {
    /**
     * Creates a HashSet for ProteinPeptide objects.
     */
    private final HashSet<ProteinPeptide> matches;

    /**
     * Creates a new HashSet.
     */
    public ProteinPeptideCollection() {
        matches = new HashSet();
    }

    /**
     * Adds peptide objects to the HashSet.
     * @param proteinPeptideMatch protein.
     */
    public final void addPeptideMatch(final ProteinPeptide proteinPeptideMatch) {
        matches.add(proteinPeptideMatch);
    }
    
    /**
     * Removes peptide values from the HashSet.
     * @param proteinPeptideMatch protein.
     */
    public final void removePeptideMatch(final ProteinPeptide proteinPeptideMatch) {
        matches.remove(proteinPeptideMatch);
    }
    /**
     * Returns the HashSet.
     * @return HashSet of peptides.
     */
    public final HashSet<ProteinPeptide> getPeptideMatches() {
        return matches;
    }
}
