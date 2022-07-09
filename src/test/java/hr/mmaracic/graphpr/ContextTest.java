package hr.mmaracic.graphpr;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class ContextTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void context() {
        Assertions.assertThat(applicationContext).isNotNull();
    }
}
