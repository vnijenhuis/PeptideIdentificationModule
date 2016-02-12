/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  * 
 */
package matrix;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Writes array data to a text file.
 * @author vnijenhuis
 */
public class CsvWriter {
    /**
     * Writes data from a ProteinPeptideCollection to a .csv file.
     * @param peptideMatrix set of arrays with protein-peptide data.
     * @param path path to write the output file to.
     * @param datasets name of the dataset(s) to which the peptides belong.
     * @param rnaSeq rnaSeq name of the dataset.
     * @param sampleSize amount of samples.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is 
     * already opened by another program.
     */
    public void generateCsvFile(HashSet<ArrayList<String>> peptideMatrix, final String path,
            final ArrayList<String> datasets, final String rnaSeq, final Integer sampleSize) throws IOException {
        //Create a new FileWriter instance.
        String name = "";
        for (String dataset: datasets) {
            name = name + (dataset + "_");
        }
        if (rnaSeq.toLowerCase().contains("common")) {
            name = name + "RNASeqCommon_peptide_matrix.csv";
        } else if (rnaSeq.toLowerCase().contains("unique")) {
            name = name + "RNASeqUnique_peptide_matrix.csv";
        }
	try (FileWriter writer = new FileWriter(path + name)) {
            System.out.println("Writing data to text file " + name);
            ArrayList<String> samples = new ArrayList<>();
            //Create list of samples. Might change Healthy to Control.
            samples.add("Healthy");
            samples.add("COPD");
            //Writes values to the header, line separator="," and line ending="\n"
            writer.append("Protein Group,");
            writer.append("Accession,");
            writer.append("Sequence,");
            writer.append("Dataset,");
            writer.append("Unique to Protein Group,");
            writer.append("Unique to Combined DB,");
            for (String dataset: datasets) {
                writer.append("Unique to individual;" + dataset + ",");
            }
            //Writes sample headers to the csv file
            for (String sample: samples) {
                for (int i = 1; i <=(sampleSize / 2); i++) {
                    //Writes the sample id to the header.
                    writer.append(sample + i + ",");
                    //Writes the sample coverage % to the header.
                }
            }
            for (String sample: samples) {
                for (int i = 1; i <=(sampleSize / 2); i++) {
                    //Writes the sample id to the header.
                    writer.append(sample.substring(0,1) + i + " -10lgP" + ",");
                    //Writes the sample coverage % to the header.
                }
            }
            //writes dataset names to the csv file.
            for (String dataset: datasets) {
                writer.append(dataset + ",");
            }
            writer.append("\n");
            //Write data to file, line separator="," and line ending="\n"
            for (ArrayList<String> proteinPeptide: peptideMatrix) {
//                //Write values to the data set.
                for (int i = 0; i <proteinPeptide.size(); i++) {
                    if (i == proteinPeptide.size()-1) {
                        writer.append(proteinPeptide.get(i));
                    } else {
                        writer.append(proteinPeptide.get(i) + ",");
                    }
                }
                writer.append("\n");
            }
            //Finishes the text file writing.
            writer.flush();
            writer.close();
            System.out.println("Finished writing data to " + name);
        } catch (IOException e) {
            System.out.println("Exception encountered: " + e.getMessage());
        }
    }
}
