package hr.mmaracic.graphpr.model.age;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class AgeVertex {

    private long id;
    private String label;
    private Map<String, Object> properties;
}
