/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package tools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class to test if files in a given path are valid.
 * @author vnijenhuis
 */
public class ValidFileChecker {
    /**
     * Checks if files exist and add them to an array for further usage.
     * @param filePath path to the file(s).
     * @param fileName string that is unique to the file name. (to prevent unnecessary input).
     * @param fileList list of files to add entries to.
     * @return ArrayList with path as String.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is
     * already opened by another program.
     */
    public ArrayList<String> checkFileValidity(final String filePath, String fileName, ArrayList<String> fileList) throws IOException {
        //Go through all folders and files in a given path.
        File file = new File(filePath);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
          });
        //Goes through all sample folders inside the RNASeq folder and creates a list of files.
        for (String sample: directories) {
            File path = new File(filePath + sample);
            for (File f: path.listFiles()) {
                if (f.toString().contains(fileName)) {
                    System.out.println("Found " + f);
                    fileList.add(f.toString());
                }
            }
        }
        return fileList;
    }

    /**
     * Checks if the input string is an existing file.
     * @param file input string which contains a path to a file.
     * @return file as string.
     * @throws IllegalArgumentException not a file.
     */
    public final String isFile(final String file){
        File checkFile = new File(file);
        if (!checkFile.isFile()) {
            throw new IllegalArgumentException("Invalid file found:" + file);
        }
        return file;
    }

    /**
     * Checks if the input string is an existing directory.
     * @param path path of the directory.
     * @return path as String.
     * @throws IllegalArgumentException not a directory.
     */
    public final String isDirectory(final String path){
        File checkPath = new File(path);
        if (!checkPath.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory found: " + path);
        }
        return path;
    }

    /**
     * Gets fasta database files.
     * @param path path to the fasta database files.
     * @param fileList list of files.
     * @return
     */
    public final ArrayList<String> getFastaDatabaseFiles(String path, ArrayList<String> fileList) {
        File filePath = new File(path);
        for (File f: filePath.listFiles()) {
            //match to any database.fa(sta) files with COPD/Healthy as sample name.
            if (f.toString().matches(".*(COPD|Healthy)_?\\d{1,}.*_database.fa(sta)?")) {
                System.out.println("Found " + f);
                fileList.add(f.toString());
            }
        }
        return fileList;
    }
}
