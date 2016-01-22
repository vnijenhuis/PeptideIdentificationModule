/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

/**
 * Defines a protein object.
 * @author vnijenhuis
 */
public class Protein {
    /**
     * Amino acid sequence of the protein.
     */
    private final String aminoAcidSequence;

    /**
     * Creates a protein object with a sequence.
     * @param proteinAminoAcidSequence contains the amino acid sequence.
     */
    public Protein(final String proteinAminoAcidSequence) {
        this.aminoAcidSequence = proteinAminoAcidSequence;
    }

    /**
     * Returns the amino acid sequence.
     * @return string containing the amino acid sequence.
     */
    public final String getAminoAcidSequence() {
        return this.aminoAcidSequence;
    }

    /**
     * To string function.
     * @return protein object as string.
     */
    @Override
    public final String toString() {
        return "Protein{Sequence; " + this.aminoAcidSequence + "}";
    }
}
