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
public class UniqueMatrixRowCreator {
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
        Integer sequenceIndex = (datasets.size()*2 + 1);
        HashSet<ArrayList<String>> proteinPeptideMatrix = new HashSet<>();
        //Create an array of each unique peptide sequence and add
        for (ProteinPeptide proteinPeptide: proteinPeptides.getProteinPeptideMatches()) {
            newEntry = new ArrayList<>();
            //Creates a new entry.
            newEntry = createNewEntry(newEntry, proteinPeptide, size,datasets, datasetNumbers, samples);
            boolean newArray = true;
            if (!proteinPeptideMatrix.isEmpty()) {
                for (ArrayList<String> entry: proteinPeptideMatrix) {
                    String sequence = entry.get(sequenceIndex);
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
     * Creates a new entry for each unique sequence. Adds unique values from the first dataset that this sequence was 
     * encountered in. The data of other datasets with a similar sequence is added in SetMatrixValues.java
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
        //A column per dataset for protein group ID's.
        for (String dataset: datasets) {
            for (Map.Entry<String, Integer> entry : datasetNumbers.entrySet()) {
                if (entry.getKey().equals(dataset)) {
                    newEntry.add(entry.getValue().toString());   
                }
            }
        }
        //A column per dataset for accession ID's.
        for (String dataset: datasets) {
            for (Map.Entry<String, Integer> entry : datasetNumbers.entrySet()) {
                if (entry.getKey().equals(dataset)) {
                    newEntry.add(entry.getValue().toString());   
                }
            }
        }
        //Column for unique accessions. First set of unique accessions from one dataset is added already.
        newEntry.add(proteinPeptide.getAccession());
        //Column for the peptide sequence.
        newEntry.add(proteinPeptide.getSequence());
        //Column for dataset names. First name is added already.
        newEntry.add(proteinPeptide.getDataset());
        //Column for the 3 uniqueness values.
        newEntry.add("N");
        newEntry.add("N");
        newEntry.add("N");
        //Columns for the total psm count per individual.
        for (int i = 0; i < sampleSize; i++) {
            newEntry.add("0");
        }
        //Columns for the psm count per individual for all datasets.
        for (int i = 0; i < sampleSize; i++) {
            newEntry.add("0");
        }
        //Columns for the psm coverage per individual
        for (int i = 0; i < sampleSize; i++) {
            newEntry.add("0.0");
        }
        //Column for the total psm count for each dataset.
        for (Map.Entry<String, Integer> dataset : datasetNumbers.entrySet()) {
            newEntry.add("0");
        }
        //Column for the total psm count for each sample type
        for (String sample: samples) {
            newEntry.add("0");
        }
        //Returns an array with indices for all data that is required.
        return newEntry;
    }
}

