package hr.mmaracic.graphpr.service;

import com.opencsv.bean.CsvToBeanBuilder;
import hr.mmaracic.graphpr.model.csv.LineEntry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Transactional
public class CsvDataService {

    public List<LineEntry> getCsvData(String fileName) throws IOException {
        return new CsvToBeanBuilder(new FileReader(fileName, StandardCharsets.UTF_8))
                .withType(LineEntry.class).build().parse();
    }
}
