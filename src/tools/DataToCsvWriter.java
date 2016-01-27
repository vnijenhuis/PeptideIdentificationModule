/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum matrix quality control  * 
 */
package tools;

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
     * @param dataSet name of the data set to which the peptides belong.
     * @param sampleSize amount of samples.
     * @throws IOException can't open/find the specified file.
     */
    public void generateCsvFile(HashSet<ArrayList<String>> peptideMatrix, final String path,
            final String dataSet, final Integer sampleSize) throws IOException {
        //Create a new FileWriter instance.
	try (FileWriter writer = new FileWriter(path + "unknown_" + dataSet + "_peptide_matrix.csv")) {
            System.out.println("Writing data to text file...");
            ArrayList<String> states = new ArrayList<>();
            // Create header with line separator="," and line ending="\n"
            states.add("Healthy");
            states.add("COPD");
            //Writes values to the header, line separator="," and line ending="\n"
            writer.append("Protein Group,");
            writer.append("Accession,");
            writer.append("Sequence,");
            writer.append("Unique,");
            writer.append("Unique Combined,");
            writer.append("Dataset,");
            for (String state: states) {
                for (int i = 1; i <=(sampleSize / 2); i++) {
                    System.out.println(state + i);
                    //Writes the sample id to the header.
                    writer.append(state + i + ",");
                    //Writes the sample coverage % to the header.
                    writer.append(state.substring(0, 1) + i + " cov%,");
                }
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
