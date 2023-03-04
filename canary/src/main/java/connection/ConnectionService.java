/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package connection;

import clients.AdminClient;
import common.metrics.MetricsRegistry;
import config.CanaryConfiguration;
import config.CanaryConstants;
import org.apache.kafka.common.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;

public class ConnectionService {

    private static final Logger LOGGER = LogManager.getLogger(ConnectionService.class);

    private final double[] connectionLatencyBuckets;
    private final boolean isTlsEnabled;
    private final AdminClient adminClient;

    public ConnectionService(CanaryConfiguration configuration, AdminClient adminClient) {
        this.connectionLatencyBuckets = configuration.getConnectionCheckLatencyBuckets();
        this.isTlsEnabled = configuration.isTlsEnabled();
        this.adminClient = adminClient;
    }

    public void checkConnection() {
        Collection<Node> brokersList = adminClient.listBrokers();

        if (!brokersList.isEmpty()) {
            for (Node broker : brokersList) {
                long startOfConnection = System.currentTimeMillis();
                boolean connected = false;
                Socket socket = new Socket();

                try {

                    // connect to broker
                    socket.connect(new InetSocketAddress(broker.host(), broker.port()), CanaryConstants.CONNECTION_TIMEOUT_MS_DEFAULT);

                    // check broker's availability
                    socket.getInputStream().read();

                    // lastly, check if socket is connected
                    connected = socket.isConnected();
                } catch (Exception e) {
                    MetricsRegistry.getInstance().getConnectionErrorTotal(broker.idString(), false).increment();

                    throw new RuntimeException(String.format("Failed to connect to broker: %s, due to: %s", broker.idString(), e.getMessage()));
                } finally {
                    long duration = System.currentTimeMillis() - startOfConnection;

                    if (connected) {
                        LOGGER.info("Connected to broker {} in {} ms", broker.idString(), duration);
                    } else {
                        LOGGER.error("Error connecting to broker {} in {} ms", broker.idString(), duration);
                    }

                    // add duration to connection latency metric
                    MetricsRegistry.getInstance().getConnectionLatency(broker.idString(), connected, connectionLatencyBuckets).record(duration);
                    closeSocket(socket);
                }
            }
        } else {
            throw new RuntimeException("Failed to connect to Kafka using Admin client, connection check is not possible");
        }
    }

    private void closeSocket(Socket socket) {
        try {
            // closing socket and its connection
            socket.close();
        } catch (Exception e) {
            LOGGER.error("Error closing the connection due to: {}", e.getMessage());
        }
    }

    private Socket getSocketBasedOnAuth() {
        if (isTlsEnabled) {
            // TODO: return socket or KafkaChannel
            return new Socket();
        }
        return new Socket();
    }
}
