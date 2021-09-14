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

import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.RecordValue;
import io.camunda.zeebe.protocol.record.value.DeploymentDistributionRecordValue;
import io.camunda.zeebe.protocol.record.value.DeploymentRecordValue;
import io.camunda.zeebe.protocol.record.value.ErrorRecordValue;
import io.camunda.zeebe.protocol.record.value.IncidentRecordValue;
import io.camunda.zeebe.protocol.record.value.JobBatchRecordValue;
import io.camunda.zeebe.protocol.record.value.JobRecordValue;
import io.camunda.zeebe.protocol.record.value.MessageRecordValue;
import io.camunda.zeebe.protocol.record.value.MessageStartEventSubscriptionRecordValue;
import io.camunda.zeebe.protocol.record.value.MessageSubscriptionRecordValue;
import io.camunda.zeebe.protocol.record.value.ProcessEventRecordValue;
import io.camunda.zeebe.protocol.record.value.ProcessInstanceCreationRecordValue;
import io.camunda.zeebe.protocol.record.value.ProcessInstanceRecordValue;
import io.camunda.zeebe.protocol.record.value.ProcessMessageSubscriptionRecordValue;
import io.camunda.zeebe.protocol.record.value.TimerRecordValue;
import io.camunda.zeebe.protocol.record.value.VariableDocumentRecordValue;
import io.camunda.zeebe.protocol.record.value.VariableRecordValue;
import io.camunda.zeebe.protocol.record.value.deployment.Process;
import java.util.function.Consumer;

public interface RecordSource {

  public void addListener(Consumer<Record<? extends RecordValue>> listener);

  public void addDeploymentListener(Consumer<Record<DeploymentRecordValue>> listener);

  public void addDeploymentDistributionListener(
      Consumer<Record<DeploymentDistributionRecordValue>> listener);

  public void addProcessListener(Consumer<Record<Process>> listener);

  public void addProcessInstanceListener(Consumer<Record<ProcessInstanceRecordValue>> listener);

  public void addProcessEventListener(Consumer<Record<ProcessEventRecordValue>> listener);

  public void addVariableListener(Consumer<Record<VariableRecordValue>> listener);

  public void addVariableDocumentListener(Consumer<Record<VariableDocumentRecordValue>> listener);

  public void addJobListener(Consumer<Record<JobRecordValue>> listener);

  public void addJobBatchListener(Consumer<Record<JobBatchRecordValue>> listener);

  public void addIncidentListener(Consumer<Record<IncidentRecordValue>> listener);

  public void addTimerListener(Consumer<Record<TimerRecordValue>> listener);

  public void addMessageListener(Consumer<Record<MessageRecordValue>> listener);

  public void addMessageSubscriptionListener(
      Consumer<Record<MessageSubscriptionRecordValue>> listener);

  public void addMessageStartEventSubscriptionListener(
      Consumer<Record<MessageStartEventSubscriptionRecordValue>> listener);

  public void addProcessMessageSubscriptionListener(
      Consumer<Record<ProcessMessageSubscriptionRecordValue>> listener);

  public void addProcessInstanceCreationListener(
      Consumer<Record<ProcessInstanceCreationRecordValue>> listener);

  public void addErrorListener(Consumer<Record<ErrorRecordValue>> listener);
}
