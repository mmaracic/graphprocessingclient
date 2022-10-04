package hr.mmaracic.graphpr;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@ActiveProfiles("neo4j")
class ContextTest extends AbstractGraph4jTest{

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void context() {
        Assertions.assertThat(applicationContext).isNotNull();
    }
}
