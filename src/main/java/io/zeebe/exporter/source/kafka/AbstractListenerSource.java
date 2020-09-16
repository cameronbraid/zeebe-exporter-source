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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractListenerSource<M> {

  private final Map<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();

  public void handleRecord(M message) throws InvalidProtocolBufferException {
    listeners
        .entrySet()
        .forEach(
            entry -> {
              if (entry.getKey().isAssignableFrom(message.getClass())) {
                entry.getValue().forEach(listener -> ((Consumer) listener).accept(message));
              }
            });
  }

  protected <T extends M> void addListener(Class<T> recordType, Consumer<T> listener) {
    final List<Consumer<?>> recordListeners = listeners.getOrDefault(recordType, new ArrayList<>());
    recordListeners.add(listener);
    listeners.put(recordType, recordListeners);
  }
}
