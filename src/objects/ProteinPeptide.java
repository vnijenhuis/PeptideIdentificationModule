/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

/**
 * ProteinPeptide object class.
 * @author vnijenhuis
 */
public class ProteinPeptide {
    /**
     * ID of the group of proteins that this protein belongs to.
     */
    private String protein_group;
    /**
     * Amino acid sequence of the peptide.
     */
    private String sequence;

    /**
     * Accession number of the protein that this peptide belongs to.
     */
    private String accession;

    /**
     * ID of the protein that this peptide belongs to.
     */
    private String id;

    /**
     * Group number of a set of proteins which this peptide belongs to.
     */
    private String group;
    private String dataSet;

    /**
     * Array with peptide values.
     */
//    private final ArrayList peptideObject;

    /**
     * Creates a protein object.
     * @param accession contains the protein id.
     * @param sequence contains the amino acid sequence.
     * @param dataSet dataset that the peptide belongs to.
     */
    public ProteinPeptide(final String accession, final String sequence, final String dataSet) {
        //Add individual data.
        this.accession = accession;
        this.sequence = sequence;
        this.dataSet = dataSet;
    }
//    public final String getProteinGroup() {
//        return this.group;
//    }
//    
//    public final void setProteinGroup(final String group) {
//        this.group = group;
//    }
//    /**
//     * Gets the ID of the protein that this peptide belongs to.
//     * @return protein ID as string.
//     */
//    public final String getProteinID() {
//        return this.id;
//    }
//    
//    public final void setProteinID(final String id) {
//        this.id = id;
//    }

    /**
     * Returns the protein accession number.
     * @return accession id as string.
     */
    public final String getAccession() {
        return this.accession;
    }
    
    public final void setAccession(final String acc) {
        this.accession = acc;
    }

    /**
     * Returns the amino acid sequence of the peptide.
     * @return amino acid sequence as string.
     */
    public final String getSequence() {
        return this.sequence;
    }
    
    public final void setSequence(final String seq) {
       this.sequence = seq;
    }
    
    /**
     * Returns the data set that this peptide belongs to.
     * @return data set name as string.
     */
    public final String getDataSet() {
        return this.dataSet;
    }
    
//    /**
//     * Returns the data set that this peptide belongs to.
//     */
//    public final void setDataSet(final String data) {
//        this.dataSet = data;
//    }

    /**
     * To string function.
     * @return peptide object as string.
     */
    @Override
    public final String toString() {
        return "Peptide{protein group; " + this.group + ", protein id; " + this.id
                + ", accession; " + this.accession + ", Sequence; "
                + this.sequence + ", data set; " + this.dataSet + "}";
    }

//    public static class Peptide2 {
//
//        private final String group;
//        private final String id;
//        private final String accession;
//        private final String sequence;
//        private final String dataSet;
//
//        public Peptide2(final String protein_group, final String protein_id,
//                final String accession, final String sequence, final String dataSet) {
//            this.group = protein_group;
//            this.id = protein_id;
//            this.accession = accession;
//            this.sequence = sequence;
//            this.dataSet = dataSet;
//        }
//    }
}
