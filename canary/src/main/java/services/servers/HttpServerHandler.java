/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package services.servers;

import com.sun.net.httpserver.HttpServer;
import services.health.HealthCheck;

import java.net.InetSocketAddress;

public class HttpServerHandler {

    public static void createHttpServer() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/liveness", new HealthCheck.LivenessHandler());
        server.createContext("/readiness", new HealthCheck.ReadinessHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }
}
