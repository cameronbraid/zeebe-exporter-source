# zeebe-exporter-source

This repository contains the components to make it easier to consume exported messages from
* `kafak` or `hazelcast` using `protobuf` serilization, or
* `kafka` using `json` serialization

---

## Hazelcast Example

In your spring boot app import the `HazelcastConfiguration`

```

@org.springframework.context.annotation.Import(io.zeebe.exporter.source.hazelcast.HazelcastConfiguration)

```

Then configure the following properties in your application.yaml (or any config source that spring supports)

```
zeebe.exporter.source.hazelcast:
  connection: localhost:5701
  connectionTimeout: PT30S
```

Then define a bean in your application context that implements ```java.util.function.Consumer<io.zeebe.exporter.source.hazelcast.HazelcastProtobufSource>```

Hazelcast also requires you to manage the `sequence` number, see `postProcessListener` and `startPosition` below

e.g.

```
@Component
public class MyConsumer implements java.util.function.Consumer<io.zeebe.exporter.source.hazelcast.HazelcastProtobufSource> {
  public void accept(io.zeebe.exporter.source.hazelcast.HazelcastProtobufSource source) {
    source.addDeploymentListener(r->System.out.println("Received " + r));
    source.addWorkflowInstanceListener(...)
    ...
    source.postProcessListener(s->saveSequenceNumber(s))
  }

  public Optional<Long> startPosition() {
    return loadSequenceNumber();
  }

}
```



## Kafka Example

In your spring boot app import the `KafkaProtobufConfiguration` is messages in kafka re protobuf encoded, or `KafkaJsonConfiguration` for json encoded messages

The kafka consumer uses a consumer group which automatically manages the consumer offset.

```
@org.springframework.context.annotation.Import(io.zeebe.exporter.source.kafka.KafkaProtobufConfiguration)
```

And configure the following properties in your application.yaml (or any config source that spring supports)

```
zeebe.exporter.source.kafka:
  consumerProperties: |
    bootstrap.servers=kafka:9093
    group.id=zeebe-simple-monitor
    auto.offset.reset=earliest

```

Then define a bean in your application context that implements either

* ```java.util.function.Consumer<io.zeebe.exporter.source.ProtobufSource>```
  * can only be used with protobuf encoded messages
* ```java.util.function.Consumer<io.zeebe.exporter.source.RecordSource>```
  * can be used with protobuf or json encoded messages. 

```
@Component
public class MyConsumer implements java.util.function.Consumer<io.zeebe.exporter.source.RecordSource> {
  public void accept(io.zeebe.exporter.source.RecordSource source) {
    source.addDeploymentListener(r->System.out.println("Received " + r));
    source.addWorkflowInstanceListener(...)
    ...
  }
}
```