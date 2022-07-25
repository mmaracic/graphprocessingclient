package hr.mmaracic.graphpr.model.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class LineEntry {

    @CsvBindByName(column = "Linija")
    private Long lineId;

    @CsvBindByName(column = "Stanica")
    private String stopName;

    @CsvBindByName(column = "Trajanje")
    private Integer durationMin;

}
