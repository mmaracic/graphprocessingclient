package hr.mmaracic.graphpr;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.mmaracic.graphpr.model.graph.StopNode;
import hr.mmaracic.graphpr.model.graph.StopProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
class PojoToMapAndPathTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    //https://stackoverflow.com/questions/39543926/how-to-convert-pojo-to-map-and-vice-versa-in-java
    void convertPojoToMap() {
        List<StopNode> nodes = createStopNodesWithRelationship();
        Map<String, Object> map =
                objectMapper.convertValue(nodes.get(0), new TypeReference<>() {
                });
        log.info(map.toString());
    }

    @Test
    //https://stackoverflow.com/questions/4296910/is-it-possible-to-read-the-value-of-a-annotation-in-java
    void getNodeLabel() {
        Node nodeAnnotation = StopNode.class.getAnnotation(Node.class);
        if (nodeAnnotation != null) {
            log.info("Value: " + Arrays.toString(nodeAnnotation.value()));
            log.info("Primary labels: " + nodeAnnotation.primaryLabel());
            log.info("Label: " + Arrays.toString(nodeAnnotation.labels()));
        }
    }

    private List<StopNode> createStopNodesWithRelationship() {

        StopNode sn1 = new StopNode();
        sn1.setId(1L);
        sn1.setName("First");
        sn1.setStopProperties(new ArrayList<>());

        StopNode sn2 = new StopNode();
        sn2.setId(2L);
        sn2.setName("Second");
        sn2.setStopProperties(new ArrayList<>());

        StopProperties sn1Properties = new StopProperties();
        sn1Properties.setId(3L);
        sn1Properties.setMainDirection(true);
        sn1Properties.setVersion(1);
        sn1Properties.setTransitTime(2);
        sn1Properties.setLineId(5L);
        sn1Properties.setNextStop(sn2);

        sn1.getStopProperties().add(sn1Properties);

        return List.of(sn1, sn2);
    }
}
