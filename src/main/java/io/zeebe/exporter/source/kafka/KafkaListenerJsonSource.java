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

import io.zeebe.protocol.record.Record;
import io.zeebe.protocol.record.RecordValue;
import org.springframework.kafka.annotation.KafkaListener;

/** Kafka listener form a single zeebe topic */
public class KafkaListenerJsonSource extends AbstractRecordSource {
  @KafkaListener(containerFactory = "zeebeListenerContainerFactory", topics = "zeebe")
  public void handleRecord(Record<? extends RecordValue> message) {
    super.handleRecord(message);
  }
}
