/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

/**
 * Creates a database search PSM object.
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
        return "PSM{Sequence; " + this.peptideSequence + "}";
    }
}
