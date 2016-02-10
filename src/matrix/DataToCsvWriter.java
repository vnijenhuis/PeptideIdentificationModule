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
public class DataToCsvWriter {
    /**
     * Writes data from a ProteinPeptideCollection to a .csv file.
     * @param peptideMatrix set of arrays with protein-peptide data.
     * @param path path to write the output file to.
     * @param datasets name of the dataset(s) to which the peptides belong.
     * @param rnaSeq rnaSeq name of the dataset.
     * @param sampleSize amount of samples.
     * @throws IOException can't open/find the specified file.
     */
    public void generateCsvFile(HashSet<ArrayList<String>> peptideMatrix, final String path,
            final ArrayList<String> datasets, final String rnaSeq, final Integer sampleSize) throws IOException {
        //Create a new FileWriter instance.
        String name = "";
        for (String dataset: datasets) {
            name = name + (dataset + "_");
        }
        name = name + rnaSeq;
	try (FileWriter writer = new FileWriter(path + name + "_peptide_matrix.csv")) {
            System.out.println("Writing data to text file " + name + "_peptide_matrix.csv");
            ArrayList<String> states = new ArrayList<>();
            // Create header with line separator="," and line ending="\n"
            states.add("Healthy");
            states.add("COPD");
            //Writes values to the header, line separator="," and line ending="\n"
            writer.append("Protein Group,");
            writer.append("Accession,");
            writer.append("Sequence,");
            writer.append("Unique to Protein Group,");
            writer.append("Unique to Combined DB,");
            for (String dataset: datasets) {
                writer.append("Unique to individual;" + dataset + ",");
            }
            writer.append("Dataset,");
            for (String state: states) {
                for (int i = 1; i <=(sampleSize / 2); i++) {
                    //Writes the sample id to the header.
                    writer.append(state + i + ",");
                    //Writes the sample coverage % to the header.
                }
            }
            for (String state: states) {
                for (int i = 1; i <=(sampleSize / 2); i++) {
                    //Writes the sample id to the header.
                    writer.append(state.substring(0,1) + i + " -10lgP" + ",");
                    //Writes the sample coverage % to the header.
                }
            }
            for (String dataset: datasets) {
                writer.append(dataset + ",");
            }
            writer.append("\n");
            //Write data to file, line separator="," and line ending="\n"
            for (ArrayList<String> proteinPeptide: peptideMatrix) {
                writer.append(proteinPeptide.get(0) + ",");
                writer.append(proteinPeptide.get(1) + ",");
                writer.append(proteinPeptide.get(2) + ",");
                writer.append(proteinPeptide.get(3) + ",");
                writer.append(proteinPeptide.get(4) + ",");
                writer.append(proteinPeptide.get(5) + ",");
                //Write values to the data set.
                for (int i = 6; i <proteinPeptide.size(); i++) {
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
            System.out.println("Finished writing the text file!");
        } catch (IOException e) {
            System.out.println("Exception encountered: " + e.getMessage());
        }
    }
}