package hr.mmaracic.graphpr.service;

import hr.mmaracic.graphpr.model.csv.LineEntry;
import hr.mmaracic.graphpr.model.graph.LineNode;
import hr.mmaracic.graphpr.model.graph.StopNode;
import hr.mmaracic.graphpr.repository.LineNodeRepository;
import hr.mmaracic.graphpr.repository.StopNodeRepository;
import hr.mmaracic.graphpr.service.neo4j.Neo4jGraphDataService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;

class Neo4jGraphDataServiceTest {

    private final CsvDataService dataService = new CsvDataService();

    private final LineNodeRepository lineNodeRepository = Mockito.mock(LineNodeRepository.class);
    private final StopNodeRepository stopNodeRepository = Mockito.mock(StopNodeRepository.class);

    private final Neo4jGraphDataService neo4jGraphDataService = new Neo4jGraphDataService(lineNodeRepository, stopNodeRepository);

    @Test
    void testGraphStorage() throws IOException {

        Mockito.when(lineNodeRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));
        Mockito.when(stopNodeRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        int version = 1;
        List<LineEntry> entries = dataService.getCsvData("data/zet_linije_stops.csv");
        neo4jGraphDataService.saveStopsAndLines(entries, version);

        Mockito.verify(lineNodeRepository, Mockito.times(1)).saveAll(anyList());
        Mockito.verify(stopNodeRepository, Mockito.times(2)).saveAll(anySet());

    }

    @Test
    void testGraphGeneration() throws IOException {
        int version = 1;
        List<LineEntry> entries = dataService.getCsvData("data/zet_linije_stops.csv");
        assertThat(entries.size(), equalTo(364));

        Set<StopNode> stopNodes = neo4jGraphDataService.extractStops(entries);
        assertThat(stopNodes.size(), equalTo(117));

        List<LineNode> lineNodes = neo4jGraphDataService.extractLineNodes(entries, stopNodes, version);
        assertThat(lineNodes.size(), equalTo(15));

        neo4jGraphDataService.addNextStopsToStopsAndLines(entries, stopNodes, version);
        assertThat(
                stopNodes.stream().map(StopNode::getStopProperties).filter(List::isEmpty).count(),
                equalTo(0L)
        );
        assertThat(
                lineNodes.stream().map(LineNode::getLineProperties).filter(List::isEmpty).count(),
                equalTo(0L)
        );
        assertThat(getLineWithId(lineNodes, 1).getLineProperties().size(), equalTo(14));
        assertThat(getLineWithId(lineNodes, 2).getLineProperties().size(), equalTo(27));
        assertThat(getLineWithId(lineNodes, 3).getLineProperties().size(), equalTo(28));
        assertThat(getLineWithId(lineNodes, 4).getLineProperties().size(), equalTo(31));
        assertThat(getLineWithId(lineNodes, 5).getLineProperties().size(), equalTo(33));
        assertThat(getLineWithId(lineNodes, 6).getLineProperties().size(), equalTo(23));
        assertThat(getLineWithId(lineNodes, 7).getLineProperties().size(), equalTo(35));
        assertThat(getLineWithId(lineNodes, 8).getLineProperties().size(), equalTo(17));
        assertThat(getLineWithId(lineNodes, 9).getLineProperties().size(), equalTo(19));
        assertThat(getLineWithId(lineNodes, 11).getLineProperties().size(), equalTo(26));
        assertThat(getLineWithId(lineNodes, 12).getLineProperties().size(), equalTo(20));
        assertThat(getLineWithId(lineNodes, 13).getLineProperties().size(), equalTo(29));
        assertThat(getLineWithId(lineNodes, 14).getLineProperties().size(), equalTo(27));
        assertThat(getLineWithId(lineNodes, 15).getLineProperties().size(), equalTo(5));
        assertThat(getLineWithId(lineNodes, 17).getLineProperties().size(), equalTo(30));
    }

    private LineNode getLineWithId(List<LineNode> lineNodes, long lineId) {
        return lineNodes.stream().filter(lineNode -> lineNode.getId().equals(lineId)).findFirst().orElseThrow(() -> new IllegalArgumentException("Line with id " + lineId + " does not exist"));
    }
}
