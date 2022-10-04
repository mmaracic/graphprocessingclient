package hr.mmaracic.graphpr.dao.age;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.mmaracic.graphpr.model.age.AgeObjectFactory;
import hr.mmaracic.graphpr.model.graph.LineNode;
import hr.mmaracic.graphpr.model.graph.StopNode;
import hr.mmaracic.graphpr.model.graph.StopProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.cypherdsl.core.*;
import org.postgresql.util.PGobject;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.*;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class AgeDao {

    //ToDo Handle mess with merging cypher query syntax to data model
    public static final String STOP_LABEL = "Stop";
    public static final String LINE_LABEL = "Line";

    public static final String NEXT_RELATIONSHIP = "NEXT";
    public static final String GRAPH = "graph";

    public static final String NAME_PROPERTY = "name";
    public static final String ID_PROPERTY = "id";

    public static final String LINE_ID_PROPERTY = "lineId";

    private final JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final PreparedStatementCallback<Integer> noResultCallback = ps -> {

        log.info(ps.toString());
        ps.executeQuery();
        return 1;
    };

    private final PreparedStatementCallback<StopNode> stopRelationshipCallback = ps  -> {

        log.info(ps.toString());
        StopNode stopNode = new StopNode();
        stopNode.setStopProperties(new ArrayList<>());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            try {
                AgeObjectFactory.parse((PGobject) rs.getObject(1), objectMapper);
                StopProperties stopProperties = new StopProperties();
                stopProperties.setLineId(rs.getLong(LINE_ID_PROPERTY));
                stopNode.getStopProperties().add(stopProperties);
            } catch (JsonProcessingException jsonProcessingException) {
                log.error(jsonProcessingException.getMessage());
            }
        }
        return stopNode;
    };

    private final PreparedStatementCallback<Map<String, StopNode>> stopListCallback = ps -> {

        log.info(ps.toString());
        Map<String, StopNode> nodes = new HashMap<>();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            try {
                StopNode stopNode = (StopNode) AgeObjectFactory.parse((PGobject) rs.getObject(1), objectMapper);
                nodes.put(stopNode.getName(), stopNode);
            } catch (JsonProcessingException jsonProcessingException) {
                log.error(jsonProcessingException.getMessage());
            }
        }
        return nodes;
    };

    public List<StopNode> saveAll(Set<StopNode> stopNodes) {
        Map<String, StopNode> existingStops = executeStatement(GRAPH, findNodes(STOP_LABEL), stopListCallback);
        stopNodes.forEach(sn -> {
            if (!existingStops.containsKey(sn.getName())) {
                executeStatement(GRAPH, createNode(STOP_LABEL, Map.of(NAME_PROPERTY, sn.getName())), noResultCallback);
            }
        });
        stopNodes.forEach(sn -> {
            List<StopProperties> existingProperties = new ArrayList<>();
            try {
                StopNode existingStop = executeStatement(GRAPH, findNodeWithRelationships(STOP_LABEL, Map.of(NAME_PROPERTY, sn.getName()), STOP_LABEL, NEXT_RELATIONSHIP), stopRelationshipCallback);
                existingProperties = existingStop.getStopProperties();
            } catch (DataAccessException dataAccessException) {
                //error acceptable when no path found - atype conversion error
                log.error(dataAccessException.getMessage());
            }
            for (StopProperties sp : sn.getStopProperties()) {
                if (!existingProperties.contains(sp)) {
                    executeStatement(
                            GRAPH,
                            createRelationship(STOP_LABEL, Map.of(NAME_PROPERTY, sn.getName()), STOP_LABEL, Map.of(NAME_PROPERTY, sp.getNextStop().getName()), NEXT_RELATIONSHIP),
                            noResultCallback);
                    executeStatement(
                            GRAPH,
                            createRelationship(STOP_LABEL, Map.of(NAME_PROPERTY, sp.getNextStop().getName()), STOP_LABEL, Map.of(NAME_PROPERTY, sn.getName()), NEXT_RELATIONSHIP),
                            noResultCallback);
                }
            }
        });
        return new ArrayList<>(executeStatement(GRAPH, findNodes(STOP_LABEL), stopListCallback).values());
    }

    public List<LineNode> saveAll(List<LineNode> lineNodes) {
        return lineNodes.stream().
                map(ln -> {
                    executeStatement(GRAPH, createNode(STOP_LABEL, Map.of(ID_PROPERTY, ln.getId())), noResultCallback);
                    return ln;
                })
                .collect(Collectors.toList());
    }

    public void deleteAll() {
        executeStatement(GRAPH, dropNodes(STOP_LABEL), noResultCallback);
        executeStatement(GRAPH, dropNodes(LINE_LABEL), noResultCallback);
    }

    private Statement findNodes(String labelFrom) {

        Node n = Cypher.node(labelFrom);
        return Cypher.match(n).returning(n).build();
    }

    private Statement findNodeWithRelationships(String labelFrom, Map<String, Object> fromProperties, String labelTo, String relationshipType) {

        Node n = Cypher.node(labelFrom).withProperties(fromProperties);
        Relationship r = n.relationshipBetween(Cypher.node(labelTo), relationshipType);
        return Cypher.match(r).returning(n).build();
    }

    private Statement dropNodes(String label) {

        Node n = Cypher.node(label);
        return Cypher.match(n).detachDelete(n).build();
    }

    private Statement createNode(String label, Map<String, Object> properties) {

        Node n = Cypher.node(label).withProperties(properties);
        return Cypher.create(n).build();
    }

    private Statement createRelationship(String labelFrom, Map<String, Object> fromProperties, String labelTo, Map<String, Object> toProperties, String relationshipType) {

        Relationship r = Cypher.node(labelFrom).withProperties(fromProperties)
                .relationshipTo(Cypher.node(labelTo).withProperties(toProperties), relationshipType);
        return Cypher.create(r).build();
    }

    private <T> T executeStatement(String graphName, Statement statement, PreparedStatementCallback<T> callback) throws DataAccessException {

        return jdbcTemplate.execute(embedCypherQuery(graphName, statement.getCypher(), 1), callback);
    }

    public String embedCypherQuery(String graphName, String cypherQuery, int resultCount) {
        StringBuilder sb = new StringBuilder("SELECT * FROM ag_catalog.cypher('" + graphName + "', $$ " + cypherQuery + " $$) AS (");
        for (int i = 1; i <= resultCount; i++) {
            sb.append("result" + i).append(" ag_catalog.agtype");
            if (i < resultCount) {
                sb.append(", ");
            }
        }
        return sb.append(");").toString();
    }
}
