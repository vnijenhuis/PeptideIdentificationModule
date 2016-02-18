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
 * Sets count and coverage values for the protein-peptide matrix.
 * @author vnijenhuis
 */
public class SetMatrixValues {
    /**
     * Adds count and coverage values to the matrix.
     * @param proteinPeptides protein peptide collection.
     * @param peptideMatrix matrix of peptides.
     * @param sampleValueIndex maximum of COPD/Healthy samples. Highest value is used.
     * This value is needed for proper count/coverage placement when using uneven amount of samples.
     * @param datasets array with datasets.
     * @param datasetNumbers dataset name as key, number as value.
     * @return HashSet with count and coverage values.
     */
    public final HashSet<ArrayList<String>> setValues(final ProteinPeptideCollection proteinPeptides,
            final HashSet<ArrayList<String>> peptideMatrix, final Integer sampleValueIndex,
            ArrayList<String> datasets, final HashMap<String,Integer> datasetNumbers) {
        //Go throigh all arrays for each proteinPeptide object.
        System.out.println("Adding protein-peptide data to the matrix. this may take a few minutes.");
        for (ArrayList<String> array: peptideMatrix) {
            int arraySize = array.size();
            //Set integers. Dataset size can vary and is used to deterime parameter positions.
            Integer datasetSize = datasets.size()*2;
            Integer sequenceIndex = datasetSize;
            Integer datasetIndex = datasetSize + 1;
            Integer uniqueGroupIndex = datasetSize + 2;
            Integer uniqueCombinedIndex = datasetSize + 3;
            Integer uniqueFastaIndex = datasetSize + 4;
            for (ProteinPeptide proteinPeptide: proteinPeptides.getProteinPeptideMatches()) {
                //Match array sequence to protein-peptide sequence to gather data from the right ProteinPeptide objects.
                if (array.get(sequenceIndex).equals(proteinPeptide.getSequence())) {
                    //Adds accession values per dataset
                    array = setAccessionValues(array, proteinPeptide, datasets, datasetNumbers);
                    //Adds dataset names.
                    array = setDatasetValues(array, proteinPeptide, datasetIndex);
                    //Adds proteingroup values per dataset
                    array = setProteinGroupValues(array, proteinPeptide, datasets, datasetNumbers);
                    //Sets the uniqueness to protein group parameter to N/Y
                    array = setUniqueToGroup(array, proteinPeptide, uniqueGroupIndex, datasetIndex);
                    //Sets the uniqueness to combined fasta file parameter to N/Y
                    array = setUniqueToCombinedDatabase(array, proteinPeptide, uniqueCombinedIndex, datasetIndex);
                    //Sets the uniqueness to fasta file parameter to N/Y
                    array = setUniqueToFasta(array, proteinPeptide, uniqueFastaIndex, datasetIndex);
                    //Add count and coverage to the array.
                    array = setCountAndCoverage(array, proteinPeptide, datasetSize,
                            sampleValueIndex, datasetNumbers);
                    //Add total count of psm's per dataset to the array.
                    array = setDatasetCounter(array, arraySize, proteinPeptide, datasets);
                }
            }
        }
    return peptideMatrix;
    }

    /**
     * Sets accession values of the peptide sequence.
     * @param array array with peptide sequence and preset values.
     * @param proteinPeptide ProteinPeptide object that has a matching sequence in the array.
     * @param datasets list of datasets.
     * @param datasetNumbers dataset name as key, number as value.
     */
    private ArrayList<String> setAccessionValues(final ArrayList<String> array, final ProteinPeptide proteinPeptide,
            final ArrayList<String> datasets, HashMap<String, Integer> datasetNumbers) {
        String setSeparator = ";";
        String accessionSeparator = "|";
        //Size of the datasets is used to determine the accession id positions.
        //Accession IDs are placed next to the protein groups -> size*2.
        Integer setSize = datasets.size();
        for (int i = setSize; i < setSize*2; i+=1) {
            //Matches dataset names and gets the integer value which will be written into the array.
            for (Map.Entry<String, Integer> entry: datasetNumbers.entrySet()) {
                if (entry.getKey().equals(proteinPeptide.getDataset())) {
                    if (array.get(i).equals(entry.getValue().toString())) {
                        //Check if the value is the first value for that dataset.
                        if (array.contains(setSeparator)) {
                            array.set(i, array.get(i) + accessionSeparator + proteinPeptide.getAccession());
                        } else {
                            array.set(i, array.get(i) + setSeparator + proteinPeptide.getAccession());
                        }
                    }
                }
            }
        }
        return array;
    }

    /**
     * Sets protein group values of the peptide sequence.
     * @param array array with peptide sequence and preset values.
     * @param proteinPeptide ProteinPeptide object that has a matching sequence in the array.
     * @param datasets list of datasets.
     * @param datasetNumbers dataset name as key, number as value.
     */
    private ArrayList<String> setProteinGroupValues(ArrayList<String> array, final ProteinPeptide proteinPeptide,
            final ArrayList<String> datasets, final HashMap<String, Integer> datasetNumbers) {
        String setSeparator = ";";
        String accessionSeparator = "|";
        Integer setSize = datasets.size();
        for (int i = 0; i < setSize; i+=1) {
            //Matches dataset names and gets the integer value which will be written into the array.
            for (Map.Entry<String, Integer> entry: datasetNumbers.entrySet()) {
                if (entry.getKey().equals(proteinPeptide.getDataset())) {
                    if (array.get(i).equals(entry.getValue().toString())) {
                        //Check if the value is the first value for that dataset.
                        if (array.contains(setSeparator)) {
                            array.set(i, array.get(i) + accessionSeparator + proteinPeptide.getProteinGroup());
                        } else {
                            array.set(i, array.get(i) + setSeparator + proteinPeptide.getProteinGroup());
                        }
                    }
                }
            }
        }
        return array;
    }

    /**
     * Sets the Unique to Combined Database value to Y if requirements are met.
     * @param array array with peptide sequence and preset values.
     * @param proteinPeptide ProteinPeptide object that has a matching sequence in the array.
     * @param datasets list of datasets.
     * @param uniqueToGroupIndex index of unique values.
     * @param datasetIndex index of the dataset name(s).
     */
    private ArrayList<String> setUniqueToCombinedDatabase(ArrayList<String> array, ProteinPeptide proteinPeptide,
            final Integer index, final Integer datasetIndex) {
        if (array.get(datasetIndex).contains(proteinPeptide.getDataset())) {
            if (proteinPeptide.getUniqueToCombined().equals("Y")) {
                array.set(index, proteinPeptide.getUniqueToCombined());
            }
        }
        return array;
    }

    /**
     * Sets the Unique to Protein Group value to Y if requirements are met.
     * @param array array with peptide sequence and preset values.
     * @param proteinPeptide ProteinPeptide object that has a matching sequence in the array.
     * @param datasets list of datasets.
     * @param uniqueToGroupIndex index of unique values.
     * @param datasetIndex index of the dataset name(s).
     */
    private ArrayList<String> setUniqueToGroup(ArrayList<String> array, ProteinPeptide proteinPeptide,
            final Integer index, final Integer datasetIndex) {
        //Sets protein group uniqueness to Y if conditions are met.
        if (array.get(datasetIndex).contains(proteinPeptide.getDataset())) {
            if (proteinPeptide.getUniqueToGroup().equals("Y")) {
                array.set(index, proteinPeptide.getUniqueToGroup());
            }
        }
        return array;
    }

    /**
     * Sets the Unique to Fasta file value to Y if requirements are met.
     * @param array array with peptide sequence and preset values.
     * @param proteinPeptide ProteinPeptide object that has a matching sequence in the array.
     * @param datasets list of datasets.
     * @param uniqueToGroupIndex index of unique values.
     * @param datasetIndex index of the dataset name(s).
     * @return array with updated uniqueness.
     */
    private ArrayList<String> setUniqueToFasta(final ArrayList<String> array, final ProteinPeptide proteinPeptide,
            final Integer index, final Integer datasetIndex) {
        //Sets unique to fasta to Y if conditions are met.
        if (array.get(datasetIndex).contains(proteinPeptide.getDataset())) {
            if (proteinPeptide.getUniqueToFasta().equals("Y")) {
                array.set(index, proteinPeptide.getUniqueToFasta());
            }
        }
        return array;
    }

    /**
     * Sets count and coverage values of a ProteinPeptide object into the array.
     * @param array array with peptide sequence and preset values.
     * @param proteinPeptide ProteinPeptide object that has a matching sequence in the array.
     * @param datasetSize size of the dataset.
     * @param copdSampleSize amount of copd samples.
     * @param healthySampleSize amount of healthy (control) samples.
     * @return array with corresponding count and coverage values.
     */
    private ArrayList<String> setCountAndCoverage(final ArrayList<String> array, final ProteinPeptide proteinPeptide,
            final Integer datasetSize, final Integer copdSampleSize,
            final HashMap<String, Integer> datasetNumbers) {
        int sampleIndex = 0;
        int countIndex = 0;
        int coverageIndex = 0;
        int startIndex = datasetSize + 4; //+4 indices for @param sequence, dataset, unique to combined, unique to group
        String sample = proteinPeptide.getSample();
        //Index is based on sample number, size of the dataset and amount of samples.
        //Can add .replaceAll("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]","") to replace special characters.
        if (sample.contains("Healthy")) {
            sampleIndex = Integer.parseInt(sample.substring(7));
            countIndex = (startIndex + sampleIndex);
            coverageIndex = (startIndex + sampleIndex + copdSampleSize*2);
        //Determine COPD indices
        } else if (sample.contains("COPD")) {
            sampleIndex = Integer.parseInt(sample.substring(4));
            countIndex = (startIndex + sampleIndex + copdSampleSize);
            coverageIndex = (startIndex + sampleIndex + copdSampleSize*3);
        }
        //Matches dataset names and gets the integer value which will be written into the array.
        for (Map.Entry<String, Integer> entry: datasetNumbers.entrySet()) {
            if (entry.getKey().equals(proteinPeptide.getDataset())) {
                //Sets count values to the count index.
                if (array.get(countIndex).equals("0")) {
                    array.set(countIndex, entry.getValue() + ";" + proteinPeptide.getCounter());
                } else {
                    array.set(countIndex, array.get(countIndex) + "|" + entry.getValue() + ";" + proteinPeptide.getCounter());
                }
                //Sets coverage values to the coverage index.
                if (array.get(coverageIndex).equals("0.0")) {
                    array.set(coverageIndex, entry.getValue() + ";" + proteinPeptide.getCoverage());
                } else {
                    array.set(coverageIndex, array.get(coverageIndex) + "|" + entry.getValue() + ";" + proteinPeptide.getCoverage());
                }
            }
        }
        return array;
    }

    /**
     * Sets the total peptide psm counter of a dataset
     * @param array array with peptide sequence and preset values.
     * @param arraySize size of the array to determine at which index to place the count values.
     * @param proteinPeptide ProteinPeptide object that has a matching sequence in the array.
     * @param datasets list of datasets.
     * @return array to which dataset psm count was added.
     */
    public ArrayList<String> setDatasetCounter(final ArrayList<String> array, final Integer arraySize,
            final ProteinPeptide proteinPeptide, final ArrayList<String> datasets) {
        for (int i = 0; i < datasets.size(); i++) {
            if (proteinPeptide.getDataset().contains(datasets.get(i))) {
                //Index starts at the total count number of the first dataset.
                int index = (arraySize-datasets.size())+i;
                Integer dataCount = Integer.parseInt(array.get(index)) + proteinPeptide.getCounter();
                array.set(index, dataCount.toString());
            }
        }
        return array;
    }

    /**
     * Adds the name of the dataset to the given array index.
     * @param array array with peptide sequence and preset values.
     * @param proteinPeptide ProteinPeptide object that has a matching sequence in the array.
     * @param datasetIndex index of the dataset name value.
     * @return array with adjusted dataset names.
     */
    private ArrayList<String> setDatasetValues(ArrayList<String> array, ProteinPeptide proteinPeptide, Integer datasetIndex) {
        //Add the dataset name to the array index if conditions are met.
        if (!array.get(datasetIndex).contains(proteinPeptide.getDataset())) {
            array.set(datasetIndex, array.get(datasetIndex) + "|" + proteinPeptide.getDataset());
        }
        return array;
    }
}
