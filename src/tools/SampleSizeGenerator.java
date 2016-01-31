/*
 * @author vnijenhuis
 * @project peptide spectrum matrix quality control  * 
 */
package tools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Generates a sampleSize based on the input files.
 * @author vnijenhuis
 */
public class SampleSizeGenerator {
    /**
     * Gathers sample numbers from the files.
     * @param filePath path of the files.
     * @return sample size
     */
    public final Integer getSamples(final String filePath) {
        File file = new File(filePath);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        Integer sampleSize = 0;
        for (String sample: directories) { 
            if (sample.contains("COPD")) {
                int index = (Integer.parseInt(sample.substring(4))*2);
                if (sampleSize < index) {
                    sampleSize = index;
                }
            } else if (sample.contains("Healthy")) {
                int index = (Integer.parseInt(sample.substring(7))*2);
                if (sampleSize < index) {
                    sampleSize = index;
                }
            }
        }
        return sampleSize;
    }
}
