package kafdrop.config;

import java.io.*;
import java.util.*;
import lombok.*;
import org.apache.kafka.clients.*;
import org.apache.kafka.common.config.*;
import org.slf4j.*;
import org.springframework.boot.context.properties.*;
import org.springframework.stereotype.*;


@Component
@ConfigurationProperties(prefix = "kafka")
@Data
public final class KafkaConfiguration {
  private static final Logger LOG = LoggerFactory.getLogger(KafkaConfiguration.class);

  private String brokerConnect;
  private Boolean isSecured = false;
  private String saslMechanism;
  private String securityProtocol;
  private String truststoreFile;
  private String propertiesFile;
  private String keystoreFile;
  private String jaasConfig;
  private String clientCallback;
  private String iamEnabled;

  public void applyCommon(Properties properties) {
    properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, brokerConnect);

    if (isSecured) {
      LOG.warn("The 'isSecured' property is deprecated; consult README.md on the preferred way to configure security");
      LOG.info("Setting security protocol to {}", securityProtocol);
      LOG.info("Setting sasl mechanism to {}", saslMechanism);
      properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
      properties.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
    }

    LOG.info("Checking truststore file {}", truststoreFile);
    if (new File(truststoreFile).isFile()) {
      LOG.info("Assigning truststore location to {}", truststoreFile);
      properties.put("ssl.truststore.location", truststoreFile);
    }
    LOG.info("Is iam enabled : {}", iamEnabled);
    if (Boolean.parseBoolean(iamEnabled)) {
      LOG.info("Setting sasl.jaas.config {} and sasl and callback callback properties {}", jaasConfig, clientCallback);
      properties.put(SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS, clientCallback);
      properties.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);
    }

    LOG.info("Checking keystore file {}", keystoreFile);
    if (new File(keystoreFile).isFile()) {
      LOG.info("Assigning keystore location to {}", keystoreFile);
      properties.put("ssl.keystore.location", keystoreFile);
    }

    LOG.info("Checking properties file {}", propertiesFile);
    final var propertiesFile = new File(this.propertiesFile);
    if (propertiesFile.isFile()) {
      LOG.info("Loading properties from {}", this.propertiesFile);
      final var propertyOverrides = new Properties();
      try (var propsReader = new BufferedReader(new FileReader(propertiesFile))) {
        propertyOverrides.load(propsReader);
      } catch (IOException e) {
        throw new KafkaConfigurationException(e);
      }
      properties.putAll(propertyOverrides);
    }
  }
}