/*
 * Copyright © 2020 camunda services GmbH (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zeebe.exporter.source;

import static org.awaitility.Awaitility.await;

import com.google.protobuf.Message;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.zeebe.containers.ZeebeContainer;
import io.zeebe.exporter.source.kafka.KafkaProtobufSourceConfiguration;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;

/**
 * This tests the deployment of the exporter into a Zeebe broker in a as-close-to-production way as
 * possible, by starting a Zeebe container and deploying the exporter as one normally would. As the
 * verification tools are somewhat limited at the moment, we only verify that some things are
 * exported; a full verification of the behaviour is still done in the main exporter module. Once
 * verification tools get better, all the verification should be moved into this module.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {KafkaProtobufSourceConfiguration.class, KafkaProtobufSourceIT.TestConfig.class})
@DirtiesContext
public final class KafkaProtobufSourceIT {

  private static KafkaContainer KAFKA_CONTAINER;
  private static ZeebeContainer ZEEBE_CONTAINER;

  private @Autowired RecordCollector recordCollector;

  private ZeebeClient client;

  @BeforeClass
  public static void setupClass() {

    KAFKA_CONTAINER = Containers.newKafkaContainer();
    KAFKA_CONTAINER.start();

    System.setProperty(
        "zeebe.exporter.source.kafka.consumerProperties",
        String.format(
            "bootstrap.servers=%s\ngroup.id=kafka-source-it\nauto.offset.reset=earliest",
            KAFKA_CONTAINER.getBootstrapServers()
            // "localhost:" + KAFKA_CONTAINER.getMappedPort(KafkaContainer.KAFKA_PORT)
            ));

    ZEEBE_CONTAINER =
        Containers.newZeebeContainer("kafka-exporter.yml", "zeebe-kafka-exporter.jar");
    ZEEBE_CONTAINER.withEnv("ZEEBE_BROKER_EXPORTERS_KAFKA_ARGS_PRODUCER_SERVERS", "kafka:9092");
    ZEEBE_CONTAINER.start();
  }

  @Before
  public void setUp() {
    client = Containers.newClient(ZEEBE_CONTAINER);
  }

  @After
  public void tearDown() {
    Containers.close(client, ZEEBE_CONTAINER, KAFKA_CONTAINER);
  }

  @Configuration
  public static class TestConfig {
    @Bean
    public RecordCollector recordCollector() {
      return new RecordCollector();
    }
  }

  public static class RecordCollector implements ProtobufSourceConnector {
    List<Message> records = new ArrayList<>();

    public void connectTo(ProtobufSource source) {
      source.addListener(records::add);
    }
  }

  @Test
  public void shouldReceiveRecord() {
    // given
    final BpmnModelInstance process =
        Bpmn.createExecutableProcess("process")
            .startEvent("start")
            .serviceTask("task")
            .zeebeJobType("type")
            .endEvent()
            .done();

    // when
    client.newDeployCommand().addProcessModel(process, "process.bpmn").send().join();

    // then
    await()
        .pollInSameThread()
        .atMost(Duration.ofSeconds(10))
        .until(
            () -> {
              return recordCollector.records.size() > 0;
            });
  }
}
