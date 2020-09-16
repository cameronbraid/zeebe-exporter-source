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

import io.zeebe.exporter.source.RecordSource;
import io.zeebe.protocol.record.Record;
import io.zeebe.protocol.record.RecordValue;
import io.zeebe.protocol.record.ValueType;
import io.zeebe.protocol.record.value.DeploymentRecordValue;
import io.zeebe.protocol.record.value.ErrorRecordValue;
import io.zeebe.protocol.record.value.IncidentRecordValue;
import io.zeebe.protocol.record.value.JobBatchRecordValue;
import io.zeebe.protocol.record.value.JobRecordValue;
import io.zeebe.protocol.record.value.MessageRecordValue;
import io.zeebe.protocol.record.value.MessageStartEventSubscriptionRecordValue;
import io.zeebe.protocol.record.value.MessageSubscriptionRecordValue;
import io.zeebe.protocol.record.value.TimerRecordValue;
import io.zeebe.protocol.record.value.VariableDocumentRecordValue;
import io.zeebe.protocol.record.value.VariableRecordValue;
import io.zeebe.protocol.record.value.WorkflowInstanceCreationRecordValue;
import io.zeebe.protocol.record.value.WorkflowInstanceRecordValue;
import io.zeebe.protocol.record.value.WorkflowInstanceResultRecordValue;
import io.zeebe.protocol.record.value.WorkflowInstanceSubscriptionRecordValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractRecordSource implements RecordSource {

  private final Map<ValueType, List<Consumer<Record<? extends RecordValue>>>> listeners =
      new HashMap<>();
  private final List<Consumer<Record<? extends RecordValue>>> allTypeListeners = new ArrayList<>();

  public void handleRecord(Record<? extends RecordValue> message) {
    allTypeListeners.forEach(consumer -> consumer.accept(message));
    listeners
        .getOrDefault(message.getValueType(), Collections.emptyList())
        .forEach(consumer -> consumer.accept(message));
  }

  protected void addListener(ValueType valueType, Consumer listener) {
    final List<Consumer<Record<?>>> recordListeners =
        listeners.getOrDefault(valueType, new ArrayList<>());
    recordListeners.add(listener);
    listeners.put(valueType, recordListeners);
  }

  public void addListener(Consumer<Record<? extends RecordValue>> listener) {
    allTypeListeners.add(listener);
  }

  public void addDeploymentListener(Consumer<Record<DeploymentRecordValue>> listener) {
    addListener(ValueType.DEPLOYMENT, listener);
  }

  public void addWorkflowInstanceListener(Consumer<Record<WorkflowInstanceRecordValue>> listener) {
    addListener(ValueType.WORKFLOW_INSTANCE, listener);
  }

  public void addVariableListener(Consumer<Record<VariableRecordValue>> listener) {
    addListener(ValueType.VARIABLE, listener);
  }

  public void addVariableDocumentListener(Consumer<Record<VariableDocumentRecordValue>> listener) {
    addListener(ValueType.VARIABLE_DOCUMENT, listener);
  }

  public void addJobListener(Consumer<Record<JobRecordValue>> listener) {
    addListener(ValueType.JOB, listener);
  }

  public void addJobBatchListener(Consumer<Record<JobBatchRecordValue>> listener) {
    addListener(ValueType.JOB_BATCH, listener);
  }

  public void addIncidentListener(Consumer<Record<IncidentRecordValue>> listener) {
    addListener(ValueType.INCIDENT, listener);
  }

  public void addTimerListener(Consumer<Record<TimerRecordValue>> listener) {
    addListener(ValueType.TIMER, listener);
  }

  public void addMessageListener(Consumer<Record<MessageRecordValue>> listener) {
    addListener(ValueType.MESSAGE, listener);
  }

  public void addMessageSubscriptionListener(
      Consumer<Record<MessageSubscriptionRecordValue>> listener) {
    addListener(ValueType.MESSAGE_SUBSCRIPTION, listener);
  }

  public void addMessageStartEventSubscriptionListener(
      Consumer<Record<MessageStartEventSubscriptionRecordValue>> listener) {
    addListener(ValueType.MESSAGE_START_EVENT_SUBSCRIPTION, listener);
  }

  public void addWorkflowInstanceSubscriptionListener(
      Consumer<Record<WorkflowInstanceSubscriptionRecordValue>> listener) {
    addListener(ValueType.WORKFLOW_INSTANCE_SUBSCRIPTION, listener);
  }

  public void addWorkflowInstanceCreationListener(
      Consumer<Record<WorkflowInstanceCreationRecordValue>> listener) {
    addListener(ValueType.WORKFLOW_INSTANCE_CREATION, listener);
  }

  public void addWorkflowInstanceResultListener(
      Consumer<Record<WorkflowInstanceResultRecordValue>> listener) {
    addListener(ValueType.WORKFLOW_INSTANCE_RESULT, listener);
  }

  public void addErrorListener(Consumer<Record<ErrorRecordValue>> listener) {
    addListener(ValueType.ERROR, listener);
  }
}
