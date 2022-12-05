/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package services.clients;

import config.CanaryConfiguration;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import services.clients.security.Auth;

import java.util.Properties;

public class ClientConfiguration {

    public static Properties producerProperties(CanaryConfiguration configuration) {
        return clientProperties(configuration, ClientType.PRODUCER);
    }

    public static Properties consumerProperties(CanaryConfiguration configuration) {
        return clientProperties(configuration, ClientType.CONSUMER);
    }

    public static Properties adminProperties(CanaryConfiguration configuration) {
        return clientProperties(configuration, ClientType.ADMIN);
    }

    private static Properties clientProperties(CanaryConfiguration configuration, ClientType clientType) {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getBootstrapServers());

        if (clientType == ClientType.PRODUCER || clientType == ClientType.CONSUMER) {
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            if (clientType == ClientType.CONSUMER) {
                properties.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.getConsumerGroupId());
            }
        }

        return Auth.updatePropertiesWithSecurityConfiguration(properties, configuration);
    }
}
