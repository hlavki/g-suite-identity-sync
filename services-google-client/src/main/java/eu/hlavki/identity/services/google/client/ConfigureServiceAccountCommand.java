package eu.hlavki.identity.services.google.client;

import eu.hlavki.identity.services.google.config.Configuration;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Service
@Command(scope = "google", name = "configure-service-account", description = "Configure service account")
public class ConfigureServiceAccountCommand implements Action {

    @Argument(index = 0, name = "keyFileLocation", description = "Private key file location", required = true, multiValued = false)
    String keyFileLocation;

    @Argument(index = 1, name = "passphrase", description = "passphrase", required = true, multiValued = false)
    String passphrase;

    @Reference
    Configuration config;

    @Override
    public Object execute() throws Exception {
        config.setServiceAccountKey(keyFileLocation, passphrase);
        return null;
    }
}
