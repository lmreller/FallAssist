package com.struggleassist.Controller;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by Lucas on 3/17/2018.
 */
public class FallDetectionTest {
    @Test
    public void runAlgorithm_incidentTrue() throws Exception {
        float threshold = FallDetection.getFallThreshold();
        FallDetection.setIncidentScore(threshold + 1);
        FallDetection.runAlgorithm(true);

        assertThat(FallDetection.getIsIncident(), is(true));
    }

    @Test
    public void runAlgorithm_incidentFalse() throws Exception {
        float threshold = FallDetection.getFallThreshold();
        FallDetection.setIncidentScore(threshold - 1);
        FallDetection.runAlgorithm(true);

        assertThat(FallDetection.getIsIncident(), is(false));
    }


}