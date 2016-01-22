/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import collections.PeptideCollection;
import objects.Protein;
import collections.ProteinCollection;
import collections.ProteinPeptideCollection;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Matches peptides from a txt file to a collection of proteins (uniprot for example).
 * @author vnijenhuis
 */
public class PeptideToProteinPeptideMatcher {
    /**
     * Matches peptide sequences to a protein database.
     * @param peptides collection of DB seach psm peptides.
     * @param proteinPeptides collection of protein-peptide objects.
     * @return array of peptides.
     * @throws FileNotFoundException file not found.
     * @throws IOException can't open file.
     */
    public final ArrayList<String> matchPeptides(final PeptideCollection peptides,
            final ProteinPeptideCollection proteinPeptides) throws FileNotFoundException, IOException {
        ArrayList<String> finalPeptides = new ArrayList<>();
        ArrayList<String> uniquePeptides = new ArrayList<>();
        int cnt = 0;
        int n = 0;
        System.out.println("Starting to match peptides to protein database.");
        for (String peptide : uniquePeptides) {
            cnt += 1;
            boolean newPeptide = true;
            for (Protein protein : proteins.getProteins()) {
                // Cast object to protein and check if peptide is present
                if (protein.getAminoAcidSequence().contains(peptide)) {
                    n+=1;
                    newPeptide = false;
                    break;
                }
            }
            if (newPeptide) {
                finalPeptides.add(peptide);
            }
            if (cnt % 1000 == 0) {
                System.out.println("Matched " + cnt + " peptides to database.");
            }
        }
        System.out.println("Known peptides: " + n);
        System.out.println("Unknown : " + (cnt - n));
        System.out.println("Finished matching " + uniquePeptides.size() + " peptides!");
        return finalPeptides;
    }
    
//    final ArrayList<String> readUniquePeptideFile(final String uniquePeptideFile,
//            final ArrayList<String> uniquePeptides) throws FileNotFoundException, IOException {
//        FileReader fr = new FileReader(uniquePeptideFile);
//        BufferedReader bffFr = new BufferedReader(fr);
//        String line;
//        while ((line = bffFr.readLine()) != null) {
//            uniquePeptides.add(line);
//        }
//        return uniquePeptides;
//    }
}
