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

import com.google.protobuf.Message;
import io.zeebe.exporter.proto.Schema;
import java.util.function.Consumer;

public interface ProtobufSource {
  public void addListener(Consumer<Message> listener);

  public void addDeploymentListener(Consumer<Schema.DeploymentRecord> listener);

  public void addWorkflowInstanceListener(Consumer<Schema.WorkflowInstanceRecord> listener);

  public void addVariableListener(Consumer<Schema.VariableRecord> listener);

  public void addVariableDocumentListener(Consumer<Schema.VariableDocumentRecord> listener);

  public void addJobListener(Consumer<Schema.JobRecord> listener);

  public void addJobBatchListener(Consumer<Schema.JobBatchRecord> listener);

  public void addIncidentListener(Consumer<Schema.IncidentRecord> listener);

  public void addTimerListener(Consumer<Schema.TimerRecord> listener);

  public void addMessageListener(Consumer<Schema.MessageRecord> listener);

  public void addMessageSubscriptionListener(Consumer<Schema.MessageSubscriptionRecord> listener);

  public void addMessageStartEventSubscriptionListener(
      Consumer<Schema.MessageStartEventSubscriptionRecord> listener);

  public void addWorkflowInstanceSubscriptionListener(
      Consumer<Schema.WorkflowInstanceSubscriptionRecord> listener);

  public void addWorkflowInstanceCreationListener(
      Consumer<Schema.WorkflowInstanceCreationRecord> listener);

  public void addWorkflowInstanceResultListener(
      Consumer<Schema.WorkflowInstanceResultRecord> listener);

  public void addErrorListener(Consumer<Schema.ErrorRecord> listener);
}
