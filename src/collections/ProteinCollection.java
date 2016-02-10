/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  * 
 */
package collections;

import java.util.HashSet;
import objects.Protein;

/**
 * A collection of protein objects.
 * @author vnijenhuis
 */
public class ProteinCollection {
    /**
     * Creates a HashSet for protein objects.
     */
    private final HashSet<Protein>  proteins;

    /**
     * Creates a new HashSet.
     */
    public ProteinCollection() {
        proteins = new HashSet();
    }

    /**
     * Adds protein objects to the HashSet.
     * @param protein protein object.
     */
    public final void addProtein(final Protein protein) {
        proteins.add(protein);
    }

    /**
     * Returns the HashSet.
     * @return HashSet of protein objects.
     */
    public final HashSet<Protein> getProteins() {
        return proteins;
    }
}
