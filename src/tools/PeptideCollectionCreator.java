/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum matrix quality control  * 
 */
package tools;

import collections.PeptideCollection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import objects.Peptide;

/**
 * Read the text file(s) and save them inside a peptide collection.
 * @author vnijenhuis
 */
public class PeptideCollectionCreator {
    /**
     * Creates a collection of peptide objects.
     * @param file loads a DB search psm.csv file and reads only the peptide sequences.
     * @return Collection of peptide objects.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public final PeptideCollection createCollection(final String file) throws FileNotFoundException, IOException {
        // Read the file
        PeptideCollection peptides = new PeptideCollection();
        String pattern = Pattern.quote(File.separator);
        String[] path = file.split(pattern);
        String sample = "";
        String dataset = "";
        //Creates the dataset and sample names.
        for (int i = 0; i < path.length; i++) {
            if (path[i].toLowerCase().contains("copd") || path[i].toLowerCase().contains("healthy")) {
                sample = path[i];
            } else if (path[i].toUpperCase().contains("2D") || path[i].toUpperCase().contains("1D")) {
                dataset = path[i];
            }
        }
        System.out.println("Collecting peptides from " + sample + " " + dataset + "...");
        FileReader fr = new FileReader(file);
        BufferedReader bffFr = new BufferedReader(fr);
        String line;
        int accessionIndex = 0;
        int peptideIndex = 0;
        boolean firstLine = true;
        //Reads each line in the given file.
        while ((line = bffFr.readLine()) != null) {
            if (firstLine) {
                String[] data = line.split(",");
                for (int i = 0; i < data.length; i++) {
                    if (data[i].toLowerCase().equals("peptide")) {
                        peptideIndex = i;
                    } else if (data[i].toLowerCase().contains("accession")) {
                        accessionIndex = i; 
                    }
                }
                firstLine = false;
                line = bffFr.readLine();
            }
            String[] data = line.split(",");
            String accession = data[accessionIndex];
            if (!accession.toUpperCase().matches("^ENST[0-9]+$") && !accession.toUpperCase().contains("DECOY")) {
                String sequence = data[peptideIndex];
                //Can remove (+15.99) and similar matches from a peptide sequence.
//                sequence = sequence.replaceAll("\\(\\+[0-9]+\\.[0-9]+\\)", "");
                Peptide peptide = new Peptide(sequence);
                Boolean newPeptide = true;
                //Create new peptide objects.
                if (!peptides.getPeptides().isEmpty()) {
                    for (Peptide p: peptides.getPeptides()) {
                        if (p.getSequence().equals(sequence)) {
                            newPeptide = false;
                        }
                    }
                    if (newPeptide) {
                        peptides.addPeptide(peptide);
                    }
                } else {
                    peptides.addPeptide(peptide);
                }
            }
        }
        System.out.println("Collected " + peptides.getPeptides().size() + " unique peptides from " + sample + " " + dataset + "!");
        return peptides;
    }
}
