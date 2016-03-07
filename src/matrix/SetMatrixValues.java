/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package matrix;

import collections.ProteinPeptideCollection;
import java.util.ArrayList;
import java.util.Arrays;
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
     * @param proteinPeptideMatrix matrix of protein-peptides.
     * @param sampleValueIndex maximum of COPD/Healthy samples. Highest value is used.
     * This value is needed for proper count/coverage placement when using uneven amount of samples.
     * @param datasets array with datasets.
     * @param datasetNumbers dataset name as key, number as value.
     * @param sampleList set of sample names.
     * @return HashSet with count and coverage values.
     */
    public final HashSet<ArrayList<String>> setValues(final ProteinPeptideCollection proteinPeptides,
            final HashSet<ArrayList<String>> proteinPeptideMatrix, final Integer sampleValueIndex,
            ArrayList<String> datasets, final HashMap<String,Integer> datasetNumbers, final ArrayList<String> sampleList) {
        //Go throigh all arrays for each proteinPeptide object.
        System.out.println("Adding protein-peptide data to the matrix. this may take a few minutes.");
        for (ArrayList<String> array: proteinPeptideMatrix) {
            //Set integers. Dataset size can vary and is used to deterime parameter positions.
            Integer datasetSize = datasets.size()*2;
            Integer uniqueAccessionIndex = datasetSize;
            Integer sequenceIndex = datasetSize + 1;
            Integer positionIndex = datasetSize + 2;
            Integer datasetIndex = datasetSize + 3;
            Integer uniqueGroupIndex = datasetSize + 4;
            Integer uniqueCombinedIndex = datasetSize + 5;
            Integer uniqueFastaIndex = datasetSize + 6;
            for (ProteinPeptide proteinPeptide: proteinPeptides.getProteinPeptideMatches()) {
                //Match array sequence to protein-peptide sequence to gather data from the right ProteinPeptide objects.
                if (array.get(sequenceIndex).equals(proteinPeptide.getSequence())) {
                    //Adds accession values per dataset
                    array = setAccessionValues(array, proteinPeptide, datasets, datasetNumbers);
                    //Adds unique accession values.
                    array = setUniqueAccessionValues(array, proteinPeptide, uniqueAccessionIndex);
                    //Adds position values to the array.
                    array = setPositionValues(array, proteinPeptide, positionIndex);
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
                            sampleValueIndex, datasetNumbers, sampleList);
                    //Add total count of psm's per dataset to the array.
                    array = setDatasetCounter(array, proteinPeptide, datasets, sampleList);
                    //Adds total count of psms per sample set (Healthy/COPD) to the array.
                    array = setTotalValues(array, proteinPeptide, sampleList);
                }
            }
        }
        return proteinPeptideMatrix;
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
     * @param sampleValueIndex used to determine the count/coverage index of the values for each sample.
     * @return array with corresponding count and coverage values.
     */
    private ArrayList<String> setCountAndCoverage(final ArrayList<String> array, final ProteinPeptide proteinPeptide,
            final Integer datasetSize, final Integer sampleValueIndex, final HashMap<String, Integer> datasetNumbers,
            final ArrayList<String> sampleList) {
        int sampleIndex = 0;
        int countIndex = 0;
        int totalCountIndex = 0;
        int coverageIndex = 0;
        int startIndex = datasetSize + 6; //+4 indices for @param sequence, dataset, unique to combined, unique to group
        String sample = proteinPeptide.getSample();
        //Index is based on sample number, size of the dataset and amount of samples.
        //Can add .replaceAll("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]","") to replace special characters.
        if (sample.contains(sampleList.get(0))) {
            sampleIndex = Integer.parseInt(sample.substring(sampleList.get(0).length()));
            totalCountIndex = (startIndex + sampleIndex);
            countIndex = (startIndex + sampleIndex + sampleValueIndex*2);
            coverageIndex = (startIndex + sampleIndex + sampleValueIndex*4);
        //Determine COPD indices. sampleList index 1 contains target sample (COPD)
        } else if (sample.contains(sampleList.get(1))) {
            sampleIndex = Integer.parseInt(sample.substring(sampleList.get(1).length()));
            totalCountIndex = (startIndex + sampleIndex + sampleValueIndex);
            countIndex = (startIndex + sampleIndex + sampleValueIndex*3);
            coverageIndex = (startIndex + sampleIndex + sampleValueIndex*5);
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
                if (array.get(totalCountIndex).equals("0")) {
                    array.set(totalCountIndex, proteinPeptide.getCounter().toString());
                } else {
                    Integer counter = Integer.parseInt(array.get(totalCountIndex)) + proteinPeptide.getCounter();
                    array.set(totalCountIndex, counter.toString());
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
     * @param proteinPeptide ProteinPeptide object that has a matching sequence in the array.
     * @param samples list of samples. (COPD/Healthy).
     * @param datasets list of datasets.
     * @return array to which dataset psm count was added.
     */
    public ArrayList<String> setDatasetCounter(final ArrayList<String> array, final ProteinPeptide proteinPeptide,
            final ArrayList<String> datasets, final ArrayList<String> samples) {
        for (int i = 0; i < datasets.size(); i++) {
            if (proteinPeptide.getDataset().contains(datasets.get(i))) {
                //Index starts at the total count number of the first dataset.
                int index = (array.size() - samples.size() - datasets.size()) + i;
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

    /**
     * Sets the combined total psm counting value of a sample.
     * @param array array with protein-peptide data.
     * @param proteinPeptide ProteinPeptide object that has a matching sequence in the array.
     * @param samples list of samples. (COPD/Healthy).
     * @return array with adjusted total counting values of a sample.
     */
    private ArrayList<String> setTotalValues(ArrayList<String> array, ProteinPeptide proteinPeptide, ArrayList<String> samples) {
        for (int i = 0; i < samples.size(); i++) {
            if (proteinPeptide.getSample().contains(samples.get(i))) {
                //Index starts at the total count number of the first dataset.
                int index = (array.size()-samples.size())+i;
                Integer dataCount = Integer.parseInt(array.get(index)) + proteinPeptide.getCounter();
                array.set(index, dataCount.toString());
            }
        }
        return array;
    }

    /**
     * Adds all unique accessions to this array index.
     * @param array array with protein-peptide data.
     * @param proteinPeptide ProteinPeptide object that has a matching sequence in the array.
     * @param uniqueAccessionIndex index of the unique accessions in the array.
     * @param datasets list of datasets.
     * @param datasetNumbers numbers corresponding to the dataset names.
     * @return array with unique accessions.
     */
    private ArrayList<String> setUniqueAccessionValues(ArrayList<String> array, ProteinPeptide proteinPeptide, Integer uniqueAccessionIndex) {
        Boolean newAccessionMatch = true;
        ArrayList<String> newAccessions = new ArrayList<>();
        ArrayList<String> accessions = new ArrayList<>();
        //Checks if one or more accessions are present (separator | is present) in the array and ProteinPeptide object.
        if (proteinPeptide.getAccession().contains("\\|")) {
            String[] newAcc = proteinPeptide.getAccession().split("\\|");
            newAccessions.addAll(Arrays.asList(newAcc));
        } else {
            newAccessions.add(proteinPeptide.getAccession());
        }
        //Checks if one or more accessions are present in the array and ProteinPeptide object.
        if (array.get(uniqueAccessionIndex).contains("\\|")) {
            String[] newAcc = array.get(uniqueAccessionIndex).split("\\|");
            accessions.addAll(Arrays.asList(newAcc));
        } else {
            accessions.add(array.get(uniqueAccessionIndex));
        }
        //Add new accessions to the array.
        for (String newAcc: newAccessions) {
            for(String acc: accessions) {
                if (newAcc.equals(acc)) {
                    newAccessionMatch = false;
                }
            }
            if (newAccessionMatch) {
                array.set(uniqueAccessionIndex, newAcc);
            }
        }
        //Return array with added accessions.
        return array;
    }

    /**
     * Adds the name of the dataset to the given array index.
     * @param array array with peptide sequence and preset values.
     * @param proteinPeptide ProteinPeptide object that has a matching sequence in the array.
     * @param positionIndex index of the position values.
     * @return array with adjusted dataset names.
     */
    private ArrayList<String> setPositionValues(ArrayList<String> array, ProteinPeptide proteinPeptide, Integer positionIndex) {
        //Add the dataset name to the array index if conditions are met.
        if (!array.get(positionIndex).contains(proteinPeptide.getPosition())) {
            array.set(positionIndex, array.get(positionIndex) + " " + proteinPeptide.getPosition());
        }
        return array;
    }
}
