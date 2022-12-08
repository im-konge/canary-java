/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package clients;

import config.CanaryConfiguration;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ClientConfiguration {
    public static Properties producerProperties(CanaryConfiguration configuration) {
        Properties properties = clientProperties(configuration);

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return properties;
    }

    public static Properties consumerProperties(CanaryConfiguration configuration) {
        Properties properties = clientProperties(configuration);

        properties.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.getConsumerGroupId());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return properties;
    }

    public static Properties adminProperties(CanaryConfiguration configuration) {
        return clientProperties(configuration);
    }

    private static Properties clientProperties(CanaryConfiguration configuration) {
        Properties properties = new Properties();

        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, configuration.getBootstrapServers());

        return updatePropertiesWithSecurityConfiguration(properties, configuration);
    }

    private static Properties updatePropertiesWithSecurityConfiguration(Properties properties, CanaryConfiguration configuration) {
        // TODO: add security configuration into properties
        return null;
    }
}
