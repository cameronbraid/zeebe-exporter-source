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

  public void addListener(Consumer<Message> listener) {
    addListener(Message.class, listener);
  }

  public void addDeploymentListener(Consumer<Schema.DeploymentRecord> listener) {
    addListener(Schema.DeploymentRecord.class, listener);
  }

  public void addWorkflowInstanceListener(Consumer<Schema.WorkflowInstanceRecord> listener) {
    addListener(Schema.WorkflowInstanceRecord.class, listener);
  }

  public void addVariableListener(Consumer<Schema.VariableRecord> listener) {
    addListener(Schema.VariableRecord.class, listener);
  }

  public void addVariableDocumentListener(Consumer<Schema.VariableDocumentRecord> listener) {
    addListener(Schema.VariableDocumentRecord.class, listener);
  }

  public void addJobListener(Consumer<Schema.JobRecord> listener) {
    addListener(Schema.JobRecord.class, listener);
  }

  public void addJobBatchListener(Consumer<Schema.JobBatchRecord> listener) {
    addListener(Schema.JobBatchRecord.class, listener);
  }

  public void addIncidentListener(Consumer<Schema.IncidentRecord> listener) {
    addListener(Schema.IncidentRecord.class, listener);
  }

  public void addTimerListener(Consumer<Schema.TimerRecord> listener) {
    addListener(Schema.TimerRecord.class, listener);
  }

  public void addMessageListener(Consumer<Schema.MessageRecord> listener) {
    addListener(Schema.MessageRecord.class, listener);
  }

  public void addMessageSubscriptionListener(Consumer<Schema.MessageSubscriptionRecord> listener) {
    addListener(Schema.MessageSubscriptionRecord.class, listener);
  }

  public void addMessageStartEventSubscriptionListener(
      Consumer<Schema.MessageStartEventSubscriptionRecord> listener) {
    addListener(Schema.MessageStartEventSubscriptionRecord.class, listener);
  }

  public void addWorkflowInstanceSubscriptionListener(
      Consumer<Schema.WorkflowInstanceSubscriptionRecord> listener) {
    addListener(Schema.WorkflowInstanceSubscriptionRecord.class, listener);
  }

  public void addWorkflowInstanceCreationListener(
      Consumer<Schema.WorkflowInstanceCreationRecord> listener) {
    addListener(Schema.WorkflowInstanceCreationRecord.class, listener);
  }

  public void addWorkflowInstanceResultListener(
      Consumer<Schema.WorkflowInstanceResultRecord> listener) {
    addListener(Schema.WorkflowInstanceResultRecord.class, listener);
  }

  public void addErrorListener(Consumer<Schema.ErrorRecord> listener) {
    addListener(Schema.ErrorRecord.class, listener);
  }
}
