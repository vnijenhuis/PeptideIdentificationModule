/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  * 
 */
package tools;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Generates a sampleSize based on the input files.
 * @author vnijenhuis
 */
public class SampleSizeGenerator {
    /**
     * Gathers sample numbers from the files.
     * @param filePath path of the files.
     * @return sample size as Integer.
     */
    public final Integer getSamples(final String filePath) {
        File file = new File(filePath);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        //Goes through all sample folders inside the RNASeq folder.
        Integer sampleSize = 0;
        for (String sample: directories) { 
            if (sample.toLowerCase().contains("copd")) {
                int index = (Integer.parseInt(sample.substring(4))*2);
                if (sampleSize < index) {
                    sampleSize = index;
                }
            } else if (sample.toLowerCase().contains("healthy")) {
                int index = (Integer.parseInt(sample.substring(7))*2);
                if (sampleSize < index) {
                    sampleSize = index;
                }
            }
        }
        //Returns sampleSize. Biggest sample size of each dataset is returned.
        //Matrix is generated based on the biggest samplesize.
        return sampleSize;
    }
}
