package hr.mmaracic.graphpr.dao.age;

import hr.mmaracic.graphpr.model.graph.StopNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Statement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class AgeDao {

    private static final String STOP_LABEL = "Stop";
    private static final String LINE_LABEL = "Line";
    private static final String GRAPH = "graph";

    private final JdbcTemplate jdbcTemplate;

    public List<StopNode> saveAll(Set<StopNode> stopNodes) {
        return stopNodes.stream().
                map(sn -> {
                    executeStatement(GRAPH, createNode(STOP_LABEL, Map.of("name", sn.getName())));
                    return sn;
                })
                .collect(Collectors.toList());
    }

    public void deleteAll() {
        executeStatement(GRAPH, dropNodes(STOP_LABEL));
        executeStatement(GRAPH, dropNodes(LINE_LABEL));
    }

    private Statement dropNodes(String label) {

        Node n = Cypher.node(label);
        return Cypher.match(n).detachDelete(n).build();
    }

    private Statement createNode(String label, Map<String, Object> properties) {

        Node n = Cypher.node(label).withProperties(properties);
        return Cypher.create(n).build();
    }

    private Integer executeStatement(String graphName, Statement statement) {

        PreparedStatementCallback<Integer> preparedStatementCallback = ps -> {

            log.info(ps.toString());
            ResultSet rs = ps.executeQuery();
            return 1;
        };

        String query = "SELECT * FROM ag_catalog.cypher('" + graphName + "', $$ " + statement.getCypher() + " $$) AS (result ag_catalog.agtype);";
        return jdbcTemplate.execute(query, preparedStatementCallback);
    }
}
