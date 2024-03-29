package hr.mmaracic.graphpr.service.neo4j;

import hr.mmaracic.graphpr.model.csv.LineEntry;
import hr.mmaracic.graphpr.model.csv.VoyageEntry;
import hr.mmaracic.graphpr.model.graph.LineNode;
import hr.mmaracic.graphpr.model.graph.StopNode;
import hr.mmaracic.graphpr.repository.LineNodeRepository;
import hr.mmaracic.graphpr.repository.StopNodeRepository;
import hr.mmaracic.graphpr.service.GraphDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
@Profile("neo4j")
@RequiredArgsConstructor
public class Neo4jGraphDataService implements GraphDataService {

    private final LineNodeRepository lineNodeRepository;

    private final StopNodeRepository stopNodeRepository;

    public void deleteStopsAndLines() {
        lineNodeRepository.deleteAll();
        stopNodeRepository.deleteAll();
    }

    public void saveStopsAndLines(List<LineEntry> lineEntryList, List<VoyageEntry> voyageEntryList, int version) {
        Set<StopNode> stops = extractStops(lineEntryList);
        stopNodeRepository.saveAll(stops);


        List<LineNode> lines = extractLineNodes(lineEntryList, voyageEntryList, stops, version);
        lineNodeRepository.saveAll(lines);

        addNextStopsToStopsAndLines(lineEntryList, stops, version);
        stopNodeRepository.saveAll(stops);
    }

}
