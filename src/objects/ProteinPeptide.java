/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package objects;

/**
 * ProteinPeptide object class.
 * @author vnijenhuis
 */
public class ProteinPeptide {
    /**
     * ID of the group of proteins that this peptide belongs to.
     */
    private String proteinGroup;

    /**
     * Accession number of the protein that this peptide belongs to.
     */
    private String accession;

    /**
     * Amino acid sequence of the peptide.
     */
    private final String sequence;

    /**
     * Unique to 1 protein group. (Y/N)
     */
    private final String uniqueToGroup;

    /**
     * Unique to one protein sequence from the combined individual database. (Y/N)
     */
    private String uniqueCombined;

    /**
     * Unique to one protein sequence from the individual database. (Y/N)
     */
    private String uniqueFasta;

    /**
     * Dataset that the peptide belongs to. (1D25CM, 1D50CM, 2DLCMSMS.)
     */
    private String dataset;

    /**
     * Occurrences of the peptide.
     */
    private Integer count;

    /**
     * Highest coverage value of the peptide.
     */
    private String coverage;

    /**
     * ID of the sample. (COPD1, Healthy2 etc).
     */
    private final String sample;

    /**
     * Creates a protein object.
     * @param proteinGroup protein group id that this protein belongs to.
     * @param accession protein accession number(s).
     * @param sequence contains the peptide amino acid sequence.
     * @param dataset dataset(s) that the peptide belongs to.
     * @param sample sample id (COPD1, Healthy4 etc)
     * @param uniqueToGroup (yes/no) Y if unique to one protein group, otherwise its a N.
     * @param uniqueFasta unique to one sequence in the individual database.
     * @param uniqueCombined unique to one sequence in the combined individual database.
     * @param count PSM counting number.
     * @param coverage coverage % of the sequence.
     */
    public ProteinPeptide(final String proteinGroup, final String accession, final String sequence,
            final String sample, final String uniqueToGroup, final String uniqueCombined, final String uniqueFasta,
            final String dataset, final Integer count,final String coverage) {
        this.proteinGroup = proteinGroup;
        this.accession = accession;
        this.sequence = sequence;
        this.sample = sample;
        this.uniqueToGroup = uniqueToGroup;
        this.uniqueCombined = uniqueCombined;
        this.uniqueFasta = uniqueFasta;
        this.dataset = dataset;
        this.count = count;
        this.coverage = coverage;
    }

    /**
     * Returns the protein group that this peptide belongs to.
     * @return protein group as String.
     */
    public final String getProteinGroup() {
        return this.proteinGroup;
    }

    /**
     * Set the protein group(s) that this peptide belongs to.
     * @param proteinGroup protein group as String.
     */
    public final void addProteinGroup(final String proteinGroup) {
        this.proteinGroup = this.proteinGroup + "|" + proteinGroup;
    }

    /**
     * Returns the accession id of the protein.
     * @return accession id as String.
     */
    public final String getAccession() {
        return this.accession;
    }

    /**
     * Sets the accession id of the protein.
     * @param accession accession id as String.
     */
    public final void addAccession(final String accession) {
        this.accession = this.accession + "|" + accession;
    }

    /**
     * Returns the peptide amino acid sequence.
     * @return sequence as String.
     */
    public final String getSequence() {
        return this.sequence;
    }

    /**
     * Returns Y(yes) if a sequence is unique to a protein group.
     * @return Y or N as String.
     */
    public final String getUniqueToGroup() {
        return this.uniqueToGroup;
    }

    /**
     * Returns the sample ID.
     * @return sample ID as String.
     */
    public final String getSample() {
        return this.sample;
    }

    /**
     * Returns Y(yes) if a sequence is matched once to indiv. database or N(no) if matched more than once.
     * @return Y or N as String.
     */
    public final String getUniqueToCombined() {
        return this.uniqueCombined;
    }

    /**
     * Sets the uniqueCombined value to yes or no depending on the flag.
     * @param flag Y or N as String.
     */
    public final void setUniqueToCombined(final String flag) {
        this.uniqueCombined = flag;
    }

        /**
     * Returns Y(yes) if a sequence is matched once to indiv. database or N(no) if matched more than once.
     * @return Y or N as String.
     */
    public final String getUniqueToFasta() {
        return this.uniqueFasta;
    }

    /**
     * Sets the uniqueCombined value to yes or no depending on the flag.
     * @param flag Y or N as String.
     */
    public final void setUniqueToFasta(final String flag) {
        this.uniqueFasta = flag;
    }

    /**
     * Returns the name of the dataset that this peptide belongs to.
     * @return dataset as String.
     */
    public final String getDataset() {
        return this.dataset;
    }

    /**
     * Adds a dataset.
     * @param set dataset.
     */
    public final void addDataset(final String set) {
        this.dataset = this.dataset + "|" + set;
    }

    /**
     * Returns the number of occurrences of this peptide.
     * @return counting number as Integer.
     */
    public final Integer getCounter() {
        return this.count;
    }

    /**
     * Adds a count number to the peptide occurrence counter.
     * @param count counting number as Integer.
     */
    public final void setCounter(final Integer count) {
        this.count = this.count + count;
    }

    /**
     * Coverage identity % of the peptide sequence.
     * @return coverage % as Double.
     */
    public final String getCoverage() {
        return this.coverage;
    }

    /**
     * Sets the coverage of the peptide sequence.
     * @param cover
     */
    public final void addCoverage(final String cover) {
        this.coverage = this.coverage + "|" + cover;
    }

    /**
     * To string function.
     * @return protein object as string.
     */
    @Override
    public final String toString() {
        return "Protein-Peptide{protein group; " + this.proteinGroup + ", accession; " + this.accession
                + ", sequence; " + this.sequence + ", sample; " + this.sample + ", unique group; "
                + this.uniqueToGroup + ", unique combined; " + this.uniqueCombined + ", unique individual; "
                + this.uniqueCombined + ", dataset; " + this.dataset + ", count; " + this.count + ", coverage; "
                + this.coverage + "}";
    }
}
