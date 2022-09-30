package hr.mmaracic.graphpr.dao.neo4j;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.Query;
import org.neo4j.driver.internal.value.MapValue;
import org.neo4j.driver.internal.value.StringValue;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class StopNodeDao {
    private final Neo4jClient client;

    @Data
    public static class NodeFullTextResult {
        private final Long id;
        private final String name;
        private final Double score;
    }

    public void createFullTextIndex() {
        client.getQueryRunner().run("CREATE FULLTEXT INDEX StopNameTextIndex IF NOT EXISTS FOR (s:Stop) ON EACH [s.name]").stream();
    }

    public List<NodeFullTextResult> queryFullTextIndex(String namePart) {
        return client.getQueryRunner().run(
                        new Query(
                                "CALL db.index.fulltext.queryNodes('StopNameTextIndex', $namePart) YIELD node, score RETURN node.id AS id, node.name AS name, score",
                                new MapValue(Map.of("namePart", new StringValue(namePart)))
                        )
                ).stream()
                .map(r -> new NodeFullTextResult(r.get("id").asLong(0), r.get("name").asString(), r.get("score").asDouble()))
                .collect(Collectors.toList());
    }
}
