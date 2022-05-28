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

import io.camunda.zeebe.client.ZeebeClient;
import io.zeebe.containers.ZeebeContainer;
import io.zeebe.containers.ZeebeDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public class Containers {

  private static Logger newContainerLogger(final String containerName) {
    return LoggerFactory.getLogger(KafkaProtobufSourceIT.class.getName() + "." + containerName);
  }

  @SuppressWarnings("OctalInteger")
  public static ZeebeContainer newZeebeContainer(
      String exporterConfigPath, String exporterJarPath) {
    final var container =
        new ZeebeContainer(ZeebeDefaults.getInstance().getDefaultDockerImage().withTag("8.0.2"));
    final MountableFile exporterConfig =
        MountableFile.forClasspathResource(exporterConfigPath, 0775);
    final MountableFile exporterJar = MountableFile.forClasspathResource(exporterJarPath, 0775);
    final var loggingConfig = MountableFile.forClasspathResource("log4j2.xml", 0775);
    final String networkAlias = "zeebe";

    return container
        .withNetwork(Network.SHARED)
        .withNetworkAliases(networkAlias)
        .withEnv("ZEEBE_BROKER_NETWORK_ADVERTISEDHOST", networkAlias)
        .withEnv("ZEEBE_LOG_LEVEL", "info")
        .withEnv(
            "LOG4J_CONFIGURATION_FILE",
            "/usr/local/zeebe/config/log4j2.xml,/usr/local/zeebe/config/log4j2-exporter.xml")
        .withEnv(
            "SPRING_CONFIG_ADDITIONAL_LOCATION",
            "file:/usr/local/zeebe/config/" + exporterConfigPath)
        .withCopyFileToContainer(exporterJar, "/usr/local/zeebe/exporters/" + exporterJarPath)
        .withCopyFileToContainer(exporterConfig, "/usr/local/zeebe/config/" + exporterConfigPath)
        .withCopyFileToContainer(loggingConfig, "/usr/local/zeebe/config/log4j2-exporter.xml")
        .withLogConsumer(new Slf4jLogConsumer(newContainerLogger("zeebeContainer"), true));
  }

  public static KafkaContainer newKafkaContainer() {
    final var kafkaImage = DockerImageName.parse("confluentinc/cp-kafka").withTag("5.5.1");
    final var container = new KafkaContainer(kafkaImage);
    final var logConsumer = new Slf4jLogConsumer(newContainerLogger("kafkaContainer"), true);

    return container
        .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
        .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1")
        .withEmbeddedZookeeper()
        .withNetwork(Network.SHARED)
        .withNetworkAliases("kafka")
        .withExposedPorts(KafkaContainer.KAFKA_PORT)
        .withLogConsumer(logConsumer);
  }

  public static ZeebeClient newClient(ZeebeContainer zeebeContainer) {
    return ZeebeClient.newClientBuilder()
        .gatewayAddress(zeebeContainer.getExternalGatewayAddress())
        .usePlaintext()
        .build();
  }

  public static void close(AutoCloseable... closeables) {
    for (var c : closeables) {
      if (c != null) {
        try {
          c.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
