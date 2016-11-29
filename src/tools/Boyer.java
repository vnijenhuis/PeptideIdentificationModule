/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

/**
 * Boyer Moore algorithm for faster peptide matching.
 * @author Arne Roeters
 */
public class Boyer {
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Boyer peptideIdentification = new Boyer("PQRST");
        peptideIdentification.searchPattern("ABCPQADPQRST");
    }
    /**
     * The bad character skip array.
     */
    private final int[] shifts;
    /**
     * The radix.
     */
    private final int radix;
    /**
     * The pattern in String.
     */
    private final String pattern;

    /**
     * Constructor of the class, sets all used variables and preprocesses 
     * the pattern string.
     *
     * @param patternIn The string to searchPattern for
     */
    public Boyer(final String patternIn) {
        this.radix = 256; // set the radix
        this.pattern = patternIn; // set the pattern
        shifts = new int[radix]; // create the bad character skip array
        // sets all to -1
        for (int i = 0; i < radix; i++) {
            shifts[i] = -1;
        }
        // sets the position of the rightmost occurrence of a character in the pattern
        for (int j = 0; j < patternIn.length(); j++) {
            shifts[patternIn.charAt(j)] = j;
        }
    }

    /**
     * Searches for the pattern in the given string.
     * @param stringIn string to search in for the given pattern.
     * @return true if the pattern is found in the string else false.
     */
    public final boolean searchPattern(final String stringIn) {
        int patLen = pattern.length();
        int inLen = stringIn.length();
        int shift;
        for (int i = 0; i <= inLen - patLen; i += shift) {
            //Reset shift to zero.
            shift = 0;
            for (int j = patLen - 1; j >= 0; j--) {
                //Make a new shift if no match was found.
                if (pattern.charAt(j) != stringIn.charAt(i + j)) {
                    //Max 1, j - shifts[index] = 4 - shifts[index]
                    shift = Math.max(1, j - shifts[stringIn.charAt(i + j)]);
                    break;
                }
            }
            if (shift == 0) return true; // found
        }
        return false; // not found
    }
}
