package hr.mmaracic.graphpr.dao.age;

import hr.mmaracic.graphpr.model.graph.LineNode;
import hr.mmaracic.graphpr.model.graph.StopNode;
import hr.mmaracic.graphpr.model.graph.StopProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Relationship;
import org.neo4j.cypherdsl.core.Statement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class AgeDao {

    private static final String STOP_LABEL = "Stop";
    private static final String LINE_LABEL = "Line";

    private static final String NEXT_RELATIONSHIP = "NEXT";
    private static final String GRAPH = "graph";

    private static final String NAME_PROPERTY = "name";
    private static final String ID_PROPERTY = "id";

    private final JdbcTemplate jdbcTemplate;

    private PreparedStatementCallback<Integer> noResultCallback = ps -> {

        log.info(ps.toString());
        ResultSet rs = ps.executeQuery();
        return 1;
    };

    private PreparedStatementCallback<Map<String, StopNode>> stopListCallback = ps -> {

        log.info(ps.toString());
        Map<String, StopNode> nodes = new HashMap();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            StopNode stopNode = new StopNode();
            stopNode.setName(rs.getString(NAME_PROPERTY));
            nodes.put(stopNode.getName(), stopNode);
        }
        return nodes;
    };

    public List<StopNode> saveAll(Set<StopNode> stopNodes) {
        Map<String, StopNode> existingStops = executeStatement(GRAPH, findNodesWithRelationships(STOP_LABEL, STOP_LABEL, NEXT_RELATIONSHIP), stopListCallback);
        stopNodes.forEach(sn -> {
                    if (!existingStops.containsKey(sn.getName())) {
                        executeStatement(GRAPH, createNode(STOP_LABEL, Map.of(NAME_PROPERTY, sn.getName())), noResultCallback);
                    }
                });
        Map<String, StopNode> existingStopsAfterUpdate = executeStatement(GRAPH, findNodesWithRelationships(STOP_LABEL, STOP_LABEL, NEXT_RELATIONSHIP), stopListCallback);
        stopNodes.forEach(sn -> {
                    List<StopProperties> existingProperties = existingStopsAfterUpdate.get(sn.getName()).getStopProperties();
                    for (StopProperties sp : sn.getStopProperties()) {
                        if (!existingProperties.contains(sp)) {
                            executeStatement(
                                    GRAPH,
                                    createRelationship(STOP_LABEL, Map.of(NAME_PROPERTY, sn.getName()), STOP_LABEL, Map.of(NAME_PROPERTY, sp.getNextStop().getName()), NEXT_RELATIONSHIP),
                                    noResultCallback);
                        }
                    }
               });
        return (List<StopNode>) existingStopsAfterUpdate.values();
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

    private Statement findNodesWithRelationships(String labelFrom, String labelTo, String relationshipType) {

        Relationship r = Cypher.node(labelFrom).relationshipBetween(Cypher.node(labelTo), relationshipType);
        return Cypher.match(r).returning(r).build();
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
                .relationshipBetween(Cypher.node(labelTo).withProperties(toProperties), relationshipType);
        return Cypher.create(r).build();
    }

    private <T> T executeStatement(String graphName, Statement statement, PreparedStatementCallback<T> callback) {

        return jdbcTemplate.execute(embedCypherQuery(graphName, statement.getCypher(), 1), callback);
    }

    public String embedCypherQuery(String graphName, String cypherQery, int resultCount) {
        StringBuilder sb = new StringBuilder("SELECT * FROM ag_catalog.cypher('" + graphName + "', $$ " + cypherQery + " $$) AS (");
        for (int i=1; i<=resultCount; i++) {
            sb.append("result" + i).append(" ag_catalog.agtype");
            if (i < resultCount) {
                sb.append(", ");
            }
        }
        return sb.append(");").toString();
    }
}
