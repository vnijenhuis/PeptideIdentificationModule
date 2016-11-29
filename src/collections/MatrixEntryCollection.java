/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import objects.MatrixEntry;

/**
 * Defines a collection of MatrixEntry objects which are used to write a matrix csv file.
 *
 * @author Vikthor
 */
public class MatrixEntryCollection {

    /**
     * Creates an ArrayList for MatrixEntry objects.
     */
    private final ArrayList<MatrixEntry> matrixEntries;

    /**
     * Creates a new ArrayList instance.
     */
    public MatrixEntryCollection() {
        matrixEntries = new ArrayList<>();
    }

    /**
     * @param matrixEntry MatrixEntry object.
     */
    public final void addMatrixEntry(final MatrixEntry matrixEntry) {
        matrixEntries.add(matrixEntry);
    }

    /**
     * @param matrixEntry MatrixEntry object.
     */
    public final void removeMatrixEntry(final MatrixEntry matrixEntry) {
        matrixEntries.remove(matrixEntry);
    }

    /**
     * @return collection of MatrixEntry objects.
     */
    public final ArrayList<MatrixEntry> getMatrixEntries() {
        return matrixEntries;
    }
    
    /**
     * Compares the peptide sequence of two MatrixEntry objects.
     * 
     * @return Integer based on the comparison of the two objects.
     */
    static Comparator<MatrixEntry> getPeptideSequenceSorter() {
        return new Comparator<MatrixEntry>() {
            @Override
            public int compare(MatrixEntry o1, MatrixEntry o2) {
                return o1.getSequence().compareTo(o2.getSequence());
            }
        };
    }

    /**
     * Sorts the collection based on the peptide sequences.
     */
    public final void sortOnPeptideSequence() {
        Collections.sort(this.matrixEntries, getPeptideSequenceSorter());
    }
}
