package hr.mmaracic.graphpr.repository;

import hr.mmaracic.graphpr.model.graph.StopNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface StopNodeRepository extends Neo4jRepository<StopNode, Long> {

    @Query("CALL db.index.fulltext.queryNodes('StopNameTextIndex', $namePart) YIELD node, score RETURN node")
    List<StopNode> fulltextQueryByName(String namePart);
}
