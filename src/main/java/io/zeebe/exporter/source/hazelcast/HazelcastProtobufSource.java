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

import io.zeebe.exporter.source.ProtobufSource;
import java.util.function.Consumer;

public interface HazelcastProtobufSource extends ProtobufSource {
  /**
   * Register a listener that is called when an item is read from the ringbuffer and consumed by the
   * registered listeners. The listener is called with the next sequence number of the ringbuffer.
   * It can be used to store the sequence number externally.
   */
  public void postProcessListener(Consumer<Long> positionConsumer);
}
