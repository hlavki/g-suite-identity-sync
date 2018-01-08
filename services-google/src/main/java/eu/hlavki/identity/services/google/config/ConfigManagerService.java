package eu.hlavki.identity.services.google.config;

import java.util.Dictionary;
import java.util.List;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigManagerService implements ManagedService {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigManagerService.class);
    private final List<Configurable> configurables;


    public ConfigManagerService(List<Configurable> configurables) {
        this.configurables = configurables;
    }


    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        if (properties != null) {
            LOG.info("LDAP Configuration changed");
            configurables.stream().forEach(c -> c.reconfigure());
        }
    }
}
