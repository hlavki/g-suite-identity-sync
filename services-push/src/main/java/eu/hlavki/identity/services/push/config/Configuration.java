package eu.hlavki.identity.services.push.config;

public interface Configuration {

    public static final String TOPIC_CHANGE = "eu/hlavki/identity/push/Configuration/CHANGED";

    boolean isEnabled();
}
