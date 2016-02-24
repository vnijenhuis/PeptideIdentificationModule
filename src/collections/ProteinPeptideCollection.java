/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package collections;

import java.util.ArrayList;
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
    private final ArrayList<ProteinPeptide> matches;

    /**
     * Creates a new HashSet.
     */
    public ProteinPeptideCollection() {
        matches = new ArrayList();
    }

    /**
     * Adds ProteinPeptide object to the HashSet.
     * @param proteinPeptideMatch ProteinPeptide object.
     */
    public final void addProteinPeptideMatch(final ProteinPeptide proteinPeptideMatch) {
        matches.add(proteinPeptideMatch);
    }

    /**
     * Removes ProteinPeptide value from the HashSet.
     * @param proteinPeptideMatch ProteinPeptide.
     */
    public final void removeProteinPeptideMatch(final ProteinPeptide proteinPeptideMatch) {
        matches.remove(proteinPeptideMatch);
    }
    /**
     * Returns the HashSet.
     * @return HashSet of ProteinPeptides.
     */
    public final ArrayList<ProteinPeptide> getProteinPeptideMatches() {
        return matches;
    }
}
