/*
 * @author vnijenhuis
 * @project peptide spectrum matrix quality control  * 
 * @copyrights vnijenhuis, Dr. P.I. Horvatovich  * 
 */
package collections;

import objects.Peptide;
import java.util.HashSet;

/**
 * A collection of peptide objects.
 * @author vnijenhuis
 */
public class PeptideCollection {
    /**
     * Creates a HashSet for peptide objects.
     */
    private final HashSet<Peptide>  peptides;

    /**
     * Creates a new HashSet.
     */
    public PeptideCollection() {
        peptides = new HashSet();
    }

    /**
     * Adds peptide objects to the HashSet.
     * @param peptide protein.
     */
    public final void addPeptide(final Peptide peptide) {
        peptides.add(peptide);
    }
    
    /**
     * Removes peptide values from the HashSet.
     * @param peptide protein.
     */
    public final void removePeptide(final Peptide peptide) {
        peptides.remove(peptide);
    }
    /**
     * Returns the HashSet.
     * @return HashSet of peptides.
     */
    public final HashSet<Peptide> getPeptides() {
        return peptides;
    }
    
    public final Boolean containsPeptide(Peptide peptide) {
        return peptides.contains(peptide);
    }
}
