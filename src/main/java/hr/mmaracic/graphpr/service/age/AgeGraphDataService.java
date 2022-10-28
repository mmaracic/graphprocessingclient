package hr.mmaracic.graphpr.service.age;

import hr.mmaracic.graphpr.dao.age.AgeDao;
import hr.mmaracic.graphpr.model.csv.LineEntry;
import hr.mmaracic.graphpr.model.csv.VoyageEntry;
import hr.mmaracic.graphpr.model.graph.LineNode;
import hr.mmaracic.graphpr.model.graph.StopNode;
import hr.mmaracic.graphpr.service.GraphDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
@Profile("age")
@RequiredArgsConstructor
public class AgeGraphDataService implements GraphDataService {

    private final AgeDao ageDao;

    @Override
    public void deleteStopsAndLines() {
        ageDao.deleteAll();
    }

    @Override
    public void saveStopsAndLines(List<LineEntry> lineEntryList, List<VoyageEntry> voyageEntryList, int version) {
        Set<StopNode> stops = extractStops(lineEntryList);
        ageDao.saveAll(stops);

        List<LineNode> lines = extractLineNodes(lineEntryList, voyageEntryList, stops, version);
        ageDao.saveAll(lines);

        addNextStopsToStopsAndLines(lineEntryList, stops, version);
        ageDao.saveAll(stops);
    }

    public String embedCypherQuery(String graphName, String query, Integer paramCount) {
        return ageDao.embedCypherQuery(graphName, query, paramCount);
    }
}
