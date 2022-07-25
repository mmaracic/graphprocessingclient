package hr.mmaracic.graphpr.service;

import com.opencsv.bean.CsvToBeanBuilder;
import hr.mmaracic.graphpr.model.csv.LineEntry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@Service
@Transactional
public class CsvDataService {

    public List<LineEntry> getCsvData(String fileName) throws FileNotFoundException {
        return new CsvToBeanBuilder(new FileReader(fileName))
                .withType(LineEntry.class).build().parse();
    }
}
