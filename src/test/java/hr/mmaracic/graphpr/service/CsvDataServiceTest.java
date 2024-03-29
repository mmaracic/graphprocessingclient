package hr.mmaracic.graphpr.service;

import hr.mmaracic.graphpr.model.csv.LineEntry;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class CsvDataServiceTest {

    private final CsvDataService dataService = new CsvDataService();

    @Test
    void testRead() throws IOException {
        List<LineEntry> entries = dataService.getCsvLineEntries("data/zet_linije_stops.csv");
        assertThat(entries.size(), equalTo(364));
    }

}
