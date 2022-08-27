package hr.mmaracic.graphpr.context;

import hr.mmaracic.graphpr.dao.StopNodeDao;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContextStartEventHandler {

    private final StopNodeDao stopNodeDao;

    @EventListener(classes = ApplicationStartedEvent.class)
    public void handleContextStart(ApplicationStartedEvent applicationStartedEvent) {
        stopNodeDao.createFullTextIndex();
    }
}
