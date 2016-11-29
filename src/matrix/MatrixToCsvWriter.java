/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matrix;

import collections.MatrixEntryCollection;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import objects.MatrixEntry;

/**
 * Writes protein-peptide data into a compact matrix.
 *
 * @author Vikthor
 */
public class MatrixToCsvWriter {

    /**
     * Writes data to a CSV file in a matrix-like layout.
     *
     * @param matrixEntryCollection collection of MatrixEntry objects.
     * @param sampleList list of sample names.
     * @param outputDirectory directory to write the output to.
     */
    public final void writeDatasetCsv(final MatrixEntryCollection matrixEntryCollection, final ArrayList<String> sampleList, final String outputDirectory) {
        String delimiter = ",";
        String lineEnding = "\n";
        String matrixHeader = createFileHeader(sampleList, delimiter, lineEnding);
        try (FileWriter writer = new FileWriter(outputDirectory)) {
            writer.append(matrixHeader);
            //Write data to file, line separator="," and line ending="\n"
            for (MatrixEntry matrixEntry: matrixEntryCollection.getMatrixEntries()) {
                String matrixRow = createMatrixRow(matrixEntry, delimiter, lineEnding);
                writer.write(matrixRow);
            }
            //Finishes the text file writing.
            writer.flush();
            writer.close();
            System.out.println("Finished writing data to " + outputDirectory);
        } catch (IOException e) {
            System.out.println(e.getCause());
        }
    }

    /**
     * Creates a CSV file header.
     *
     * @param sampleList list of samples.
     * @param delimiter file delimiter.
     * @param lineEnding file end of line.
     * @return matrix header as String.
     */
    private String createFileHeader(final ArrayList<String> sampleList, final String delimiter, final String lineEnding) {
        String matrixHeader = "";
        matrixHeader += "Protein Group" + delimiter;
        matrixHeader += "Sequence" + delimiter;
        matrixHeader += "Accessions" + delimiter;
        matrixHeader += "Unique to Protein Group" + delimiter;
        matrixHeader += "Unique to Sample Database" + delimiter;
        matrixHeader += "Dataset" + delimiter;
        for (String sample: sampleList) {
            matrixHeader += sample + " #spectra" + delimiter;
        }
        for (String sample: sampleList) {
            matrixHeader += sample + " Score" + delimiter;
        }
        matrixHeader += "Total spectra" + lineEnding;
        return matrixHeader;
    }

    /**
     * Creates a row for the CSV PeptideMatrix.
     *
     * @param matrixEntry MatrixEntry object.
     * @param delimiter file delimiter.
     * @param lineEnding file end of line.
     * @return matrix row as String.
     */
    private String createMatrixRow(final MatrixEntry matrixEntry, final String delimiter, final String lineEnding) {
        String matrixRow = "";
        String separator = ":";
        Integer totalSpectraCount = 0;
        if (matrixEntry.getProteinGroupList().size() == 1) {
            matrixRow += matrixEntry.getProteinGroupList().get(0) + delimiter;
        } else {
            for (int i = 0; i < matrixEntry.getProteinGroupList().size(); i++) {
                if (i == 0) {
                    matrixRow += matrixEntry.getProteinGroupList().get(i);
                } else if (i < matrixEntry.getProteinGroupList().size() - 1) {
                    matrixRow += separator + matrixEntry.getProteinGroupList().get(i);
                } else {
                    matrixRow += separator + matrixEntry.getProteinGroupList().get(i) + delimiter;
                }
            }
        }
        matrixRow += matrixEntry.getSequence() + delimiter;
        for (int i = 0; i < matrixEntry.getAccessionList().size(); i++) {
            String accession = matrixEntry.getAccessionList().get(i);
            if (i == matrixEntry.getAccessionList().size() - 1) {
                matrixRow += accession + delimiter;
            } else {
                matrixRow += accession + separator;            
            }
        }
        if (matrixEntry.isUniqueToProteinGroup()) {
            matrixRow += "Y" + delimiter;
        } else {
            matrixRow += "N" + delimiter;
        }
        if (matrixEntry.isUniqueToSampleDatabase()) {
            matrixRow += "Y" + delimiter;
        } else {
            matrixRow += "N" + delimiter;
        }
        matrixRow += matrixEntry.getDataset() + delimiter;
        for (Integer spectraCount: matrixEntry.getSampleIndexList()) {
            matrixRow += spectraCount + delimiter;
            totalSpectraCount += spectraCount;
        }
        for (Double score: matrixEntry.getScoreList()) {
            if (score == 0.0) {
                matrixRow += "0.0" + delimiter;
            } else {
                matrixRow += score + delimiter;
            }
        }
        matrixRow += totalSpectraCount + lineEnding;
        return matrixRow;
    }
}
