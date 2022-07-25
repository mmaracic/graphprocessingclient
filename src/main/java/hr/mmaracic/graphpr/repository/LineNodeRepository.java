package hr.mmaracic.graphpr.repository;

import hr.mmaracic.graphpr.model.graph.LineNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface LineNodeRepository extends Neo4jRepository<LineNode, Long> {
}
