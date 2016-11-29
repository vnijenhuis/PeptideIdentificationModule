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
public class MatrixEntry {

    /**
     * ID of the group of proteins that this peptide belongs to.
     */
    private final ArrayList<String> proteinGroup;

    /**
     * Accession number of the protein that this peptide belongs to.
     */
    private final ArrayList<String> accessionList;

    /**
     * Amino acid sequence of the peptide.
     */
    private final String sequence;

    /**
     * Unique to 1 protein group. (Y/N)
     */
    private Boolean uniqueToProteinGroup;

    /**
     * Unique to one protein sequence from the combined individual database. (Y/N)
     */
    private Boolean uniqueToSampleDatabase;

    /**
     * Dataset that the peptide belongs to. (1D25CM, 1D50CM, 2DLCMSMS.)
     */
    private final String dataset;

    /**
     * Highest score value of the peptide.
     */
    private final ArrayList<Double> scoreList;

    /**
     * List of counts for the given peptide sequence for each sample.
     */
    private final ArrayList<Integer> sampleCountIndexList;

    /**
     * Mass of the peptide sequence.
     */
    private final String mass;

    /**
     * Length of the peptide sequence.
     */
    private final Integer length;

    /**
     * Creates a ProteinPeptide object.
     *
     * @param proteinGroup protein group id that this protein belongs to.
     * @param accessionList protein accession number(s).
     * @param sequence contains the peptide amino acid sequence.
     * @param dataset dataset(s) that the peptide belongs to.
     * @param mass mass of the peptide sequence.
     * @param length length of the peptide sequence.
     * @param uniqueToProteinGroup true if unique to one protein group.
     * @param sampleCountIndexList sample id (COPD1, Healthy4 etc)
     * @param uniqueToDatabase true if unique to one sequence in the given database.
     * @param count PSM counting number.
     * @param scoreList of the peptide sequence.
     */
    public MatrixEntry(final ArrayList<String> proteinGroup, final ArrayList<String> accessionList, final String sequence, final ArrayList<Integer> sampleCountIndexList, final String mass, final Integer length, final Boolean uniqueToProteinGroup, final Boolean uniqueToDatabase, final String dataset, final Integer count, final ArrayList<Double> scoreList) {
        this.proteinGroup = proteinGroup;
        this.accessionList = accessionList;
        this.sequence = sequence;
        this.sampleCountIndexList = sampleCountIndexList;
        this.mass = mass;
        this.length = length;
        this.uniqueToProteinGroup = uniqueToProteinGroup;
        this.uniqueToSampleDatabase = uniqueToDatabase;
        this.dataset = dataset;
        this.scoreList = scoreList;
    }

    /**
     * Returns the protein group that this peptide belongs to.
     *
     * @return protein group as String.
     */
    public final ArrayList<String> getProteinGroupList() {
        return this.proteinGroup;
    }

    /**
     * Set the protein group(s) that this peptide belongs to.
     *
     * @param proteinGroup protein group as String.
     */
    public final void addProteinGroup(final String proteinGroup) {
        this.proteinGroup.add(proteinGroup);
    }

    /**
     * Returns the accession id of the protein.
     *
     * @return accession id as String.
     */
    public final ArrayList<String> getAccessionList() {
        return this.accessionList;
    }

    /**
     * Sets the accession id of the protein.
     *
     * @param accession accession id as String.
     */
    public final void addAccession(final String accession) {
        this.accessionList.add(accession);
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
    public final Boolean isUniqueToProteinGroup() {
        return this.uniqueToProteinGroup;
    }

    public void setUniqueToGroup(Boolean uniqueFlag) {
        this.uniqueToProteinGroup = uniqueFlag;
    }

    /**
     * Returns the sample ID.
     *
     * @return sample ID as String.
     */
    public final ArrayList<Integer> getSampleIndexList() {
        return this.sampleCountIndexList;
    }

    /**
     * Returns the sample ID.
     *
     * @param index given sample index.
     * @param sampleCount given spectra count of the sequence.
     */
    public final void setCountAtSampleIndex(final Integer index, final Integer sampleCount) {
        this.sampleCountIndexList.set(index, sampleCount);
    }

    /**
     * Returns the sample ID.
     *
     * @param index given sample index.
     * @param score given score of the sample.
     */
    public final void setScoreAtIndex(final Integer index, final Double score) {
        if (this.scoreList.get(index) <= score) {
            this.scoreList.set(index, score);
        }
    }

    /**
     * Returns the sample ID.
     *
     * @param index given index to add the sample count.
     * @param count given peptide spectra count.
     */
    public final void addCountToSampleIndex(final Integer index, final Integer count) {
        Integer oldCount = this.sampleCountIndexList.get(index);
        Integer newCount = oldCount + count;
        this.sampleCountIndexList.set(index, newCount);
    }

    /**
     * Returns Y(yes) if a sequence is matched once to the sample database or
     * N(no) if matched more than once.
     *
     * @return Y or N as String.
     */
    public final Boolean isUniqueToSampleDatabase() {
        return this.uniqueToSampleDatabase;
    }

    /**
     * Sets the uniqueToDatabase value to yes or no depending on the flag.
     *
     * @param flag Y or N as String.
     */
    public final void setUniqueToSampleDatabase(final Boolean flag) {
        this.uniqueToSampleDatabase = flag;
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
     * Score identity % of the peptide sequence.
     *
     * @return score % as Double.
     */
    public final ArrayList<Double> getScoreList() {
        return this.scoreList;
    }

    /**
     * Sets the score of the peptide sequence.
     *
     * @param score score of the peptide sequence.
     */
    public final void addScore(final Double score) {
        this.scoreList.add(score);
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
        return "MatrixEntry{Protein group; " + this.proteinGroup + ", Accession; " + this.accessionList
                + ", Sequence; " + this.sequence + ", Sample count list; " + this.sampleCountIndexList + ", Mass; " + this.mass + ", Length; "
                + this.length + ", Unique to group; " + this.uniqueToProteinGroup + ", Unique to combined; "
                + this.uniqueToSampleDatabase + ", Dataset; " + this.dataset + ", Score; " + this.scoreList + "}";
    }
}
