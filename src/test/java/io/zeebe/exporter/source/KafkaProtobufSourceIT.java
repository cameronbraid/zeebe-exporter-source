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
import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.DeploymentEvent;
import io.zeebe.containers.ZeebeBrokerContainer;
import io.zeebe.containers.ZeebePort;
import io.zeebe.exporter.source.kafka.KafkaProtobufConfiguration;
import io.zeebe.model.bpmn.Bpmn;
import io.zeebe.model.bpmn.BpmnModelInstance;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.MountableFile;

/**
 * This tests the deployment of the exporter into a Zeebe broker in a as-close-to-production way as
 * possible, by starting a Zeebe container and deploying the exporter as one normally would. As the
 * verification tools are somewhat limited at the moment, we only verify that some things are
 * exported; a full verification of the behaviour is still done in the main exporter module. Once
 * verification tools get better, all the verification should be moved into this module.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {KafkaProtobufConfiguration.class, KafkaProtobufSourceIT.TestConfig.class})
@DirtiesContext
public final class KafkaProtobufSourceIT {

  private static final KafkaContainer KAFKA_CONTAINER = newKafkaContainer();
  private static final ZeebeBrokerContainer ZEEBE_CONTAINER = newZeebeContainer();

  @ClassRule
  public static final RuleChain RULE_CHAIN =
      RuleChain.outerRule(KAFKA_CONTAINER).around(ZEEBE_CONTAINER);

  private @Autowired RecordCollector recordCollector;
  private ZeebeClient client;

  @BeforeClass
  public static void setupClass() {
    final var bootstrapServer =
        "localhost:" + KAFKA_CONTAINER.getMappedPort(KafkaContainer.KAFKA_PORT);
    System.setProperty(
        "zeebe.exporter.source.kafka.consumerProperties",
        String.format(
            "bootstrap.servers=%s\ngroup.id=kafka-source-it\nauto.offset.reset=earliest",
            bootstrapServer));
  }

  @Before
  public void setUp() {
    client = newClient();
  }

  @After
  public void tearDown() {
    if (client != null) {
      client.close();
      client = null;
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
    final DeploymentEvent deploymentEvent =
        client.newDeployCommand().addWorkflowModel(process, "process.bpmn").send().join();

    // then
    await()
        .pollInSameThread()
        .atMost(Duration.ofSeconds(10))
        .until(
            () -> {
              return recordCollector.records.size() > 0;
            });
  }

  private ZeebeClient newClient() {
    return ZeebeClient.newClientBuilder()
        .brokerContactPoint(ZEEBE_CONTAINER.getExternalAddress(ZeebePort.GATEWAY))
        .usePlaintext()
        .build();
  }

  @SuppressWarnings("OctalInteger")
  private static ZeebeBrokerContainer newZeebeContainer() {
    final ZeebeBrokerContainer container =
        new ZeebeBrokerContainer(ZeebeClient.class.getPackage().getImplementationVersion());
    final MountableFile exporterJar =
        MountableFile.forClasspathResource("zeebe-kafka-exporter.jar", 0775);
    final MountableFile exporterConfig =
        MountableFile.forClasspathResource("kafka-protobuf-exporter.yml", 0775);
    final String networkAlias = "zeebe";

    return container
        .withNetwork(Network.SHARED)
        .withNetworkAliases(networkAlias)
        .withEnv("ZEEBE_BROKER_NETWORK_HOST", "0.0.0.0")
        .withEnv("ZEEBE_BROKER_NETWORK_ADVERTISEDHOST", networkAlias)
        .withEnv("ZEEBE_BROKER_EXPORTERS_KAFKA_ARGS_PRODUCER_SERVERS", "kafka:9092")
        .withCopyFileToContainer(exporterJar, "/usr/local/zeebe/lib/zeebe-kafka-exporter.jar")
        .withCopyFileToContainer(exporterConfig, "/usr/local/zeebe/config/kafka-exporter.yml")
        .withEnv(
            "SPRING_CONFIG_ADDITIONAL_LOCATION", "file:/usr/local/zeebe/config/kafka-exporter.yml")
        .withLogConsumer(new Slf4jLogConsumer(newContainerLogger("zeebeContainer"), true));
  }

  private static KafkaContainer newKafkaContainer() {
    final KafkaContainer container = new KafkaContainer("5.5.1");
    return container
        .withEmbeddedZookeeper()
        .withNetwork(Network.SHARED)
        .withNetworkAliases("kafka")
        .withExposedPorts(KafkaContainer.KAFKA_PORT)
        .withLogConsumer(new Slf4jLogConsumer(newContainerLogger("kafkaContainer"), true));
  }

  private static Logger newContainerLogger(final String containerName) {
    return LoggerFactory.getLogger(KafkaProtobufSourceIT.class.getName() + "." + containerName);
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
}
