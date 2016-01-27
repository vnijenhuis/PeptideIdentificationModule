/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum matrix quality control  * 
 */
package objects;

/**
 * Creates a Peptide object.
 * @author vnijenhuis
 */
public class Peptide {
    /**
     * Amino acid sequence of the protein.
     */
    private final String peptideSequence;

    /**
     * Creates a DatabasePSM object.
     * @param peptideSequence contains the peptide amino acid sequence.
     */
    public Peptide(final String peptideSequence) {
        this.peptideSequence = peptideSequence;
    }

    /**
     * Returns the data base PSM sequence.
     * @return peptide amino acid sequence as String.
     */
    public final String getSequence() {
        return this.peptideSequence;
    }

    /**
     * To string function.
     * @return data base PSM object as String.
     */
    @Override
    public final String toString() {
        return "Peptide{Sequence; " + this.peptideSequence + "}";
    }
}
