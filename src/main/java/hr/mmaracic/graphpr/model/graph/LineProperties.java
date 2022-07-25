package hr.mmaracic.graphpr.model.graph;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Data
@RelationshipProperties
public class LineProperties {

    @RelationshipId
    private Long id;

    private Integer version;

    @TargetNode
    private StopNode stop;

}
