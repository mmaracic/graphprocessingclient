package hr.mmaracic.graphpr.dao;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Query;
import org.neo4j.driver.internal.value.MapValue;
import org.neo4j.driver.internal.value.StringValue;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class StopNodeDao {

    private final Driver driver;

    @Data
    public static class NodeFullTextResult {
        private final Long id;
        private final String name;
        private final Double score;
    }

    public void createFullTextIndex() {
        driver.session().run("CREATE FULLTEXT INDEX StopNameTextIndex FOR (s:Stop) ON EACH [s.name]").stream();
    }

    public List<NodeFullTextResult> queryFullTextIndex(String namePart) {
        return driver.session().run(
                        new Query(
                                "CALL db.index.fulltext.queryNodes('StopNameTextIndex', $namePart) YIELD node, score RETURN node.id AS id, node.name AS name, score",
                                new MapValue(Map.of("namePart", new StringValue(namePart)))
                        )
                ).stream()
                .map(r -> new NodeFullTextResult(r.get("id").asLong(), r.get("name").asString(), r.get("score").asDouble()))
                .collect(Collectors.toList());
    }
}
