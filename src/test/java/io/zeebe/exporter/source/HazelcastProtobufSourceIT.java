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

import static org.awaitility.Awaitility.await;

import com.google.protobuf.Message;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.zeebe.containers.ZeebeContainer;
import io.zeebe.exporter.source.hazelcast.HazelcastSource;
import io.zeebe.exporter.source.hazelcast.HazelcastSourceConfiguration;
import io.zeebe.exporter.source.hazelcast.HazelcastSourceConnector;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {
      HazelcastSourceConfiguration.class,
      HazelcastProtobufSourceIT.TestConfig.class,
    })
@DirtiesContext
public final class HazelcastProtobufSourceIT {

  private static ZeebeContainer ZEEBE_CONTAINER;

  private @Autowired RecordCollector recordCollector;

  private ZeebeClient client;

  @BeforeClass
  public static void setupClass() {
    ZEEBE_CONTAINER =
        Containers.newZeebeContainer("hazelcast-exporter.yml", "zeebe-hazelcast-exporter.jar");
    ZEEBE_CONTAINER.addExposedPort(5701);
    ZEEBE_CONTAINER.start();
    System.setProperty(
        "zeebe.exporter.source.hazelcast.address", ZEEBE_CONTAINER.getExternalAddress(5701));
  }

  @Before
  public void setUp() {
    client = Containers.newClient(ZEEBE_CONTAINER);
  }

  @After
  public void tearDown() {
    Containers.close(client, ZEEBE_CONTAINER);
  }

  @Test
  public void shouldReceiveRecord() {
    // given
    final BpmnModelInstance process =
        Bpmn.createExecutableProcess("process")
            .startEvent("start")
            .serviceTask("task")
            .zeebeJobType("type")
            .endEvent()
            .done();

    // when
    client.newDeployCommand().addProcessModel(process, "process.bpmn").send().join();

    // then
    await()
        .pollInSameThread()
        .atMost(Duration.ofSeconds(10))
        .until(
            () -> {
              return recordCollector.records.size() > 0;
            });
  }

  @Configuration
  public static class TestConfig {
    @Bean
    public RecordCollector recordCollector() {
      return new RecordCollector();
    }
  }

  public static class RecordCollector implements ProtobufSourceConnector, HazelcastSourceConnector {
    List<Message> records = new ArrayList<>();

    public void connectTo(ProtobufSource source) {
      source.addListener(records::add);
    }

    @Override
    public void connectTo(HazelcastSource source) {}

    @Override
    public Optional<Long> startPosition() {
      return Optional.empty();
    }
  }
}
