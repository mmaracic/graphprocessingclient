package hr.mmaracic.graphpr.model.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class VoyageEntry {

    @CsvBindByName(column = "Linija")
    private Long lineId;

    @CsvBindByName(column = "Razdoblje")
    private String period;

    @CsvBindByName(column = "Učestalost polazaka (min)")
    private Integer frequencyMin;

}
