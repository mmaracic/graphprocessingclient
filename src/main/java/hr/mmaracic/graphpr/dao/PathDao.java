package hr.mmaracic.graphpr.dao;

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
public class PathDao {

    private final Neo4jClient client;

    public List<String> getPathsBetweenStops(String startStop, String endStop) {
        return client.getQueryRunner().run(
                new Query(
                        "MATCH path=(:Stop{name: $startStop}) -[:NEXT*]-> (:Stop{name: $endStop}) RETURN nodes(path) LIMIT 1",
                        new MapValue(Map.of("startStop", new StringValue(startStop), "endStop", new StringValue(endStop)))
                )
        ).stream()
                .map(r -> r.get("name").asString())
                .collect(Collectors.toList());
    }


}
