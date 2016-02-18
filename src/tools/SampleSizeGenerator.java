/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package tools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Determines the highest sampleSize of COPD and Healthy samples.
 * @author vnijenhuis
 */
public class SampleSizeGenerator {
    /**
     * Gathers sample numbers from the files.
     * @param filePath path of the files.
     * @return list of samples sizes: Healthy on index 0, COPD on index 1.
     */
    public final ArrayList<Integer> getSamples(final String filePath) {
        File file = new File(filePath);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        //Goes through all sample folders inside the RNASeq folder.
        Integer copdSamples = 0;
        Integer healthySamples = 0;
        for (String sample: directories) {
            if (sample.toLowerCase().contains("copd")) {
                int index = (Integer.parseInt(sample.substring(4)));
                if (copdSamples < index) {
                    copdSamples = index;
                }
            } else if (sample.toLowerCase().contains("healthy")) {
                int index = (Integer.parseInt(sample.substring(7)));
                if (healthySamples < index) {
                    healthySamples = index;
                }
            }
        }
        //Returns sample sizes of COPD and Healthy.
        ArrayList<Integer> sampleSize = new ArrayList<>();
        sampleSize.add(healthySamples);
        sampleSize.add(copdSamples);
        //Matrix is generated based on the biggest samplesize.
        return sampleSize;
    }
}
