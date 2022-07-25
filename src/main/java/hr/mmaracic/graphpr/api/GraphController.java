package hr.mmaracic.graphpr.api;

import hr.mmaracic.graphpr.model.csv.LineEntry;
import hr.mmaracic.graphpr.service.CsvDataService;
import hr.mmaracic.graphpr.service.GraphDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GraphController {

    private final CsvDataService csvDataService;

    private final GraphDataService graphDataService;

    @PostMapping(value = "load")
    public Boolean loadData() throws IOException {
        int version = 1;
        List<LineEntry> entries = csvDataService.getCsvData("data/zet_linije_stops.csv");
        graphDataService.saveStopsAndLines(entries, version);
        return true;
    }

    @PostMapping(value = "truncate")
    public Boolean deleteData() {
        graphDataService.deleteStopsAndLines();
        return true;
    }
}
