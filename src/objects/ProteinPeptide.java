/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

/**
 * ProteinPeptide object class.
 *
 * @author vnijenhuis
 */
public class ProteinPeptide {

    /**
     * ID of the group of proteins that this protein belongs to.
     */
    private final String proteinGroup;

    /**
     * Accession number of the protein that this peptide belongs to.
     */
    private String accession;

    /**
     * Amino acid sequence of the peptide.
     */
    private final String sequence;

    /**
     * Unique to 1 protein group.
     */
    private final String uniqueToGroup;

    /**
     * Unique to one individual sequence.
     */
    private final Integer flag;

    /**
     * Dataset that the peptide belongs to.
     */
    private final String dataset;

    /**
     * Occurrences of the peptide.
     */
    private Integer count;

    /**
     * Highest coverage value of the peptide.
     */
    private Double coverage;

    /**
     * Creates a protein object.
     * @param proteinGroup protein group id that this protein belongs to.
     * @param accession protein accession number(s).
     * @param sequence contains the peptide amino acid sequence.
     * @param dataSet dataset(s) that the peptide belongs to.
     * @param uniqueGroup (yes/no) Y if unique to one protein group, otherwise its a N.
     * @param unique flag to check if a peptide is present in only one sequence of the individual database.
     * @param count counting number of the sequence.
     * @param coverage
     */
    public ProteinPeptide(final String proteinGroup, final String accession, final String sequence,
            final String dataSet, final String uniqueToGroup, final Integer unique, final Integer count,
            final Double coverage) {
        this.proteinGroup = proteinGroup;
        this.accession = accession;
        this.sequence = sequence;
        this.uniqueToGroup = uniqueToGroup;
        this.flag = unique;
        this.dataset = dataSet;
        this.count = count;
        this.coverage = coverage;
    }

    /**
     * Returns the protein group that this peptide belongs to.
     * @return protein_group as String.
     */
    public final String getProteinGroup() {
        return this.proteinGroup;
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
        this.accession = (this.accession + "|" + accession);
    }

    /**
     * Returns the peptide amino acid sequence.
     * @return sequence as String.
     */
    public final String getSequence() {
        return this.sequence;
    }

    /**
     * Flag to check if a peptide is unique to a protein group.
     * @return flag Y when a peptide belongs to one protein group, or flag N when a peptide
     * belongs to multiple protein groups.
     */
    public final String getUniqueGroup() {
        return this.uniqueToGroup;
    }

    /**
     * Flag number to check in how many individual databases this peptide is found.
     * @return flag number as Integer.
     */
    public final Integer getUniqueFlag() {
        return this.flag;
    }

    /**
     * Returns the name of the dataset that this peptide belongs to.
     * @return dataset as String.
     */
    public final String getDataset() {
        return this.dataset;
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
        Integer newCount = this.count + count;
        this.count = newCount;
    }

    /**
     * Coverage identity % of the peptide sequence.
     * @return coverage % as Double.
     */
    public final Double getCoverage() {
        return this.coverage;
    }

    /**
     * Sets the coverage of the peptide sequence.
     * @param cover 
     */
    public final void setCoverage(final Double cover) {
        this.coverage = cover;
    }
    
    /**
     * To string function.
     * @return protein object as string.
     */
    @Override
    public final String toString() {
        return "Peptide{protein group; " + this.proteinGroup + " accession; " + this.accession
                + " sequence; " + this.sequence + " unique; " + this.uniqueToGroup + " flag; "
                + this.flag + " dataset; " + this.dataset + " count; " + this.count + " coverage; "
                + this.coverage + "}";
    }
}
