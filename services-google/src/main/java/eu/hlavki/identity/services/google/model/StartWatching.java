package eu.hlavki.identity.services.google.model;

import java.util.UUID;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@XmlRootElement
public class StartWatching {

    private String id;
    private String type = "web_hook";
    private String address;
    private String token;
    private Params params;

    public StartWatching() {
        id = UUID.randomUUID().toString();
    }

    public StartWatching(String address) {
        this();
        this.address = address;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Params {

        private int ttl;
    }
}
