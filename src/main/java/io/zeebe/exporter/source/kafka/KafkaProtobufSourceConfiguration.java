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

import com.google.protobuf.Message;
import io.zeebe.exporters.kafka.serde.ProtobufRecordDeserializer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
@EnableKafka
@EnableConfigurationProperties(value = {KafkaProperties.class})
public class KafkaProtobufSourceConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaProtobufSourceConfiguration.class);

  @Autowired KafkaProperties kafkaProperties;

  @Bean
  public ConsumerFactory<Long, Message> zeebeConsumerFactory() {
    final Properties props = kafkaProperties.getConsumerProperties();
    final Map<String, Object> p = props == null ? new HashMap<>() : new HashMap(props);

    LOG.info("Connecting to Kafka '{}'", p.get("bootstrap.servers"));

    return new DefaultKafkaConsumerFactory<>(
        p, new LongDeserializer(), new ProtobufRecordDeserializer());
  }

  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Long, Message>>
      zeebeListenerContainerFactory() {
    final ConcurrentKafkaListenerContainerFactory<Long, Message> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(zeebeConsumerFactory());
    return factory;
  }

  @Bean
  public ConnectConnectors connectConnectors() {
    return new ConnectConnectors();
  }

  @Bean
  public KafkaListenerProtobufSource kafkaListenerProtobufSource() {
    return new KafkaListenerProtobufSource();
  }
}
