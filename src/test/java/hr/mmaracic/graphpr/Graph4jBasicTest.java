package hr.mmaracic.graphpr;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
class Graph4jBasicTest {

    private static final Neo4j embeddedDatabaseServer = neo4j();

    private static Neo4j neo4j() {
        return Neo4jBuilders.newInProcessBuilder()
                .withDisabledServer() // No need for http
                .withFixture(""
                        + "CREATE (TheMatrix:Movie {title:'The Matrix', released:1999, tagline:'Welcome to the Real World'})\n"
                        + "CREATE (TheMatrixReloaded:Movie {title:'The Matrix Reloaded', released:2003, tagline:'Free your mind'})\n"
                        + "CREATE (TheMatrixRevolutions:Movie {title:'The Matrix Revolutions', released:2003, tagline:'Everything that has a beginning has an end'})\n"
                )
                .build();

        // For enterprise use
        // return com.neo4j.harness.EnterpriseNeo4jBuilders.newInProcessBuilder()
        //    .newInProcessBuilder()
        //    .build();
    }

    static void closeNeo4j() {
        embeddedDatabaseServer.close();
    }

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", embeddedDatabaseServer::boltURI);
        registry.add("spring.neo4j.authentication.password", () -> "");
    }

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

}
