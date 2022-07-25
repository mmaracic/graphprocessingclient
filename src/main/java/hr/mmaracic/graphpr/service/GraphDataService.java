package hr.mmaracic.graphpr.service;

import hr.mmaracic.graphpr.model.csv.LineEntry;
import hr.mmaracic.graphpr.model.graph.LineNode;
import hr.mmaracic.graphpr.model.graph.LineProperties;
import hr.mmaracic.graphpr.model.graph.StopNode;
import hr.mmaracic.graphpr.model.graph.StopProperties;
import hr.mmaracic.graphpr.repository.LineNodeRepository;
import hr.mmaracic.graphpr.repository.StopNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GraphDataService {

    private final LineNodeRepository lineNodeRepository;

    private final StopNodeRepository stopNodeRepository;

    public void deleteStopsAndLines() {
        lineNodeRepository.deleteAll();
        stopNodeRepository.deleteAll();
    }

    public void saveStopsAndLines(List<LineEntry> entries, int version) {
        Set<StopNode> stops = extractStops(entries);
        stopNodeRepository.saveAll(stops);

        List<LineNode> lines = extractLineNodes(entries, stops, version);
        lineNodeRepository.saveAll(lines);

        addNextStopsToStopsAndLines(entries, stops, version);
        stopNodeRepository.saveAll(stops);
    }

    public Set<StopNode> extractStops(List<LineEntry> entries) {
        return entries.stream().map(e -> {
            StopNode sn = new StopNode();
            sn.setName(e.getStopName());
            return sn;
        }).collect(Collectors.toSet());
    }

    public void addNextStopsToStopsAndLines(List<LineEntry> entries, Set<StopNode> stops, int version) {
        for (int i = 0; i < entries.size() - 1; i++) {
            LineEntry currentEntry = entries.get(i);
            LineEntry nextEntry = entries.get(i + 1);
            if (currentEntry.getLineId().equals(nextEntry.getLineId())) {
                StopNode currentStop = getStopNode(stops, currentEntry.getStopName());
                StopNode nextStop = getStopNode(stops, nextEntry.getStopName());

                int duration = nextEntry.getDurationMin() - currentEntry.getDurationMin();
                StopProperties currentStopProperties = new StopProperties();
                currentStopProperties.setTransitTime(duration);
                currentStopProperties.setMainDirection(true);
                currentStopProperties.setNextStop(nextStop);
                currentStopProperties.setLineId(currentEntry.getLineId());
                currentStopProperties.setVersion(version);
                currentStop.getStopProperties().add(currentStopProperties);

                StopProperties nextStopProperties = new StopProperties();
                nextStopProperties.setTransitTime(duration);
                nextStopProperties.setMainDirection(false);
                nextStopProperties.setNextStop(currentStop);
                nextStopProperties.setLineId(nextEntry.getLineId());
                nextStopProperties.setVersion(version);
                nextStop.getStopProperties().add(nextStopProperties);
            }
        }
    }

    public List<LineNode> extractLineNodes(List<LineEntry> entries, Set<StopNode> stops, int version) {
        return entries.stream().collect(Collectors.groupingBy(LineEntry::getLineId, Collectors.toList()))
                .entrySet().stream().map(e -> {
                    LineNode ln = new LineNode();
                    ln.setId(e.getKey());
                    ln.setLineProperties(
                            e.getValue().stream().map(le -> {
                                StopNode stopNode = getStopNode(stops, le.getStopName());
                                LineProperties lineProperties = new LineProperties();
                                lineProperties.setStop(stopNode);
                                lineProperties.setVersion(version);
                                return lineProperties;
                            }).collect(Collectors.toList())
                    );
                    return ln;
                }).collect(Collectors.toList());
    }

    private StopNode getStopNode(Set<StopNode> stops, String stopName) {
        return stops.stream().filter(s -> s.getName().equals(stopName)).findFirst().orElseThrow();
    }
}
