package hr.mmaracic.graphpr;

import hr.mmaracic.graphpr.dao.neo4j.StopNodeDao;
import hr.mmaracic.graphpr.model.csv.LineEntry;
import hr.mmaracic.graphpr.model.graph.LineNode;
import hr.mmaracic.graphpr.model.graph.StopNode;
import hr.mmaracic.graphpr.repository.LineNodeRepository;
import hr.mmaracic.graphpr.repository.StopNodeRepository;
import hr.mmaracic.graphpr.service.CsvDataService;
import hr.mmaracic.graphpr.service.neo4j.Neo4jGraphDataService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
class Graph4jGraphTest extends AbstractGraph4jTest {

    @Autowired
    private CsvDataService csvDataService;

    @Autowired
    private Neo4jGraphDataService neo4jGraphDataService;

    @Autowired
    private StopNodeRepository stopNodeRepository;

    @Autowired
    private LineNodeRepository lineNodeRepository;

    @Autowired
    private StopNodeDao stopNodeDao;

    @Test
    void shouldRetrieveStops() throws IOException {
        loadData();
        List<StopNode> stops = stopNodeRepository.findAll();
        assertThat(stops.size(), equalTo(116));
    }

    @Test
    void shouldRetrieveLines() throws IOException {
        loadData();
        List<LineNode> lines = lineNodeRepository.findAll();
        assertThat(lines.size(), equalTo(15));
    }

    @Test
    void checkConnections() throws IOException {
        loadData();
        List<StopNode> nodes = stopNodeRepository.findAll();
        assertThat(nodes.stream().map(StopNode::getStopProperties).allMatch(sp -> !sp.isEmpty()), Matchers.is(true));
    }

    @Test
    void checkFullTextSearchWithScore() throws IOException {
        loadData();
        List<StopNodeDao.NodeFullTextResult> results = stopNodeDao.queryFullTextIndex("trg");
        assertThat(results.size(), Matchers.is(9));
    }

    @Test
    void checkFullTextSearch() throws IOException {
        loadData();
        List<StopNode> results = stopNodeRepository.fulltextQueryByName("trg");
        assertThat(results.size(), Matchers.is(9));
    }

    private void loadData() throws IOException {
        int version = 1;
        List<LineEntry> entries = csvDataService.getCsvData("data/zet_linije_stops.csv");
        neo4jGraphDataService.saveStopsAndLines(entries, version);
    }

}
