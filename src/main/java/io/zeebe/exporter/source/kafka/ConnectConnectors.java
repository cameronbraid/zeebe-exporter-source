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

import io.zeebe.exporter.source.ProtobufSource;
import io.zeebe.exporter.source.ProtobufSourceConnector;
import io.zeebe.exporter.source.RecordSource;
import io.zeebe.exporter.source.RecordSourceConnector;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

public class ConnectConnectors {

  @Autowired(required = false)
  ProtobufSource protobufSource;

  @Autowired(required = false)
  List<ProtobufSourceConnector> protobufSourceConnectors;

  @Autowired(required = false)
  List<RecordSourceConnector> recordSourceConnectors;

  @Autowired(required = false)
  RecordSource recordSource;

  @PostConstruct
  public void connectConnectors() {

    if (protobufSourceConnectors != null && protobufSourceConnectors.size() > 0) {
      if (protobufSource == null) {
        throw new RuntimeException(
            "There are Consumer<ProtobufSource> beans defined but protobuf support isn't turned on.  See zeebe.importer.kafk.enabled and zeebe.exporter.source.kafka.format:proto");
      }
      protobufSourceConnectors.forEach(connector -> connector.connectTo(protobufSource));
    }

    if (recordSourceConnectors != null && recordSourceConnectors.size() > 0) {
      if (recordSource == null && protobufSource == null) {
        throw new RuntimeException(
            "There are Consumer<RecordSource> beans defined but protobuf or json support isn't turned on.  See zeebe.importer.kafk.enabled and zeebe.exporter.source.kafka.format:json|protobuf");
      }

      if (recordSource != null) {
        recordSourceConnectors.forEach(connector -> connector.connectTo(recordSource));
      } else {

        throw new RuntimeException("protobuf to record converter not yet implemented");
        // RecordToProtoSourceAdapter protobufSourceAdapter = new
        // RecordToProtoSourceAdapter(protobufSource);
        // recordSourceConsumers.forEach(consumer->consumer.accept(protobufSourceAdapter));

      }
    }
  }
}
