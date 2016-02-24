/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  * 
 */
package matcher;

import collections.PeptideCollection;
import collections.ProteinCollection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import objects.Peptide;
import objects.Protein;

/**
 * Uses multi-threading to allow for a faster collection matching.
 * @author vnijenhuis
 */
public class MultiThreadDatabaseMatcher implements Callable {
    /**
     * Collection of peptides.
     */
    private final PeptideCollection peptides;

    /**
     * Collection of proteins.
     */
    private final ProteinCollection proteins;

    /**
     * Multi-tread database matcher.
     * @param peptides peptide collection.
     * @param proteins  
     */
    public MultiThreadDatabaseMatcher(final PeptideCollection peptides, final ProteinCollection proteins) {
        this.peptides = peptides;
        this.proteins = proteins;
    }

    /**
     * Call function which matches the protein and peptide collections.
     * @return returns a peptide collection with peptides that did NOT match to the protein database. 
     */
    @Override
    public Object call() {
        PeptideCollection matchedPeptideCollection = new PeptideCollection();
        //Matches peptides to the protein database.
        for (Peptide peptide: peptides.getPeptides().subList(0, 500)) {
            for (Protein protein: proteins.getProteins().subList(0, 100)) {
                if (protein.getSequence().contains(peptide.getSequence())) {
                    matchedPeptideCollection.addPeptide(peptide);
                }
            }
        }
        //Returns the peptides that did NOT match to the protein database.
        return matchedPeptideCollection;
    }

    /**
     * Collects matched peptides and returns these peptides in a new collection.
     * @param peptides collection of peptides.
     * @param proteins collection of proteins.
     * @param threads amount of threads used.
     * @return collection of matched peptides.
     * @throws InterruptedException process was interrupted.
     * @throws ExecutionException could not execute the call function.
     */
    public PeptideCollection getMatchedPeptides(final PeptideCollection peptides, final ProteinCollection proteins, 
            final Integer threads) throws InterruptedException, ExecutionException {
        PeptideCollection finalPeptides = new PeptideCollection();
        //Creates a new execution service and sets the amount of threads to use. (if available)
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        //Executes the call function of MultiThreadDatabaseMatcher.
        Callable<PeptideCollection> callable = new MultiThreadDatabaseMatcher(peptides, proteins);
        //Collects the output from the call function
        Future<PeptideCollection> future = pool.submit(callable);
        //Adds the output to finalPeptides.
        finalPeptides = future.get();
        return finalPeptides;
    }
}
