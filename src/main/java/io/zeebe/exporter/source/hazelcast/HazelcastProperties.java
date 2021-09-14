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

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("zeebe.exporter.source.hazelcast")
public class HazelcastProperties {

  private String address = "localhost:5701";
  private String connectionTimeout = "PT30S";

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getConnectionTimeout() {
    return connectionTimeout;
  }

  public void setConnectionTimeout(String connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }
}
