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
package io.zeebe.exporter.source.kafka;

import com.google.protobuf.Message;
import io.zeebe.exporter.proto.Schema;
import io.zeebe.exporter.source.ProtobufSource;
import java.util.function.Consumer;

public abstract class AbstractProtobufSource extends AbstractListenerSource<Message>
    implements ProtobufSource {

  @Override
  public void addListener(Consumer<Message> listener) {
    addListener(Message.class, listener);
  }

  @Override
  public void addDeploymentListener(Consumer<Schema.DeploymentRecord> listener) {
    addListener(Schema.DeploymentRecord.class, listener);
  }

  @Override
  public void addDeploymentDistributionListener(
      Consumer<Schema.DeploymentDistributionRecord> listener) {
    addListener(Schema.DeploymentDistributionRecord.class, listener);
  }

  @Override
  public void addProcessListener(Consumer<Schema.ProcessRecord> listener) {
    addListener(Schema.ProcessRecord.class, listener);
  }

  @Override
  public void addProcessInstanceListener(Consumer<Schema.ProcessInstanceRecord> listener) {
    addListener(Schema.ProcessInstanceRecord.class, listener);
  }

  @Override
  public void addProcessEventListener(Consumer<Schema.ProcessEventRecord> listener) {
    addListener(Schema.ProcessEventRecord.class, listener);
  }

  @Override
  public void addVariableListener(Consumer<Schema.VariableRecord> listener) {
    addListener(Schema.VariableRecord.class, listener);
  }

  @Override
  public void addVariableDocumentListener(Consumer<Schema.VariableDocumentRecord> listener) {
    addListener(Schema.VariableDocumentRecord.class, listener);
  }

  @Override
  public void addJobListener(Consumer<Schema.JobRecord> listener) {
    addListener(Schema.JobRecord.class, listener);
  }

  @Override
  public void addJobBatchListener(Consumer<Schema.JobBatchRecord> listener) {
    addListener(Schema.JobBatchRecord.class, listener);
  }

  @Override
  public void addIncidentListener(Consumer<Schema.IncidentRecord> listener) {
    addListener(Schema.IncidentRecord.class, listener);
  }

  @Override
  public void addTimerListener(Consumer<Schema.TimerRecord> listener) {
    addListener(Schema.TimerRecord.class, listener);
  }

  @Override
  public void addMessageListener(Consumer<Schema.MessageRecord> listener) {
    addListener(Schema.MessageRecord.class, listener);
  }

  @Override
  public void addMessageSubscriptionListener(Consumer<Schema.MessageSubscriptionRecord> listener) {
    addListener(Schema.MessageSubscriptionRecord.class, listener);
  }

  @Override
  public void addMessageStartEventSubscriptionListener(
      Consumer<Schema.MessageStartEventSubscriptionRecord> listener) {
    addListener(Schema.MessageStartEventSubscriptionRecord.class, listener);
  }

  @Override
  public void addProcessMessageSubscriptionListener(
      Consumer<Schema.ProcessMessageSubscriptionRecord> listener) {
    addListener(Schema.ProcessMessageSubscriptionRecord.class, listener);
  }

  @Override
  public void addProcessInstanceCreationListener(
      Consumer<Schema.ProcessInstanceCreationRecord> listener) {
    addListener(Schema.ProcessInstanceCreationRecord.class, listener);
  }

  @Override
  public void addErrorListener(Consumer<Schema.ErrorRecord> listener) {
    addListener(Schema.ErrorRecord.class, listener);
  }
}
