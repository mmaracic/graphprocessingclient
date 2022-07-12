package hr.mmaracic.graphpr;

import apoc.ApocConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class Graph4jBasicTest {

    private static final Neo4j embeddedDatabaseServer;

    static {
        try {
            embeddedDatabaseServer = neo4j();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static Neo4j neo4j() throws URISyntaxException {
        // We need the directory containing the APOC jar, otherwise all APOC procedures must be loaded manually.
        // While the intuitive idea might be not having APOC on the class path at all in that case and just dump
        // it into the plugin directory, it doesn't work as APOC needs some extension factories to work with
        // and those are not loaded from the plugin unless it's part of the original class loader that loaded neo.
        // If you know which methods you're a gonna use, you can configure them manually instead.
        var pluginDirContainingApocJar = new File(
                ApocConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                .getParentFile().toPath();
        return Neo4jBuilders.newInProcessBuilder()
                .withDisabledServer() // No need for http
                .withFixture(""
                        + "CREATE (TheMatrix:Movie {title:'The Matrix', released:1999, tagline:'Welcome to the Real World'})\n"
                        + "CREATE (TheMatrixReloaded:Movie {title:'The Matrix Reloaded', released:2003, tagline:'Free your mind'})\n"
                        + "CREATE (TheMatrixRevolutions:Movie {title:'The Matrix Revolutions', released:2003, tagline:'Everything that has a beginning has an end'})\n"
                )
                .withConfig(GraphDatabaseSettings.plugin_dir, pluginDirContainingApocJar)
                .withConfig(GraphDatabaseSettings.procedure_unrestricted, List.of("apoc.*"))
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
