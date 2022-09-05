package hr.mmaracic.graphpr.dao;

import hr.mmaracic.graphpr.AbstractGraph4jTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@SpringBootTest
class PathDaoTest extends AbstractGraph4jTest {

    @Autowired
    private PathDao pathDao;

    @Test
    void testPathsBetweenStops() {
        List<String> path = pathDao.getPathsBetweenStops("Trešnjevački trg", "Žitnjak");
        Assertions.assertEquals(1, path.size());
        Assertions.assertEquals(path, List.of("Trešnjevački trg", "Badalićeva", "Tehnički muzej", "Studentski centar", "Zagrebčanka"));
    }
}
