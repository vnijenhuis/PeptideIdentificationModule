/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package objects;

import java.util.ArrayList;

/**
 * ProteinPeptide object class.
 *
 * @author vnijenhuis
 */
public class ProteinPeptide {

    /**
     * List of protein groups found for this peptide sequence.
     */
    private ArrayList<String> proteinGroupList;

    /**
     * List that contains a list of accessions for each protein group in the proteinGroupList.
     */
    private final ArrayList<ArrayList<String>> accessionList;

    /**
     * Amino acid sequence of the peptide.
     */
    private final String sequence;

    /**
     * Unique to 1 protein group. (Y/N)
     */
    private final Boolean uniqueToGroup;

    /**
     * Unique to one protein sequence from the combined individual database. (Y/N)
     */
    private Boolean uniqueToDatabase;

    /**
     * Dataset that the peptide belongs to.
     */
    private String dataset;

    /**
     * List containing the spectra count of each peptide sequence and protein group combination.
     */
    private final ArrayList<Integer> peptideCountList;

    /**
     * List that contains a list of scores for each protein group in the proteinGroupList.
     */
    private final ArrayList<ArrayList<Double>> combinedScoreList;

    /**
     * ID of the sample. (COPD1, Healthy2 etc).
     */
    private final String sample;

    /**
     * Mass of the peptide sequence.
     */
    private final String mass;

    /**
     * Length of the peptide sequence.
     */
    private final Integer length;

    /**
     * Sample index number.
     */
    private final Integer sampleIndexNumber;

    /**
     * Creates a protein object.
     *
     * @param proteinGroupList list of protein group ids that this peptide belongs to.
     * @param accessionList list of protein accession ids.
     * @param sequence contains the peptide amino acid sequence.
     * @param dataset contains the dataset name.
     * @param sampleIndexNumber sample index number used to put data at the correct matrix index.
     * @param mass mass of the peptide sequence.
     * @param length length of the peptide sequence.
     * @param sample id of the sample, which is the file name minus the extension.
     * @param uniqueToGroup (yes/no) Y if unique to one protein group, otherwise its a N.
     * @param uniqueToDatabase unique to one sequence in the given database.
     * @param peptideCountList PSM counting number.
     */
    public ProteinPeptide(final ArrayList<String> proteinGroupList, final ArrayList<ArrayList<String>> accessionList, final String sequence, final String sample, final Integer sampleIndexNumber, final String mass, final Integer length, final Boolean uniqueToGroup, final Boolean uniqueToDatabase, final String dataset, final ArrayList<Integer> peptideCountList, final ArrayList<ArrayList<Double>> combinedScoreList) {
        this.proteinGroupList = proteinGroupList;
        this.accessionList = accessionList;
        this.sequence = sequence;
        this.sample = sample;
        this.sampleIndexNumber = sampleIndexNumber;
        this.mass = mass;
        this.length = length;
        this.uniqueToGroup = uniqueToGroup;
        this.uniqueToDatabase = uniqueToDatabase;
        this.dataset = dataset;
        this.peptideCountList = peptideCountList;
        this.combinedScoreList = combinedScoreList;
    }

    /**
     * Returns the protein group that this peptide belongs to.
     *
     * @return protein group as String.
     */
    public final ArrayList<String> getProteinGroupList() {
        return this.proteinGroupList;
    }

    /**
     * Set the protein group(s) that this peptide belongs to.
     *
     * @param proteinGroup protein group as String.
     */
    public final void addProteinGroup(final String proteinGroup) {
        this.proteinGroupList.add(proteinGroup);
    }

    /**
     * Returns an ArrayList that contains an ArrayList of accessions for each protein group in the proteinGroupList.
     *
     * @return accession id as String.
     */
    public final ArrayList<ArrayList<String>> getCombinedAccessionList() {
        return this.accessionList;
    }

    /**
     * Sets the accession id of the protein.
     *
     * @param index defines to which list the accession id is assigned to.
     * @param accession accession id as String.
     */
    public final void addAccession(final Integer index, final String accession) {
        this.accessionList.get(index).add(accession);
    }

    /**
     * Returns the peptide amino acid sequence.
     *
     * @return sequence as String.
     */
    public final String getSequence() {
        return this.sequence;
    }

    /**
     * Returns Y(yes) if a sequence is unique to a protein group.
     *
     * @return Y or N as String.
     */
    public final Boolean getUniqueToGroup() {
        return this.uniqueToGroup;
    }

    /**
     * Returns the sample ID.
     *
     * @return sample ID as String.
     */
    public final String getSample() {
        return this.sample;
    }

    /**
     * Returns the sample index number.
     *
     * @return sample index number as Integer.
     */
    public final Integer getSampleIndexNumber() {
        return this.sampleIndexNumber;
    }

    /**
     * Returns Y(yes) if a sequence is matched once to indiv. database or N(no)
     * if matched more than once.
     *
     * @return Y or N as String.
     */
    public final Boolean getUniqueToDatabase() {
        return this.uniqueToDatabase;
    }

    /**
     * Sets the uniqueCombined value to yes or no depending on the flag.
     *
     * @param flag Y or N as String.
     */
    public final void setUniqueToDatabase(final Boolean flag) {
        this.uniqueToDatabase = flag;
    }

    /**
     * Returns the name of the dataset that this peptide belongs to.
     *
     * @return dataset as String.
     */
    public final String getDataset() {
        return this.dataset;
    }

    /**
     * Adds a dataset.
     *
     * @param set dataset.
     */
    public final void addDataset(final String set) {
        this.dataset = this.dataset + "|" + set;
    }

    /**
     * Returns the number of occurrences of this peptide.
     *
     * @return counting number as Integer.
     */
    public final ArrayList<Integer> getCountList() {
        return this.peptideCountList;
    }

    /**
     * Adds a count number to the peptide occurrence counter.
     *
     * @param index index for the given count.
     * @param count counting number as Integer.
     */
    public final void addToCount(final Integer index, final Integer count) {
        Integer newCount = peptideCountList.get(index) + count;
        this.peptideCountList.set(index, newCount);
    }

    /**
     * Score identity % of the peptide sequence.
     *
     * @return score % as Double.
     */
    public final ArrayList<ArrayList<Double>> getScoreList() {
        return this.combinedScoreList;
    }

    /**
     * Sets the score of the peptide sequence.
     *
     * @param index index for the given score.
     * @param score score of the peptide sequence.
     */
    public final void addScore(final Integer index, final Double score) {
        this.combinedScoreList.get(index).add(score);
    }

    /**
     * Returns the length of the peptide sequence.
     *
     * @return length of the sequence as Integer.
     */
    public Integer getLength() {
        return length;
    }

    /**
     * Returns the mass of the peptide sequence.
     *
     * @return mass of the sequence as String.
     */
    public String getMass() {
        return this.mass;
    }

    /**
     * To string function of the ProteinPeptide object.
     *
     * @return ProteinPeptide object as string.
     */
    @Override
    public final String toString() {
        return "Protein-Peptide{Protein group; " + this.getProteinGroupList() + ", Accession; " + this.getCombinedAccessionList()
                + ", Sequence; " + this.getSequence() + ", Sample; " + this.getSample() + ", Mass; " + this.getMass() + ", Length; "
                + this.getLength() + ", Unique to group; " + this.getUniqueToGroup() + ", Unique to sample database; " + this.getUniqueToDatabase()
                + ", Dataset; " + this.getDataset() + ", psm Count; " + this.getCountList() + ", Score; " + this.getScoreList() + "}";
    }
}
