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

import com.google.protobuf.InvalidProtocolBufferException;
import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.RecordValue;
import io.zeebe.exporter.proto.RecordTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

/** Kafka listener form a single zeebe topic */
public class KafkaListenerProtobufSource extends AbstractProtobufSource {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaListenerProtobufSource.class);

  @KafkaListener(
      containerFactory = "zeebeListenerContainerFactory",
      topics =
          "#{@'zeebe.exporter.source.kafka-io.zeebe.exporter.source.kafka.KafkaProperties'.topics}")
  public void handleRecord(Record<? extends RecordValue> message)
      throws InvalidProtocolBufferException {

    LOG.debug("Received Message {} {}", message.getRecordType(), message.getValueType());

    super.handleRecord(RecordTransformer.toProtobufMessage(message));
  }
}
