package hr.mmaracic.graphpr.context;

import hr.mmaracic.graphpr.dao.neo4j.StopNodeDao;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class ContextStartEventHandler {

    private final StopNodeDao stopNodeDao;

    @EventListener(classes = ApplicationStartedEvent.class)
    public void handleContextStart(ApplicationStartedEvent applicationStartedEvent) {
        stopNodeDao.createFullTextIndex();
    }
}
