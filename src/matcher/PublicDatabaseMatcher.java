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
import objects.Protein;
import objects.ProteinPeptide;
import tools.Boyer;

/**
 * Uses multi-threading to allow for a faster collection matching.
 * @author vnijenhuis
 */
public class PublicDatabaseMatcher implements Callable {

    /**
     * ProteinPeptide peptide sequence.
     */
    private final String proteinPeptideSequence;

    /**
     * Collection of proteins.
     */
    private final ProteinCollection proteins;

    /**
     * Multi-tread database matcher.
     * @param proteinPeptideSequence ProteinPeptide peptide sequence.
     * @param proteins collection of Protein objects.
     */
    public PublicDatabaseMatcher(final String proteinPeptideSequence, final ProteinCollection proteins) {
        this.proteinPeptideSequence = proteinPeptideSequence;
        this.proteins = proteins;
    }

    /**
     * Call function which matches the protein and peptide collections.
     * @return returns a peptide collection with peptides that did NOT match to the protein database. 
     */
    @Override
    public Object call() {
        //Matches peptides to the protein database.
        Boolean matchedToDatabase = false;
        Boyer peptideSequence = new Boyer(proteinPeptideSequence);
        for (Protein protein : proteins.getProteins()) {
            //Checks if peptide sequence is present in the given database(s).
            if (peptideSequence.searchPattern(protein.getSequence())) {
                matchedToDatabase = true;
                break;
            }
        }
        //Returns the peptides that did NOT match to the protein database.
        return matchedToDatabase;
    }

    /**
     * Collects matched ProteinPeptide objects and returns a collection of these objects.
     * @param proteinPeptideCollection collection of ProteinPeptide objects.
     * @param proteins collection of Protein objects.
     * @param threads amount of threads used.
     * @return collection of matched peptides.
     * @throws InterruptedException process was interrupted.
     * @throws ExecutionException could not execute the call function.
     */
    public ProteinPeptideCollection getMatchedProteinPeptides(final ProteinPeptideCollection proteinPeptideCollection, final ProteinCollection proteins, 
            final Integer threads) throws InterruptedException, ExecutionException {
        ProteinPeptideCollection filteredProteinPeptideCollection = new ProteinPeptideCollection();
        //Creates a new execution service and sets the amount of threads to use. (if available)
        System.out.println("Using " + threads + " threads to match peptides to the public protein database.");
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        //Executes the call function of MultiThreadDatabaseMatcher.
        int count = 0;
        for (ProteinPeptide proteinPeptide: proteinPeptideCollection.getProteinPeptideMatches()) {
            count++;
            Callable<Boolean> callable = new PublicDatabaseMatcher(proteinPeptide.getSequence(), proteins);
            //Collects the output from the call function
            Future<Boolean> future = pool.submit(callable);
            //Adds the output to finalPeptides.
            Boolean matchedToDatabase = future.get();
            if (!matchedToDatabase) {
                filteredProteinPeptideCollection.addProteinPeptideMatch(proteinPeptide);
            }
            if (count % 2000 == 0) {
                System.out.println("Matched " + count + " peptide entries to the public protein database.");
            }
        }
        System.out.println(filteredProteinPeptideCollection.getProteinPeptideMatches().size() + " protein-peptides did not match to the public protein database.");
        //Shutdown command for the pool to prevent the script from running infinitely.
        pool.shutdown();
        return filteredProteinPeptideCollection;
    }
}
