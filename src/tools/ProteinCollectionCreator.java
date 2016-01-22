/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import objects.Protein;
import collections.ProteinCollection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

/**
 * Creates a collection of protein objects.
 * @author vnijenhuis
 */
public class ProteinCollectionCreator {
    /**
     * Buffered reader for faster file reading.
     */
    private BufferedReader dbReader;

    /**
     * Reads a protein fasta.gz file such as the uniprot-database.fasta.gz 
     * and creates a protein collection.
     * @param dbFile database file.
     * @return returns a collection of proteins.
     */
    public final ProteinCollection createCollection(final String dbFile) {
        ProteinCollection proteins = new ProteinCollection();
        try {
            System.out.println("Loading individual proteins...");
            File file = new File(dbFile);
            if (dbFile.contains(".gz")) {
                InputStream fileStream = new FileInputStream(file);
                InputStream gzipStream = new GZIPInputStream(fileStream);
                Reader decoder = new InputStreamReader(gzipStream, "US-ASCII");
                dbReader = new BufferedReader(decoder);
            } else {
                FileReader fr = new FileReader(file);
                dbReader = new BufferedReader(fr);
            }
            String line;
            boolean firstLine = true;
            String sequence = "";
            //Create protein objects with a sequence.
            while ((line = dbReader.readLine()) != null) {
                if (line.startsWith(">") && firstLine) {
                    firstLine = false;
                } else if (line.startsWith(">")) {
                    Protein protein = new Protein(sequence);
                    proteins.addProtein(protein);
                    sequence = "";
                } else {
                    sequence += line.trim();
                }
            }

        }   catch (FileNotFoundException ex) {
                System.out.println("File nout found: " + ex.getMessage());
        }   catch (IOException ex) {
                System.out.println("Encountered IO Exception; " + ex.getMessage());
        }
        //Return the protein collection.
        System.out.println("Loaded " + proteins.getProteins().size() + " proteins!");
        return proteins;
    }
}

