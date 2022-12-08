/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package servers;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class HttpServerHandler {

    private static HttpServerHandler httpServerHandler;
    private static final int HTTP_PORT = 8080;
    private Server server;

    private HttpServerHandler() {
        this.server = new Server(HTTP_PORT);

        ContextHandler livenessContext = new ContextHandler();
        livenessContext.setContextPath("/liveness");
        livenessContext.setHandler(new LivenessHandler());
        livenessContext.setAllowNullPathInfo(true);


        ContextHandler readinessContext = new ContextHandler();
        readinessContext.setContextPath("/readiness");
        readinessContext.setHandler(new ReadinessHandler());
        readinessContext.setAllowNullPathInfo(true);

        ContextHandlerCollection contexts = new ContextHandlerCollection(livenessContext, readinessContext);
        server.setHandler(contexts);
    }

    public HttpServerHandler getInstance() {
        if (httpServerHandler == null) {
            httpServerHandler = new HttpServerHandler();
        }
        return httpServerHandler;
    }

    public void startHttpServer() {
        try {
            server.start();
        } catch (Exception e)   {
            throw new RuntimeException(e);
        }
    }

    public void stopHttpServer() {
        try {
            server.start();
        } catch (Exception e)   {
            throw new RuntimeException(e);
        }
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
}
