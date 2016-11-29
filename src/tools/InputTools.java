/*
 * @author Vikthor Nijenhuis
 * @project Peptide mzIdentML Identfication Module * 
 */
package tools;

import java.io.File;

/**
 * Has functions to check the validity of a file in different ways.
 *
 * @author vnijenhuis
 */
public class InputTools {

    /**
     * Checks if the input string is an existing file.
     *
     * @param file input string which contains a path to a file.
     * @return true if string is an existing file, else false.
     * @throws IllegalArgumentException given string does not represent an existing file.
     */
    public final Boolean isFile(final String file) {
        File checkFile = new File(file);
        return checkFile.isFile();
    }

    /**
     * Check if a given file is a fasta file.
     *
     * @param file file name as string.
     * @return true if string matches to the pattern, else false.
     */
    public final Boolean isFasta(final String file) {
        if (isFile(file)) {
            return file.matches(".*\\.fa(sta)?(\\.gz)?");
        }
        return false;
    }

    /**
     * Check if the input is a .csv file.
     *
     * @param file file name as string.
     * @return true if string matches to the pattern, else false.
     */
    public final Boolean isCsv(final String file) {
        if (isFile(file)) {
            return file.matches(".*\\.csv(\\.gz)?");
        }
        return false;
    }

    /**
     * Check if the input is a .mzid file.
     *
     * @param file file name as string.
     * @return true if string matches to the pattern, else false.
     */
    public final Boolean isMzID(final String file) {
        if (isFile(file)) {
            return file.matches(".*\\.mzid(\\.gz)?");
        }
        return false;
    }

    /**
     * Tests if a file is a text file.
     *
     * @param file path to a text file.
     * @return true/false.
     */
    public final Boolean isTxtFile(final String file) {
        if (isFile(file)) {
            return file.endsWith(".txt");
        }
        return false;
    }

    /**
     * Checks if the input string is an existing directory.
     *
     * @param path to the directory as string.
     * @return true if given string is a path, else false.
     * @throws IllegalArgumentException given path is not a directory.
     */
    public final Boolean isDirectory(final String path) {
        File checkPath = new File(path);
        return checkPath.isDirectory();
    }
}
