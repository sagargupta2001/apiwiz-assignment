package org.apiwiz.client;

import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class WebClientProducer {

    @Inject
    Vertx vertx;

    @Produces
    public WebClient webClient() {
        // Retrieve SSL configuration dynamically
        boolean sslEnabled = Boolean.parseBoolean(System.getenv("SSL_ENABLED"));

        WebClientOptions options = new WebClientOptions()
                .setDefaultHost("localhost")
                .setDefaultPort(8080);

        if (sslEnabled) {
            String keystorePath = System.getenv("KEYSTORE_PATH");
            String keystorePassword = System.getenv("KEYSTORE_PASSWORD");
            String truststorePath = System.getenv("TRUSTSTORE_PATH");
            String truststorePassword = System.getenv("TRUSTSTORE_PASSWORD");

            // Enable SSL
            options.setSsl(true).setTrustAll(false);

            if (keystorePath != null && keystorePassword != null) {
                options.setKeyCertOptions(new JksOptions().setPath(keystorePath).setPassword(keystorePassword));
            }

            if (truststorePath != null && truststorePassword != null) {
                options.setTrustStoreOptions(new JksOptions().setPath(truststorePath).setPassword(truststorePassword));
            }
        } else {
            // If SSL is disabled, make sure trust is not set
            options.setSsl(false)
                    .setTrustAll(true); // Accept all certificates in non-SSL mode (be cautious in prod)
        }

        return WebClient.create(vertx, options);
    }
}
