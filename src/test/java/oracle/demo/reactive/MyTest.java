package oracle.demo.reactive;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Logger;

import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.DisableDiscovery;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import oracle.demo.messaging.processor.ProcessorTestResource;

import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;

@HelidonTest
@AddConfig(key = "demo.processor.delay", value = "100")
public class MyTest{
    
    private final Logger logger = Logger.getLogger(MyTest.class.getName());

    @Inject private WebTarget webTarget;

    @Test
    public void test(){
        logger.info(webTarget.getUri().toString());
        //curl localhost:8080/reactive-messaging/process/key1?value=val1
        String response = webTarget.path("/reactive-messaging/process/key1").queryParam("value", "val1") .request().get(String.class);
        logger.info(response);
        //assertTrue(response.matches("Result: CBA,NML,ZYX - Elapsed time\\(ms\\): \\d+\\n"));
    }

}
