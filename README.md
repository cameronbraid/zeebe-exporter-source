# zeebe-exporter-source

This repository contains the components to make it easier to consume exported messages from
* `kafak` or `hazelcast` using `protobuf` serilization, or
* `kafka` using `json` serialization

---

## Hazelcast Example

In your spring boot app import the `HazelcastSourceConfiguration`

```

@Import(io.zeebe.exporter.source.hazelcast.HazelcastSourceConfiguration)

```

Then configure the following properties in your application.yaml (or any config source that spring supports)

```
zeebe.exporter.source.hazelcast:
  connection: localhost:5701
  connectionTimeout: PT30S
```

Then a bean in your application context that implements ```io.zeebe.exporter.source.ProtobufSourceConnector``` to receive the protobuf records

Hazelcast also requires you to manage the `sequence` number, so you also need to implement ```io.zeebe.exporter.source.hazelcast.HazelcastSourceConnector```


e.g.

```
@Component
public class MyConnector implements ProtobufSourceConnector, HazelcastSourceConnector {
  
  public void connectTo(ProtobufSource source) {
    source.addDeploymentListener(r->System.out.println("Received " + r));
    source.addWorkflowInstanceListener(...)
  }

  public void connectTo(HazelcastSource source) {
    source.postProcessListener(s->/* save sequence number */);
  }

  public Optional<Long> startPosition() {
    return /* load sequence number */;
  }

}
```



## Kafka Example

In your spring boot app import the `KafkaProtobufSourceConfiguration` is messages in kafka re protobuf encoded, or `KafkaJsonSourceConfiguration` for json encoded messages

The kafka consumer uses a consumer group which automatically manages the consumer offset.

```
@Import(io.zeebe.exporter.source.kafka.KafkaProtobufSourceConfiguration)
```
or
```
@Import(io.zeebe.exporter.source.kafka.KafkaJsonSourceConfiguration)
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