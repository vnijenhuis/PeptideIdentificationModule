/*
 * @author vnijenhuis
 * @project peptide spectrum matrix quality control  * 
 * @copyrights vnijenhuis, Dr. P.I. Horvatovich  * 
 */
package tools;

import collections.PeptideCollection;
import collections.ProteinPeptideCollection;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import objects.ProteinPeptide;

/**
 *
 * @author f103013
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
        String[] path = file.split("\\\\");
        String patient = path[path.length-2];
        String dataset = path[path.length-4];
        System.out.println("Collecting peptides from " + patient + " " + dataset + "...");
        FileReader fr = new FileReader(file);
        BufferedReader bffFr = new BufferedReader(fr);
        String line;
        Integer count = 1;
        Integer uniqueFlag = 0;
        Boolean firstLine = true;
        while ((line = bffFr.readLine()) != null) {
            if (firstLine) {
                line = bffFr.readLine();
            }
            String[] data = line.split(",");
            String proteinGroup = data[0];
            String accession = data[2];
            String sequence = data[3];
            String uniqueToGroup = data[4];
            Double coverage = Double.parseDouble(data[5]);
            boolean newPeptide = true;
            ProteinPeptide proteinPeptideMatch = new ProteinPeptide(proteinGroup, accession,sequence, dataset, uniqueToGroup, uniqueFlag, count, coverage);
            if (!proteinPeptides.getPeptideMatches().isEmpty()) {
                for (ProteinPeptide proteinPeptide: proteinPeptides.getPeptideMatches()) {
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
                if (newPeptide) {
                    proteinPeptides.addPeptideMatch(proteinPeptideMatch);
                }
            } else {
                proteinPeptides.addPeptideMatch(proteinPeptideMatch);
            }
        }
        System.out.println(proteinPeptides.getPeptideMatches().size());
        return proteinPeptides;
    }
}
