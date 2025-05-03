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
    private static final String SSL_ENABLED = "SSL_ENABLED";
    private static final String KEYSTORE_PATH = "KEYSTORE_PATH";
    private static final String KEYSTORE_PASSWORD = "KEYSTORE_PASSWORD";
    private static final String TRUSTSTORE_PATH = "TRUSTSTORE_PATH";
    private static final String TRUSTSTORE_PASSWORD = "TRUSTSTORE_PASSWORD";

    @Inject
    Vertx vertx;

    @Produces
    public WebClient webClient() {
        boolean sslEnabled = Boolean.parseBoolean(System.getenv(SSL_ENABLED));

        WebClientOptions options = new WebClientOptions()
                .setDefaultHost("localhost")
                .setDefaultPort(8080);

        if (sslEnabled) {
            String keystorePath = System.getenv(KEYSTORE_PATH);
            String keystorePassword = System.getenv(KEYSTORE_PASSWORD);
            String truststorePath = System.getenv(TRUSTSTORE_PATH);
            String truststorePassword = System.getenv(TRUSTSTORE_PASSWORD);

            options.setSsl(true).setTrustAll(false);

            if (keystorePath != null && keystorePassword != null) {
                options.setKeyCertOptions(new JksOptions().setPath(keystorePath).setPassword(keystorePassword));
            }

            if (truststorePath != null && truststorePassword != null) {
                options.setTrustStoreOptions(new JksOptions().setPath(truststorePath).setPassword(truststorePassword));
            }
        } else {
            // If SSL is disabled, make sure trust is not set
            // Accept all certificates in non-SSL mode (be cautious in prod)
            options.setSsl(false).setTrustAll(true);
        }

        return WebClient.create(vertx, options);
    }
}
