package com.struggleassist.Model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by Lucas on 4/2/2018.
 */
public class RecordTest {

    private String response = "test";
    private float score = 0;
    Record record = new Record(response, score);

    @Test
    public void record_isCreated() throws Exception {
        assertThat(record.isInitialized, is(true));
    }

    @Test
    public void date_isCorrect() throws Exception {
        String expectedDate = "03/03/03";
        record.setDateOfIncident(expectedDate);
        assertThat(record.getDateOfIncident(), is(expectedDate));
    }

    @Test
    public void location_isCorrect() throws Exception {
        String expectedLocation = "42 Wallaby Way, Sydney";
        record.setIncidentLocation(expectedLocation);
        assertThat(record.getIncidentLocation(), is(expectedLocation));
    }

    @Test
    public void response_isCorrect() throws Exception {
        String expectedResponse = "@test_2";
        assertThat(record.getUserResponse(), is(response));
        record.setUserResponse(expectedResponse);
        assertThat(record.getUserResponse(), is(expectedResponse));
    }

    @Test
    public void note_isCorrect() throws Exception {
        String expectedNote = "I fell and it hurt";
        record.setIncidentNotes(expectedNote);
        assertThat(record.getIncidentNotes(), is(expectedNote));
    }

    @Test
    public void score_isCorrect() throws Exception {
        Float expectedScore = 6.9f;
        assertThat(record.getIncidentScore(), is(score));
        record.setIncidentScore(expectedScore);
        assertThat(record.getIncidentScore(), is(expectedScore));
    }

    @Test
    public void id_isGenerated() throws Exception {
        String currentId = record.getId();
        String nextId = record.generateId();
        boolean test = currentId.equals(nextId);
        assertThat(test, is(false));
    }
}