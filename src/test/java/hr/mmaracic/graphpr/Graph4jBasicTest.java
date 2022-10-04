package hr.mmaracic.graphpr;

import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@ActiveProfiles("neo4j")
class Graph4jBasicTest extends AbstractGraph4jTest{

    @Autowired
    private Driver driver;

    @Test
    void shouldRetrieveMovieTitles() {

        assertThat(driver, notNullValue(Driver.class));
        Session session = driver.session();
        assertThat(session, notNullValue(Session.class));
        List<String> titles = session.run("MATCH (m:Movie) RETURN m ORDER BY m.name ASC").stream()
                .map(r -> r.get("m").asNode())
                .map(n -> n.get("title").asString())
                .collect(Collectors.toList());
        assertThat(titles.size(), equalTo(3));
    }

    @Test
    void apocTest() {
        assertThat(driver, notNullValue(Driver.class));
        Session session = driver.session();
        assertThat(session, notNullValue(Session.class));
        List<String> procedures = session.run("CALL apoc.help('text')").stream()
                .map(r -> r.get("name").asString())
                .collect(Collectors.toList());
        assertThat(procedures.size(), equalTo(50));
    }

}
