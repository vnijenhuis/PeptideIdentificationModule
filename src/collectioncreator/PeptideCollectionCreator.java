/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  * 
 */
package collectioncreator;

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
     * @throws FileNotFoundException file was not found/does not exist.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is 
     * already opened by another program.
     */
    public final PeptideCollection createCollection(final String file) throws FileNotFoundException, IOException {
        //Create new collection of peptides.
        PeptideCollection peptides = new PeptideCollection();
        //Determines the File.separator depending on the platform.
        String pattern = Pattern.quote(File.separator);
        String[] path = file.split(pattern);
        String sample = "";
        String dataset = "";
        //Creates the dataset and sample names.
        for (String folder : path) {
            if (folder.toLowerCase().contains("copd") || folder.toLowerCase().contains("healthy")) {
                sample = folder;
            } else if (folder.toUpperCase().contains("2D") || folder.toUpperCase().contains("1D")) {
                dataset = folder;
            }
        }
        System.out.println("Collecting peptides from " + sample + " " + dataset + "...");
        FileReader reader = new FileReader(file);
        BufferedReader bffReader = new BufferedReader(reader);
        String line;
        int accessionIndex = 0;
        int peptideIndex = 0;
        boolean firstLine = true;
        //Read each line.
        while ((line = bffReader.readLine()) != null) {
            //Determines the index of parameters.
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
                line = bffReader.readLine();
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
        //Return the collection of peptides.
        System.out.println("Collected " + peptides.getPeptides().size() + " unique peptides from " + sample + " " + dataset + "!");
        return peptides;
    }
}
