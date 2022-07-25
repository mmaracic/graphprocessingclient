package hr.mmaracic.graphpr.repository;

import hr.mmaracic.graphpr.model.graph.StopNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface StopNodeRepository extends Neo4jRepository<StopNode, Long> {
}
