/*
 * @author vnijenhuis
 * @project peptide spectrum matrix quality control  * 
 * @copyrights � vnijenhuis, Dr. P.I. Horvatovich  * 
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
        ArrayList<String> copdSamples = new ArrayList<>();
        ArrayList<String> healthySamples = new ArrayList<>();
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
                copdSamples.add(sample);
            } else if (sample.contains("Healthy")) {
                healthySamples.add(sample);
            }
        }
        if (copdSamples.size() >= healthySamples.size()) {
            sampleSize = (copdSamples.size()*2);
        } else {
            sampleSize = (healthySamples.size()*2);
        }
        return sampleSize;
    }
}
