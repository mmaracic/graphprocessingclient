package hr.mmaracic.graphpr.model.graph;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Node("Line")
public class LineNode {

    @Id
    private Long id;

    @Relationship(type = "CONTAINS", direction = Relationship.Direction.OUTGOING)
    private List<LineProperties> lineProperties = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineNode lineNode = (LineNode) o;
        return id.equals(lineNode.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + " " + lineProperties.size();
    }
}
