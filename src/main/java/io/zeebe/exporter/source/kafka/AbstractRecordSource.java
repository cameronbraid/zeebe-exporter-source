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

import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.RecordValue;
import io.camunda.zeebe.protocol.record.ValueType;
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
import io.zeebe.exporter.source.RecordSource;
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
        .forEach(
            consumer -> {
              try {
                consumer.accept(message);
              } catch (Throwable e) {
                throw new RuntimeException(e);
              }
            });
  }

  protected void addListener(ValueType valueType, Consumer listener) {
    final List<Consumer<Record<?>>> recordListeners =
        listeners.getOrDefault(valueType, new ArrayList<>());
    recordListeners.add(listener);
    listeners.put(valueType, recordListeners);
  }

  @Override
  public void addListener(Consumer<Record<? extends RecordValue>> listener) {
    allTypeListeners.add(listener);
  }

  @Override
  public void addDeploymentListener(Consumer<Record<DeploymentRecordValue>> listener) {
    addListener(ValueType.DEPLOYMENT, listener);
  }

  @Override
  public void addDeploymentDistributionListener(
      Consumer<Record<DeploymentDistributionRecordValue>> listener) {
    addListener(ValueType.DEPLOYMENT_DISTRIBUTION, listener);
  }

  @Override
  public void addProcessListener(Consumer<Record<Process>> listener) {
    addListener(ValueType.PROCESS, listener);
  }

  @Override
  public void addProcessInstanceListener(Consumer<Record<ProcessInstanceRecordValue>> listener) {
    addListener(ValueType.PROCESS_INSTANCE, listener);
  }

  @Override
  public void addProcessEventListener(Consumer<Record<ProcessEventRecordValue>> listener) {
    addListener(ValueType.PROCESS_EVENT, listener);
  }

  @Override
  public void addVariableListener(Consumer<Record<VariableRecordValue>> listener) {
    addListener(ValueType.VARIABLE, listener);
  }

  @Override
  public void addVariableDocumentListener(Consumer<Record<VariableDocumentRecordValue>> listener) {
    addListener(ValueType.VARIABLE_DOCUMENT, listener);
  }

  @Override
  public void addJobListener(Consumer<Record<JobRecordValue>> listener) {
    addListener(ValueType.JOB, listener);
  }

  @Override
  public void addJobBatchListener(Consumer<Record<JobBatchRecordValue>> listener) {
    addListener(ValueType.JOB_BATCH, listener);
  }

  @Override
  public void addIncidentListener(Consumer<Record<IncidentRecordValue>> listener) {
    addListener(ValueType.INCIDENT, listener);
  }

  @Override
  public void addTimerListener(Consumer<Record<TimerRecordValue>> listener) {
    addListener(ValueType.TIMER, listener);
  }

  @Override
  public void addMessageListener(Consumer<Record<MessageRecordValue>> listener) {
    addListener(ValueType.MESSAGE, listener);
  }

  @Override
  public void addMessageSubscriptionListener(
      Consumer<Record<MessageSubscriptionRecordValue>> listener) {
    addListener(ValueType.MESSAGE_SUBSCRIPTION, listener);
  }

  @Override
  public void addMessageStartEventSubscriptionListener(
      Consumer<Record<MessageStartEventSubscriptionRecordValue>> listener) {
    addListener(ValueType.MESSAGE_START_EVENT_SUBSCRIPTION, listener);
  }

  @Override
  public void addProcessMessageSubscriptionListener(
      Consumer<Record<ProcessMessageSubscriptionRecordValue>> listener) {
    addListener(ValueType.PROCESS_MESSAGE_SUBSCRIPTION, listener);
  }

  @Override
  public void addProcessInstanceCreationListener(
      Consumer<Record<ProcessInstanceCreationRecordValue>> listener) {
    addListener(ValueType.PROCESS_INSTANCE_CREATION, listener);
  }

  @Override
  public void addErrorListener(Consumer<Record<ErrorRecordValue>> listener) {
    addListener(ValueType.ERROR, listener);
  }
}
