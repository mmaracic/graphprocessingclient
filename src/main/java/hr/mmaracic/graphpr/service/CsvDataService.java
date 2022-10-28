package hr.mmaracic.graphpr.service;

import com.opencsv.bean.CsvToBeanBuilder;
import hr.mmaracic.graphpr.model.csv.LineEntry;
import hr.mmaracic.graphpr.model.csv.VoyageEntry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Transactional
public class CsvDataService {

    public List<LineEntry> getCsvLineEntries(String fileName) throws IOException {
        return new CsvToBeanBuilder(new FileReader(fileName, StandardCharsets.UTF_8))
                .withType(LineEntry.class).build().parse();
    }

    public List<VoyageEntry> getCsvVoyageEntries(String fileName) throws IOException {
        return new CsvToBeanBuilder(new FileReader(fileName, StandardCharsets.UTF_8))
                .withType(VoyageEntry.class).build().parse();
    }
}
