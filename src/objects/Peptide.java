/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  * 
 */
package objects;

/**
 * Creates a Peptide object.
 * @author vnijenhuis
 */
public class Peptide {
    /**
     * Amino acid sequence of the peptide.
     */
    private final String peptideSequence;

    /**
     * Creates a peptide object.
     * @param peptideSequence contains the peptide amino acid sequence.
     */
    public Peptide(final String peptideSequence) {
        this.peptideSequence = peptideSequence;
    }

    /**
     * Returns the peptide sequence.
     * @return peptide amino acid sequence as String.
     */
    public final String getSequence() {
        return this.peptideSequence;
    }

    /**
     * To string function.
     * @return peptide object as String.
     */
    @Override
    public final String toString() {
        return "Peptide{Sequence; " + this.peptideSequence + "}";
    }
}
