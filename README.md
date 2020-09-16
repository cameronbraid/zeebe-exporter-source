# zeebe-exporter-source

This repository contains the components to make it easier to consume exported messages from
* `kafak` or `hazelcast` using `protobuf` serilization, or
* `kafka` using `json` serialization

---

## Hazelcast Example

In your spring boot app import the `HazelcastConfiguration`

```

@Import(io.zeebe.exporter.source.hazelcast.HazelcastConfiguration)

```

Then configure the following properties in your application.yaml (or any config source that spring supports)

```
zeebe.exporter.source.hazelcast:
  connection: localhost:5701
  connectionTimeout: PT30S
```

Then define a bean in your application context that implements ```io.zeebe.exporter.source.hazelcast.HazelcastProtobufSourceConnector```

Hazelcast also requires you to manage the `sequence` number, see `postProcessListener` and `startPosition` below

e.g.

```
@Component
public class MyConnector implements HazelcastProtobufSourceConnector {
  public void connectTo(io.zeebe.exporter.source.hazelcast.HazelcastProtobufSource source) {
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
@Import(io.zeebe.exporter.source.kafka.KafkaProtobufConfiguration)
```
or
```
@Import(io.zeebe.exporter.source.kafka.KafkaJsonConfiguration)
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

* ```io.zeebe.exporter.source.ProtobufSourceConnector```
  * can only be used with protobuf encoded messages
  
* ```io.zeebe.exporter.source.RecordSourceConnector```
  * can be used with json encoded messages. 

```
@Component
public class MyConnector implements io.zeebe.exporter.source.ProtobufSourceConnector {
  public void connectTo(io.zeebe.exporter.source.ProtobufSource source) {
    source.addDeploymentListener(r->System.out.println("Received " + r));
    source.addWorkflowInstanceListener(...)
    ...
  }
}
```