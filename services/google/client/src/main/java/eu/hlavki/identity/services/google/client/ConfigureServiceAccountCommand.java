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

    @Argument(index = 0, name = "clientEmail", description = "Service account client email", required = true, multiValued = false)
    String clientEmail;

    @Argument(index = 1, name = "privateKey", description = "Base64 encoded private key", required = true, multiValued = false)
    String privateKey;

    @Argument(index = 1, name = "subject", description = "Subject email", required = true, multiValued = false)
    String subject;

    @Argument(index = 1, name = "tokenUri", description = "Uri to obtain security token", required = true, multiValued = false)
    String tokenUri;

    @Reference
    Configuration config;

    @Override
    public Object execute() throws Exception {
        config.setServiceAccount(clientEmail, privateKey, subject, tokenUri);
        return null;
    }
}
