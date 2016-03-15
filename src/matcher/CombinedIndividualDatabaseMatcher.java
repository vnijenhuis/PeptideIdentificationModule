/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package matcher;

import collections.ProteinCollection;
import collections.ProteinPeptideCollection;
import objects.Protein;
import objects.ProteinPeptide;

/**
 * Matches the peptideMatrix sequences to databases.
 * For example: uniprot, ensemble, all individual proteins.
 * @author vnijenhuis
 */
public class CombinedIndividualDatabaseMatcher {
    /**
     * Matches peptide sequences to a combined database of individual protein sequences.
     * @param proteinPeptides collection of ProteinPeptide objects.
     * @param proteins collection of Protein objects.
     * @return ProteinPeptideCollection with adjusted values.
     */
    public final ProteinPeptideCollection matchToCombined(ProteinPeptideCollection proteinPeptides,
            ProteinCollection proteins) {
        System.out.println("Matching sequences to combined database.");
        int count = 0;
        for (ProteinPeptide proteinPeptide: proteinPeptides.getProteinPeptideMatches()) {
            String sequence = proteinPeptide.getSequence().replaceAll("\\(\\+[0-9]+\\.[0-9]+\\)", "");
            count += 1;
            Integer oneMatch = 0;
            //Get accession names.
            String[] accessions = proteinPeptide.getAccession().split("\\|");
            String positions = "";
            Boolean newPosition = false;
            //Test if a protein sequence contains the peptide sequence.
            for (String accession: accessions) {
                String position = "0";
                for (Protein protein: proteins.getProteins()) {
                    if (protein.getSequence().contains(sequence)) {
                        //Check if accessions match to gather start and end positions of the peptide sequence.
                        if (accession.equals(protein.getAccession())) {
                            Integer start = protein.getSequence().indexOf(sequence) + 1;
                            Integer end = (start + sequence.length());
                            position = start + "_" + end;
                            newPosition = true;
                            break;
                        }
                        oneMatch += 1;
                    }
                }
                //Determines the position value.
                if (newPosition && positions.isEmpty()) {
                    positions += position;
                } else if (newPosition) {
                    positions += "|" + position;
                } else if (positions.isEmpty()) {
                    positions += position;
                } else {
                    positions += "|0";
                }
            }
            //Adds position values to the proteinPeptide object.
            proteinPeptide.setPosition(positions);
            //Set unique to Y if one match was found.
            if (oneMatch == 1) {
                //If only one match has been found: set flag to Y(es)
                if (proteinPeptide.getUniqueToCombined().equals("") || proteinPeptide.getUniqueToCombined().equals("N")) {
                    proteinPeptide.setUniqueToCombined("Y");
                }
                //Set flag to N(o) if more/less then one match was found.
            } else {
                if (!proteinPeptide.getUniqueToCombined().contains("Y")) {
                    proteinPeptide.setUniqueToCombined("N");
                }
            }
            if (count %1000 == 0) {
                System.out.println("Matched " + count + " peptides to the combined database.");
            }
        }
        System.out.println("Matched " + proteinPeptides.getProteinPeptideMatches().size()
                + " peptides to the combined database.");
        //Return proteinPeptide collection with adjusted uniqueness values.
        return proteinPeptides;
    }
}
