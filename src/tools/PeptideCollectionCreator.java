/*
 * @author vnijenhuis
 * @project peptide spectrum matrix quality control  * 
 * @copyrights vnijenhuis, Dr. P.I. Horvatovich  * 
 */
package tools;

import collections.PeptideCollection;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import objects.Peptide;

/**
 * Read the text file(s) and save them inside a peptide collection.
 * @author vnijenhuis
 */
public class PeptideCollectionCreator {
    /**
     * 
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public final PeptideCollection createCollection(final String file) throws FileNotFoundException, IOException {
        // Read the file
        PeptideCollection peptides = new PeptideCollection();
        String[] path = file.split("\\\\");
        String patient = path[path.length-2];
        String dataSet = path[path.length-4];
        System.out.println("Collecting peptides from " + patient + " " + dataSet + "...");
        FileReader fr = new FileReader(file);
        BufferedReader bffFr = new BufferedReader(fr);
        String line;
        Integer count = 0;
        while ((line = bffFr.readLine()) != null) {
            // Split data into strings
            count +=1;
            String[] data = line.split(",");
            String sequence = data[0];
            Peptide peptide = new Peptide(sequence);
            Boolean newPeptide = true;
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
        System.out.println("Collected " + peptides.getPeptides().size() + " unique peptides from " + patient + " " + dataSet + "!");
        return peptides;
    }
}
