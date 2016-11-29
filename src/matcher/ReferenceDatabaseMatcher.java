/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package matcher;

import collections.ProteinCollection;
import collections.ProteinPeptideCollection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import tools.Boyer;
import objects.Protein;
import objects.ProteinPeptide;

/**
 * Designed to match a collection of ProteinPeptide objects from a sample to the protein sequence database of that sample. 
 */
public class ReferenceDatabaseMatcher implements Callable {
    
    /**
     * Collection of ProteinPeptide objects.
     */
    private final ProteinPeptide currentProteinPeptide;

    /**
     * Collection of Protein objects.
     */
    private final ProteinCollection proteinCollection;

    /**
     * Multi-tread database matcher.
     *
     * @param currentProteinPeptide current ProteinPeptide object.
     * @param proteinCollection collection of Protein objects.   
     */
    public ReferenceDatabaseMatcher(final ProteinPeptide currentProteinPeptide, final ProteinCollection proteinCollection) {
        this.currentProteinPeptide = currentProteinPeptide;
        this.proteinCollection = proteinCollection;
    }
       
    /**
     * Call function which matches the protein and peptide collections.
     * @return returns a peptide collection with peptides that did NOT match to the protein database. 
     */
    @Override
    public Object call() {
        //Matches peptides to the protein database.
        int matches = 0;
        Boolean isUnique = true;
        Boyer peptideSequence = new Boyer(currentProteinPeptide.getSequence());
            for (Protein protein: proteinCollection.getProteins()) {
                if (peptideSequence.searchPattern(protein.getSequence()) && matches == 0) {
                    matches += 1;
                    isUnique = true;
                } else if (peptideSequence.searchPattern(protein.getSequence()) && matches == 1) {
                    isUnique = false;
                    break;
                }
            }
        //Returns the peptides that did NOT match to the protein database.
        currentProteinPeptide.setUniqueToDatabase(isUnique);
        return currentProteinPeptide;
    }

    /**
     * Collects matched peptides and returns these peptides in a new collection.
     * @param proteinPeptideCollection collection of peptides.
     * @param proteins collection of Protein objects.
     * @param threads amount of threads used.
     * @return collection of matched ProteinPeptide objects.
     * @throws InterruptedException process was interrupted.
     * @throws ExecutionException could not execute the call function.
     */
    public ProteinPeptideCollection getMatchedProteinPeptides(final ProteinPeptideCollection proteinPeptideCollection, final ProteinCollection proteins, 
            final Integer threads) throws InterruptedException, ExecutionException {
        ProteinPeptideCollection flaggedProteinPeptideCollection = new ProteinPeptideCollection();
        proteinPeptideCollection.sortOnPeptideSequence();
        //Creates a new execution service and sets the amount of threads to use. (if available)
        System.out.println("Using " + threads + " threads to match peptides to the reference protein database.");
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        //Executes the call function of MultiThreadDatabaseMatcher.
        int count = 0;
        for (ProteinPeptide proteinPeptide: proteinPeptideCollection.getProteinPeptideMatches()) {
            count++;
            Callable<ProteinPeptide> callable = new ReferenceDatabaseMatcher(proteinPeptide, proteins);
            //Collects the output from the call function
            Future<ProteinPeptide> future = pool.submit(callable);
            //Adds the output to finalPeptides.
            ProteinPeptide newProteinPeptide = future.get();
            flaggedProteinPeptideCollection.addProteinPeptideMatch(newProteinPeptide);
            if (count % 2000 == 0) {
                System.out.println("Matched " + count + " protein-peptide sequence entries to the reference protein database.");
            }
        }
        System.out.println(count + " we're flagged according to the matching to the referernce protein database.");
        //Shutdown command for the pool to prevent the script from running infinitely.
        pool.shutdown();
        return flaggedProteinPeptideCollection;
    }
}
