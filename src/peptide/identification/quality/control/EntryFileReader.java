/*
 * @author Vikthor Nijenhuis
 * @project peptide fragmentation control
 */
package peptide.identification.quality.control;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import tools.InputTools;

/**
 * Reads the entry file that contains file paths.
 *
 * @author vnijenhuis
 */
public class EntryFileReader {

    /**
     * Reads the main entry file.
     *
     * @param entry file with paths to .txt files that contains the path to .txt files that contains the path to the
     * .csv files.
     * @return LinkedHashMap with dataset name as key and file names as values.
     * @throws FileNotFoundException Could not find or open the given file.
     * @throws IOException Cannot access the given file because it is used/opened by another program.
     */
    public final ArrayList<String> readMainTextFile(final String entry) throws FileNotFoundException, IOException {
        ArrayList<String> mainEntryFileList = new ArrayList<>();
        InputTools inputCheck = new InputTools();
        //Check if given string is a valid file.
        System.out.println(entry);
        inputCheck.isTxtFile(entry);
        mainEntryFileList.add(entry);
        //Gather file names from first .txt file.
        ArrayList<String> entryList = readTextFile(mainEntryFileList);
        //Gather files names from the .txt files from the first .txt file.
        return entryList;
    }

    /**
     * Reads .txt files and adds each line to an ArrayList.
     *
     * @param entryFile .txt file with data.
     * @return ArrayList of file names.
     * @throws FileNotFoundException Could not find or open the given file.
     * @throws IOException Cannot access the given file because it is used/opened by another program.
     */
    private ArrayList<String> readTextFile(final ArrayList<String> entryList) throws FileNotFoundException, IOException {
        InputTools inputCheck = new InputTools();
        ArrayList<String> fileList = new ArrayList<>();
        for (String entry : entryList) {
            try (FileReader entryReader = new FileReader(entry); BufferedReader buffEntryReader = new BufferedReader(entryReader)) {
                String line;
                //Tests if given files are .txt files.
                while ((line = buffEntryReader.readLine()) != null) {
                    if (inputCheck.isTxtFile(line)) {
                        fileList.add(line);
                    } else {
                        fileList.add("");
                        System.out.println("WARNING: given file is not a .txt file: " + line);
                    }
                }
            }
        }
        return fileList;
    }

    /**
     * Creates a LinkedHashMap with fixed positions.
     *
     * @param entryList list of .txt files.
     * @param pattern given separator pattern.
     * @return LinkedHashMap with dataset name as key and a list file names as values.
     * @throws FileNotFoundException Could not find or open the given file.
     * @throws IOException Cannot access the given file because it is used/opened by another program.
     */
    public final LinkedHashMap<String, ArrayList<String>> createPsmCsvHashMap(final ArrayList<String> entryList,
            final String pattern) throws FileNotFoundException, IOException {
        InputTools inputCheck = new InputTools();
        LinkedHashMap<String, ArrayList<String>> csvEntryMap = new LinkedHashMap<>();
        //Creates a LinkedHashMap with data from the .txt files in finalList.
        for (String sampleFile : entryList) {
            String rnaSeq = "";
            ArrayList<String> methodFileList = new ArrayList<>();
            try (FileReader fileReader = new FileReader(sampleFile); BufferedReader buffFileReader = new BufferedReader(fileReader)) {
                String line;
                while ((line = buffFileReader.readLine()) != null) {
                    //Check if given string is a valid file.
                    String[] folders = line.split(pattern);
                    rnaSeq = folders[folders.length - 3];
                    //Warning is given if a file is not a valid .csv file.
                    if (inputCheck.isCsv(line)) {
                        methodFileList.add(line);
                    } else {
                        System.out.println("WARNING: given file is not a .csv file: " + line);
                    }
                }
                csvEntryMap.put(rnaSeq, methodFileList);
            }
        }
        return csvEntryMap;
    }

    /**
     * Creates a LinkedHashMap with fixed positions.
     *
     * @param entryList list of .txt files.
     * @param pattern pattern used to split folders. Pattern depends on os.system.
     * @return LinkedHashMap with dataset name as key and a list file names as values.
     * @throws FileNotFoundException Could not find or open the given file.
     * @throws IOException Cannot access the given file because it is used/opened by another program.
     */
    public final LinkedHashMap<String, ArrayList<String>> createCsvHashMap(final ArrayList<String> entryList,
            final String pattern) throws FileNotFoundException, IOException {
        InputTools inputCheck = new InputTools();
        LinkedHashMap<String, ArrayList<String>> mzidEntryMap = new LinkedHashMap<>();
        //Creates a LinkedHashMap with data from the .txt files in finalList.
        for (String sampleFile : entryList) {
            String dataset = "";
            ArrayList<String> methodFileList = new ArrayList<>();
            try (FileReader fileReader = new FileReader(sampleFile); BufferedReader buffFileReader = new BufferedReader(fileReader)) {
                String line;
                while ((line = buffFileReader.readLine()) != null) {
                    System.out.println(line);
                    //Check if given string is a valid file.
                    String[] folders = line.split(pattern);
                    dataset = folders[folders.length - 3];
                    //Warning is given if a file is not a valid .mzid file.
                    if (inputCheck.isCsv(line)) {
                        methodFileList.add(line);
                    } else {
                        System.out.println("WARNING: given file is not a .mzid file: " + line);
                    }
                }
                mzidEntryMap.put(dataset, methodFileList);
            }
        }
        return mzidEntryMap;
    }

    /**
     * Gathers database files from a database entry file.
     *
     * @param entryList list of database files.
     * @param pattern given separator pattern.
     * @return list of database locations.
     * @throws FileNotFoundException couldn't find the given file.
     * @throws IOException unable to access the given file.
     */
    public final LinkedHashMap<String, ArrayList<String>> createDatabaseHashMap(final ArrayList<String> entryList,
            final String pattern) throws FileNotFoundException, IOException {
        InputTools inputCheck = new InputTools();
        LinkedHashMap<String, ArrayList<String>> databaseEntryMap = new LinkedHashMap<>();
        for (String sampleFile : entryList) {
            String method = "";
            ArrayList<String> methodFileList = new ArrayList<>();
            try (FileReader fileReader = new FileReader(sampleFile); BufferedReader buffFileReader = new BufferedReader(fileReader)) {
                String line;
                while ((line = buffFileReader.readLine()) != null) {
                    //Gather rnaSeq name.
                    String[] folders = line.split(pattern);
                    method = folders[folders.length - 2];
                    //Warning is given if a file is not a valid .fa(sta)(.gz) file.
                    if (inputCheck.isFasta(line)) {
                        methodFileList.add(line);
                    } else {
                        System.out.println("WARNING: given file is not a .fa(sta)(.gz) file: " + line);
                    }
                }
                databaseEntryMap.put(method, methodFileList);
            }
        }
        return databaseEntryMap;
    }
}
