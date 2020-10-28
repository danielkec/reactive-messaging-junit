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
public class MsgProcessingBean2 {
    private final static Logger logger = Logger.getLogger(MsgProcessingBean2.class.getSimpleName());

    private final LinkedBlockingDeque<KeyValue> queue = new LinkedBlockingDeque<>();

    public void submit(KeyValue kv){
        try{
            Objects.requireNonNull(kv.getValue());
            queue.put(kv);        
        }catch(Exception e){ throw new RuntimeException("cannot submit message: " + kv, e);}
    }
    
    @Outgoing("channel-3")
    public KeyValueMessage publish() {
        logger.info("publish() is being called.");
        try{
            KeyValue kv = queue.take(); 
            logger.info("Publishing [channel-3]: " + kv);
            return KeyValueMessage.of(kv);
        }catch(Exception e){ throw new RuntimeException("cannot get message from the queue", e);}
    }

    @Incoming("channel-3")
    @Outgoing("channel-4")
    public String process(KeyValueMessage message) {
        logger.info("Processing [channel-3 -> channel-4]: " + message.getPayload());
        KeyValue kv = message.getPayload();
        return kv.getKey() + "=" + kv.getValue();
    }


    @Incoming("channel-4")
    public void consume(String message) {
        logger.info(String.format("Consuming [channel-4]: %s", message));
    }

}
