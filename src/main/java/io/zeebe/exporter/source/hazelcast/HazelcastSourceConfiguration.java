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
package io.zeebe.exporter.source.hazelcast;

import com.google.protobuf.Message;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import io.zeebe.exporter.proto.Schema;
import io.zeebe.exporter.source.ProtobufSource;
import io.zeebe.exporter.source.ProtobufSourceConnector;
import io.zeebe.hazelcast.connect.java.ZeebeHazelcast;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {HazelcastProperties.class})
public class HazelcastSourceConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(HazelcastSourceConfiguration.class);

  @Autowired HazelcastProperties hazelcastProperties;

  @Bean
  public HazelcastInstance hazelcastInstance(HazelcastProperties hazelcastProperties) {

    final ClientConfig clientConfig = new ClientConfig();
    clientConfig.getNetworkConfig().addAddress(hazelcastProperties.getConnection());

    final var connectionRetryConfig =
        clientConfig.getConnectionStrategyConfig().getConnectionRetryConfig();

    connectionRetryConfig.setClusterConnectTimeoutMillis(
        Duration.parse(hazelcastProperties.getConnectionTimeout()).toMillis());

    LOG.info("Connecting to Hazelcast '{}'", hazelcastProperties.getConnection());

    return HazelcastClient.newHazelcastClient(clientConfig);
  }

  interface BuilderSource extends ProtobufSource, HazelcastSource {}

  @Bean
  public ZeebeHazelcast zeebeHazelcastBuilder(
      HazelcastInstance hazelcastInstance,
      @Autowired HazelcastSourceConnector hazelcastSourceConnector,
      @Autowired(required = false) List<ProtobufSourceConnector> protobufSourceConnectors) {

    final var builder = ZeebeHazelcast.newBuilder(hazelcastInstance);

    final var builderSource =
        new BuilderSource() {

          @Override
          public void addListener(Consumer<Message> listener) {
            builder.addDeploymentListener((Consumer) listener);
            builder.addWorkflowInstanceListener((Consumer) listener);
            builder.addVariableListener((Consumer) listener);
            builder.addVariableDocumentListener((Consumer) listener);
            builder.addJobListener((Consumer) listener);
            builder.addJobBatchListener((Consumer) listener);
            builder.addIncidentListener((Consumer) listener);
            builder.addTimerListener((Consumer) listener);
            builder.addMessageListener((Consumer) listener);
            builder.addMessageSubscriptionListener((Consumer) listener);
            builder.addMessageStartEventSubscriptionListener((Consumer) listener);
            builder.addWorkflowInstanceSubscriptionListener((Consumer) listener);
            builder.addWorkflowInstanceCreationListener((Consumer) listener);
            builder.addWorkflowInstanceResultListener((Consumer) listener);
            builder.addErrorListener((Consumer) listener);
          }

          public void addDeploymentListener(Consumer<Schema.DeploymentRecord> listener) {
            builder.addDeploymentListener(listener);
          }

          public void addWorkflowInstanceListener(
              Consumer<Schema.WorkflowInstanceRecord> listener) {
            builder.addWorkflowInstanceListener(listener);
          }

          public void addVariableListener(Consumer<Schema.VariableRecord> listener) {
            builder.addVariableListener(listener);
          }

          public void addVariableDocumentListener(
              Consumer<Schema.VariableDocumentRecord> listener) {
            builder.addVariableDocumentListener(listener);
          }

          public void addJobListener(Consumer<Schema.JobRecord> listener) {
            builder.addJobListener(listener);
          }

          public void addJobBatchListener(Consumer<Schema.JobBatchRecord> listener) {
            builder.addJobBatchListener(listener);
          }

          public void addIncidentListener(Consumer<Schema.IncidentRecord> listener) {
            builder.addIncidentListener(listener);
          }

          public void addTimerListener(Consumer<Schema.TimerRecord> listener) {
            builder.addTimerListener(listener);
          }

          public void addMessageListener(Consumer<Schema.MessageRecord> listener) {
            builder.addMessageListener(listener);
          }

          public void addMessageSubscriptionListener(
              Consumer<Schema.MessageSubscriptionRecord> listener) {
            builder.addMessageSubscriptionListener(listener);
          }

          public void addMessageStartEventSubscriptionListener(
              Consumer<Schema.MessageStartEventSubscriptionRecord> listener) {
            builder.addMessageStartEventSubscriptionListener(listener);
          }

          public void addWorkflowInstanceSubscriptionListener(
              Consumer<Schema.WorkflowInstanceSubscriptionRecord> listener) {
            builder.addWorkflowInstanceSubscriptionListener(listener);
          }

          public void addWorkflowInstanceCreationListener(
              Consumer<Schema.WorkflowInstanceCreationRecord> listener) {
            builder.addWorkflowInstanceCreationListener(listener);
          }

          public void addWorkflowInstanceResultListener(
              Consumer<Schema.WorkflowInstanceResultRecord> listener) {
            builder.addWorkflowInstanceResultListener(listener);
          }

          public void addErrorListener(Consumer<Schema.ErrorRecord> listener) {
            builder.addErrorListener(listener);
          }

          public void postProcessListener(Consumer<Long> listener) {
            builder.postProcessListener(listener);
          }
        };

    hazelcastSourceConnector.connectTo(builderSource);

    protobufSourceConnectors.forEach(
        connector -> {
          connector.connectTo(builderSource);
        });

    final var startOptional = hazelcastSourceConnector.startPosition();
    if (startOptional.isPresent()) {
      builder.readFrom(startOptional.get());
    } else {
      builder.readFromHead();
    }

    return builder.build();
  }
}
