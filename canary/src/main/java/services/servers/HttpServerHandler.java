/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package services.servers;

import com.sun.net.httpserver.HttpServer;
import services.health.HealthCheck;

import java.net.InetSocketAddress;

public class HttpServerHandler {

    private static HttpServerHandler httpServerHandler;
    private HttpServer server;

    private HttpServerHandler() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(8080), 0);
        } catch (Exception e) {
            //add LOGGER here, but wait for another PR to be merged, to prevent conflicts
            e.printStackTrace();
        }
    }

    public HttpServerHandler getInstance() {
        if (httpServerHandler == null) {
            httpServerHandler = new HttpServerHandler();
        }
        return httpServerHandler;
    }

    public void startHttpServer() {
        this.server.createContext("/liveness", new HealthCheck.LivenessHandler());
        this.server.createContext("/readiness", new HealthCheck.ReadinessHandler());
        // creates a default executor
        this.server.setExecutor(null);
        this.server.start();
    }

    public void stopHttpServer() {
        this.server.stop(0);
    }
}
