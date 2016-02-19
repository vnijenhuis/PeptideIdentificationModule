/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  * 
 */
package matrix;

import collections.ProteinPeptideCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import objects.ProteinPeptide;

/**
 * Creates a HashSet of arrays which serves as a matrix.
 * @author vnijenhuis
 */
public class UniqueSequenceCreator {
    /**
     * Creates arrays with unique sequences and a part of the corresponding data.
     * Data will be added in the SetMatrixValues.class
     * @param proteinPeptides array for protein-peptide data.
     * @param size amount of samples per condition (COPD/Healthy)
     * @param datasets list of datasets.
     * @param datasetNumbers numbers corresponding to the dataset name.
     * @param samples list of sample names.
     * @return 
     */
    public final HashSet<ArrayList<String>> createMatrix(final ProteinPeptideCollection proteinPeptides,
            final Integer size, final ArrayList<String> datasets, final HashMap<String, Integer> datasetNumbers,
            final ArrayList<String> samples) {
        System.out.println("Creating lists to store protein-peptide data...");
        ArrayList<String> newEntry;
        HashSet<ArrayList<String>> proteinPeptideMatrix = new HashSet<>();
        //Create an array of each unique peptide sequence and add
        for (ProteinPeptide proteinPeptide: proteinPeptides.getProteinPeptideMatches()) {
            newEntry = new ArrayList<>();
            //Delimiter.
            //Creates a new entry.
            newEntry = createNewEntry(newEntry, proteinPeptide, size,datasets, datasetNumbers, samples);
            boolean newArray = true;
            if (!proteinPeptideMatrix.isEmpty()) {
                for (ArrayList<String> entry: proteinPeptideMatrix) {
                    String sequence = entry.get(6);
                    if (proteinPeptide.getSequence().equals(sequence)) {
                        newArray = false;
                    }
                }
                //Add new array to the hashset.
                if (newArray) {
                    proteinPeptideMatrix.add(newEntry);
                }
                //Add first array to the hashset.
            } else {
                proteinPeptideMatrix.add(newEntry);
            }
        }
        //Returns the matrix.
        return proteinPeptideMatrix;
    }

    /**
     * Creates a new entry with a sequence and dataset.
     * This is a unique sequence entry to which data can be added.
     * @param newEntry new array to add the sequence and dataset to.
     * @param proteinPeptide proteinPeptide object with data.
     * @param sampleSize biggest sample size of the 
     * @param datasets list with datasets.
     * @param samples list of sample names.
     * @return returns a new Array with starting values of protein-peptide data.
     */
    private ArrayList<String> createNewEntry(final ArrayList<String> newEntry, final ProteinPeptide proteinPeptide,
            final Integer sampleSize, final ArrayList<String> datasets, final HashMap<String, Integer> datasetNumbers,
            final ArrayList<String> samples) {
        //Separated indices for protein group numbers.
        for (String dataset: datasets) {
            for (Map.Entry<String, Integer> entry : datasetNumbers.entrySet()) {
                if (entry.getKey().equals(dataset)) {
                    newEntry.add(entry.getValue().toString());   
                }
            }
        }
        //Separated indices for accession IDs.
        for (String dataset: datasets) {
            for (Map.Entry<String, Integer> entry : datasetNumbers.entrySet()) {
                if (entry.getKey().equals(dataset)) {
                    newEntry.add(entry.getValue().toString());   
                }
            }
        }
        //Add sequence, dataset and standard uniqueness values.
        newEntry.add(proteinPeptide.getAccession());
        newEntry.add(proteinPeptide.getSequence());
        newEntry.add(proteinPeptide.getDataset());
        //Set default of uniqueness to N.
        newEntry.add("N");
        newEntry.add("N");
        newEntry.add("N");
        //Add array indices for psm count per individual per dataset
        for (int i = 0; i < sampleSize; i++) {
            newEntry.add("0");
        }
        //Add array indices for total psm count per individual for all datasets.
        for (int i = 0; i < sampleSize; i++) {
            newEntry.add("0");
        }
        //Add array indices for psm coverage per individual
        for (int i = 0; i < sampleSize; i++) {
            newEntry.add("0.0");
        }
        //Total count for each dataset.
        for (Map.Entry<String, Integer> dataset : datasetNumbers.entrySet()) {
            newEntry.add("0");
        }
        //Total count for each sample type (Healthy/COPD)
        for (String sample: samples) {
            newEntry.add("0");
        }
        return newEntry;
    }
}

