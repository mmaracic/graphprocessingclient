package hr.mmaracic.graphpr.model.graph;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Node("Stop")
public class StopNode {

    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Relationship(type = "NEXT", direction = Relationship.Direction.OUTGOING)
    private List<StopProperties> stopProperties = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StopNode stopNode = (StopNode) o;
        return Objects.equals(id, stopNode.id) && Objects.equals(name, stopNode.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return id + " " + name + " " + stopProperties.size();
    }
}
