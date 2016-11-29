/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    
        
    /**
     * Sorts this collection based on the peptide sequence.
     *
     * @return integer based on comparator.
     */
    static Comparator<ProteinPeptide> getPeptideSequenceSorter() {
        return new Comparator<ProteinPeptide>() {
            @Override
            public int compare(ProteinPeptide o1, ProteinPeptide o2) {
                return o1.getSequence().compareTo(o2.getSequence());
            }
        };
    }

    /**
     * Sorts the collection based on the peptide sequence.
     */
    public final void sortOnPeptideSequence() {
        Collections.sort(this.matches, getPeptideSequenceSorter());
    }
}
