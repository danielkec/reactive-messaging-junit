package oracle.demo.messaging.processor;

import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SubmissionPublisher;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Publisher;

import io.helidon.common.configurable.ThreadPoolSupplier;
import oracle.demo.messaging.processor.KeyValueMessage.KeyValue;


@ApplicationScoped
public class MsgProcessingBean1 {
    private final static Logger logger = Logger.getLogger(MsgProcessingBean1.class.getSimpleName());

    private final SubmissionPublisher<KeyValueMessage> publisher = new SubmissionPublisher<>(
        ThreadPoolSupplier.builder().threadNamePrefix("messaging-process-").build().get(),
        Flow.defaultBufferSize()
    );

    public int submit(KeyValue kv){
        return publisher.submit(KeyValueMessage.of(kv));
    }

    @Outgoing("channel-1")
    public Publisher<KeyValueMessage> preparePublisher() {
        return ReactiveStreams
                .fromPublisher(FlowAdapters.toPublisher(publisher))
                .buildRs();
    }

    @Incoming("channel-1")
    @Outgoing("channel-2")
    public String process(KeyValueMessage message) {
        logger.info("Processing [channel-1 -> channel-2]: " + message.getPayload());
        KeyValue kv = message.getPayload();
        return kv.getKey() + "=" + kv.getValue();
    }


    @Incoming("channel-2")
    public void consume(String message) {
        logger.info(String.format("Consuming [channel-2]: %s", message));
    }

}
