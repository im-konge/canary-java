/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
import config.CanaryConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import servers.HttpServerHandler;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        CanaryConfiguration configuration = CanaryConfiguration.fromMap(System.getenv());

        Canary canary = new Canary(configuration);

        LOGGER.info("Starting HTTP server");
        HttpServerHandler.getInstance().startHttpServer();

        canary.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down HTTP server");
            HttpServerHandler.getInstance().stopHttpServer();

            canary.stop();
        }));
    }
}
