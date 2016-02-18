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
     * @param outputPath path and file to write the output file to.
     * @param datasets name of the dataset(s) to which the peptides belong.
     * @param sampleSize amount of samples.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is
     * already opened by another program.
     */
    public void generateCsvFile(HashSet<ArrayList<String>> peptideMatrix, final String outputPath,
            final ArrayList<String> datasets, final Integer sampleSize) throws IOException {
        //Create a new FileWriter instance.
        Integer size = datasets.size();
        Integer doubleSize = datasets.size()*2;
        String delimiter = ",";
        String lineEnding = "\n";
        String fileHeader = createFileHeader(delimiter, lineEnding, datasets, sampleSize);
        System.out.println("Writing data to " + outputPath);
	try (FileWriter writer = new FileWriter(outputPath)) {
            writer.append(fileHeader);
            //Write data to file, line separator="," and line ending="\n"
            for (ArrayList<String> proteinPeptide: peptideMatrix) {
                //Writes protein group data to the csv file.
                String proteinGroup = writeProteinGroupToCsv(proteinPeptide, size, delimiter);
                writer.append(proteinGroup);
                //Writes accession data to the csv file.
                String accession = writeAccessionToCsv(proteinPeptide, size, doubleSize, delimiter);
                writer.append(accession);
                //Writes uniqueness, dataset and count/coverage values to the csv file.
                String dataValues = writeDataToCsv(proteinPeptide, doubleSize, delimiter,lineEnding);
                writer.append(dataValues);
                //Write values to the data set.
            }
            //Finishes the text file writing.
            writer.flush();
            writer.close();
            System.out.println("Finished writing data to " + outputPath);
        } catch (IOException e) {
            System.out.println(e.getCause());
        }
    }

    /**
     * Creates the header of the file.
     * @param delimiter delimiter of the file.
     * @param lineEnding line ending of the file.
     * @param datasets datasets (1D25/1D50 etc.)
     * @param sampleSize amount of samples used.
     * @return header for the file.
     */
    private String createFileHeader(final String delimiter, final String lineEnding, final ArrayList<String> datasets,
           final Integer sampleSize) {
        String header = "";
        ArrayList<String> samples = new ArrayList<>();
            //Create list of samples. Might change Healthy to Control.
            samples.add("Healthy");
            samples.add("COPD");
            //Writes values to the header, line separator="," and line ending="\n"
            header += "Protein Group" + delimiter;
            header += "Accession" + delimiter;
            header += "Sequence" + delimiter;
            header += "Dataset" + delimiter;
            header += "Unique to Protein Group" + delimiter;
            header += "Unique to Combined DB" + delimiter;
            header += "Unique to fasta file" + delimiter;
            //Writes sample headers to the csv file
            for (String sample: samples) {
                for (int i = 1; i <=(sampleSize / 2); i++) {
                    //Writes the sample id to the header.
                    header += sample + i + delimiter;
                    //Writes the sample coverage % to the header.
                }
            }
            for (String sample: samples) {
                for (int i = 1; i <=(sampleSize / 2); i++) {
                    //Writes the sample id to the header.
                     header += sample.substring(0,1) + i + " -10lgP" + delimiter;
                    //Writes the sample coverage % to the header.
                }
            }
            //writes dataset names to the csv file.
            for (int i = 0; i <= datasets.size()-1; i++) {
                if (i == datasets.size()-1) {
                     header += datasets.get(i);
                } else {
                    header += datasets.get(i) + delimiter;
                }
            }
            header += lineEnding;
        return header;
    }

    /**
     * Writes protein group data to a string which is written to a csv file.
     * @param proteinPeptide array with protein-peptide data.
     * @param size size of the array.
     * @param delimiter delimiter for the csv file. (",")
     * @return string with protein group data.
     */
    private String writeProteinGroupToCsv(ArrayList<String> proteinPeptide, Integer size, final String delimiter) {
        String proteinGroup = "";
        for (int i = 0; i < size; i++) {
            if (i != size) {
                if (proteinPeptide.get(i).contains(";")) {
                    if (proteinGroup.isEmpty()) {
                        proteinGroup += proteinPeptide.get(i);
                    } else {
                        proteinGroup += " " + proteinPeptide.get(i);
                    }
                }
            }
        }
        proteinGroup += delimiter;
        return proteinGroup;
    }

    /**
     * Writes accession data to a string which is written to a csv file.
     * @param proteinPeptide array with protein-peptide data.
     * @param size size of the array.
     * @param delimiter delimiter for the csv file. (",")
     * @return string with protein accession data.
     */
    private String writeAccessionToCsv(ArrayList<String> proteinPeptide, Integer size,
            Integer doubleSize, String delimiter) {
        String accession = "";
        for (int i = size; i < doubleSize; i++) {
            if (i != doubleSize) {
                if (proteinPeptide.get(i).contains(";")) {
                    if (accession.isEmpty()) {
                        accession += proteinPeptide.get(i);
                    } else {
                        accession += " " + proteinPeptide.get(i);
                    }
                }
            }
        }
        accession += delimiter;
        return accession;
    }

    /**
     * Writes accession data to a string which is written to a csv file.
     * @param proteinPeptide array with protein-peptide data.
     * @param size size of the array.
     * @param delimiter delimiter for the csv file. (",")
     * @param lineEnding end of a line.
     * @return string with uniqueness, dataset, count and coverage data.
     */
    private String writeDataToCsv(ArrayList<String> proteinPeptide, Integer doubleSize, String delimiter, String lineEnding) {
        String data = "";
        for (int i = doubleSize; i <proteinPeptide.size(); i++) {
            if (i == proteinPeptide.size()) {
                data += proteinPeptide.get(i);
            } else {
                 data += proteinPeptide.get(i) + delimiter;
            }
        }
        data += lineEnding;
        return data;
    }
}
