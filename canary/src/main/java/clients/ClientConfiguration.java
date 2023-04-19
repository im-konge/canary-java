/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package clients;

import common.security.SaslType;
import config.CanaryConfiguration;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.security.plain.PlainLoginModule;
import org.apache.kafka.common.security.scram.ScramLoginModule;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ClientConfiguration {
    public static Properties producerProperties(CanaryConfiguration configuration) {
        Properties properties = clientProperties(configuration);

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, String.valueOf(configuration.getReconcileInterval()));
        properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, String.valueOf(configuration.getReconcileInterval()));

        return properties;
    }

    public static Properties consumerProperties(CanaryConfiguration configuration) {
        Properties properties = clientProperties(configuration);

        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, configuration.getClientId());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.getConsumerGroupId());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

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
        if (configuration.isTlsEnabled()) {
            properties = updatePropertiesWithTlsConfiguration(properties, configuration);
        }
        if (shouldUpdatePropertiesWithSaslConfig(configuration)) {
            properties = updatePropertiesWithSaslConfiguration(properties, configuration);
        }

        return properties;
    }

    private static Properties updatePropertiesWithTlsConfiguration(Properties properties, CanaryConfiguration configuration) {
        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SSL);
        properties.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "PEM");
        properties.put(SslConfigs.SSL_TRUSTSTORE_CERTIFICATES_CONFIG, configuration.getTlsCaCert());

        properties.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PEM");
        properties.put(SslConfigs.SSL_KEYSTORE_CERTIFICATE_CHAIN_CONFIG, configuration.getTlsClientCert());
        properties.put(SslConfigs.SSL_KEYSTORE_KEY_CONFIG, configuration.getTlsClientKey());

        return null;
    }

    private static Properties updatePropertiesWithSaslConfiguration(Properties properties, CanaryConfiguration configuration) {
        SaslType saslType = SaslType.valueOf(configuration.getSaslMechanism());
        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_SSL);
        properties.put(SaslConfigs.SASL_MECHANISM, saslType.getKafkaProperty());

        String saslJaasConfig = saslType.equals(SaslType.PLAIN) ? PlainLoginModule.class.toString() : ScramLoginModule.class.toString();
        saslJaasConfig += String.format(" required username=%s password=%s", configuration.getSaslUser(), configuration.getSaslPassword());

        if (saslType.equals(SaslType.SCRAM_SHA_512)) {
            saslJaasConfig += "algorithm=SHA-512";
        }

        saslJaasConfig += ";";
        properties.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);

        return properties;
    }

    private static boolean shouldUpdatePropertiesWithSaslConfig(CanaryConfiguration configuration) {
        return configuration.getSaslMechanism() != null
            && !configuration.getSaslMechanism().isEmpty()
            && SaslType.getAllSaslTypes().contains(configuration.getSaslMechanism());
    }
}
