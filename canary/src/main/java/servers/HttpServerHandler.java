/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package servers;

import common.metrics.MetricsRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import status.ConsumingStatus;
import status.StatusService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class HttpServerHandler {

    private static final Logger LOGGER = LogManager.getLogger(HttpServerHandler.class);
    private static final int HTTP_PORT = 8080;
    private StatusService statusService;
    private Server server;

    public HttpServerHandler(StatusService statusService) {
        this.server = new Server(HTTP_PORT);
        this.statusService = statusService;

        ContextHandler livenessContext = new ContextHandler();
        livenessContext.setContextPath("/liveness");
        livenessContext.setHandler(new LivenessHandler());
        livenessContext.setAllowNullPathInfo(true);


        ContextHandler readinessContext = new ContextHandler();
        readinessContext.setContextPath("/readiness");
        readinessContext.setHandler(new ReadinessHandler());
        readinessContext.setAllowNullPathInfo(true);

        ContextHandler metricsContext = new ContextHandler();
        metricsContext.setContextPath("/metrics");
        metricsContext.setHandler(new MetricsHandler());
        metricsContext.setAllowNullPathInfo(true);

        ContextHandler statusContext = new ContextHandler();
        statusContext.setContextPath("/status");
        statusContext.setHandler(new StatusHandler());
        statusContext.setAllowNullPathInfo(true);

        ContextHandlerCollection contexts = new ContextHandlerCollection(livenessContext, readinessContext, metricsContext, statusContext);
        server.setHandler(contexts);
    }

    public void startHttpServer() {
        try {
            getServer().start();
        } catch (Exception e)   {
            LOGGER.error("Failed to start the webserver", e);
            throw new RuntimeException(e);
        }
    }

    public void stopHttpServer() {
        try {
            getServer().stop();
        } catch (Exception e)   {
            LOGGER.error("Failed to stop the webserver", e);
            throw new RuntimeException(e);
        }
    }

    private Server getServer() {
        return server;
    }

    public static class LivenessHandler extends AbstractHandler {
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.setContentType("application/json");

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("{\"status\": \"ok\"}");

            baseRequest.setHandled(true);
        }
    }

    public static class ReadinessHandler extends AbstractHandler {
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.setContentType("application/json");

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("{\"status\": \"ok\"}");

            baseRequest.setHandled(true);
        }
    }

    public class MetricsHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.setContentType("text/plain");

            response.setStatus(HttpServletResponse.SC_OK);
            MetricsRegistry.getInstance().getPrometheusMeterRegistry().scrape(response.getWriter());

            baseRequest.setHandled(true);
        }
    }

    public class StatusHandler extends AbstractHandler {

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
            ConsumingStatus consumingStatus = statusService.getConsumingStatus();
            response.setContentType("application/json");

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(consumingStatus.toJsonString());

            baseRequest.setHandled(true);
        }
    }
}
