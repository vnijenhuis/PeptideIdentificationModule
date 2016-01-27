/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum matrix quality control  * 
 */
package tools;

import collections.ProteinPeptideCollection;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import objects.ProteinPeptide;

/**
 * Creates a protein-peptide collection.
 * @author vnijenhuis
 */
public class ProteinPeptideCollectionCreator {
    /**
     * Creates a collection of the protein-peptide.csv file;
     * @param file protein-peptide.csv file.
     * @return collection of ProteinPeptide objects.
     * @throws FileNotFoundException couldn't find the protein-peptide.csv file.
     * @throws IOException could not find or open the specified file or directory.
     */
    public final ProteinPeptideCollection createCollection(final String file) throws FileNotFoundException, IOException {
        ProteinPeptideCollection proteinPeptides = new ProteinPeptideCollection();
        //Creates dataset and patient names depending on the map names.
        String[] path = file.split("\\\\");
        String patient = path[path.length-2];
        String dataset = path[path.length-4];
        System.out.println("Collecting protein-peptides from " + patient + " " + dataset + "...");
        // Load the file.
        FileReader fr = new FileReader(file);
        BufferedReader bffFr = new BufferedReader(fr);
        String line;
        Integer count = 1;
        String uniqueCombined = "";
        Boolean firstLine = true;
        //Read the file.
        while ((line = bffFr.readLine()) != null) {
            if (firstLine) {
                line = bffFr.readLine();
                firstLine = false;
            }
            //Assign data to variables.
            String[] data = line.split(",");
            String proteinGroup = data[0];
            String accession = data[2];
            String sequence = data[3];
            //Remove first and last 2 indices.
            sequence = sequence.replaceAll("\\.[A-Z]$", "");
            sequence = sequence.replaceAll("^[A-Z]\\.", "");
            //Possibility to remove (+15.99) values from peptides
//            sequence = sequence.replaceAll("\\(\\+[0-9]+\\.[0-9]+\\)", "");
            String uniqueToGroup = data[4];
            Double coverage = Double.parseDouble(data[5]);
            boolean newPeptide = true;
            ProteinPeptide match = new ProteinPeptide(proteinGroup, accession, sequence, patient,uniqueToGroup,
                    uniqueCombined, dataset, count, coverage);
            //Add matches to a ProteinPeptideCollection.
            if (!proteinPeptides.getProteinPeptideMatches().isEmpty()) {
                for (ProteinPeptide proteinPeptide: proteinPeptides.getProteinPeptideMatches()) {
                    if (proteinPeptide.getSequence().equals(sequence)) {
                        if ((proteinPeptide.getProteinGroup().equals(proteinGroup))) {
                            newPeptide = false;
                            proteinPeptide.setCounter(count);
                            if (!proteinPeptide.getAccession().contains(accession)) {
                                proteinPeptide.addAccession(accession);
                            }
                            if (proteinPeptide.getCoverage() < coverage) {
                                proteinPeptide.setCoverage(coverage);
                            }
                        }
                    }
                }
                //If no match was found: add new entry to collection.
                if (newPeptide) {
                    proteinPeptides.addProteinPeptideMatch(match);
                }
            } else {
                proteinPeptides.addProteinPeptideMatch(match);
            }
        }
        System.out.println("Collected " + proteinPeptides.getProteinPeptideMatches().size() + " protein-peptides from " + file);
        return proteinPeptides;
    }
}
