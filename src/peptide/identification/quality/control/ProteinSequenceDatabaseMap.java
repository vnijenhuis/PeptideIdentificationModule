/*
 * @author Vikthor Nijenhuis
 * @project Peptide mzIdentML Identfication Module * 
 */
package peptide.identification.quality.control;

import collection.creator.ProteinFileReader;
import collections.ProteinCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Creates a HashMap of protein database fasta files.
 *
 * @author vnijenhuis
 */
public class ProteinSequenceDatabaseMap {

    /**
     * Reads a HashMap of database files and creates protein collections of the given files.
     *
     * @param databaseEntryMap HashMap with database index as key and a list of files as value.
     * @return HashMap with database index as Key and a HashMap as value. This HashMap has sample name as key and a
     * ProteinDatabaseSequenceCollection as value.
     */
    public final HashMap<String, ArrayList<ProteinCollection>> createProteinSequenceDatabaseMap(
            LinkedHashMap<String, ArrayList<String>> databaseEntryMap) {
        ProteinFileReader proteinReader = new ProteinFileReader();
        //Gather files for each index.
        HashMap<String, ArrayList<ProteinCollection>> databaseMap = new HashMap<>();
        for (Map.Entry<String, ArrayList<String>> entryMap : databaseEntryMap.entrySet()) {
            ArrayList<ProteinCollection> proteinCollectionList = new ArrayList<>();
            for (String file : entryMap.getValue()) {
                ProteinCollection proteins = new ProteinCollection();
                proteinReader.createCollection(file, proteins);
                proteinCollectionList.add(proteins);
            }
            databaseMap.put(entryMap.getKey(), proteinCollectionList);
        }
        return databaseMap;
    }
}
