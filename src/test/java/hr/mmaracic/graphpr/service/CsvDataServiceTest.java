package hr.mmaracic.graphpr.service;

import hr.mmaracic.graphpr.model.csv.LineEntry;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CsvDataServiceTest {

    private final CsvDataService dataService = new CsvDataService();

    @Test
    public void testRead() throws FileNotFoundException {
        List<LineEntry> entries = dataService.getCsvData("data/zet_linije_stops.csv");
        assertThat(entries.size(), equalTo(364));
    }

}
