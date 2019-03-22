package eu.hlavki.identity.services.google.model;

import java.time.Duration;
import java.util.UUID;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@XmlRootElement
public class StartPushChannel {

    private String id;
    private String type = "web_hook";
    private String address;
    private String token;
    private long expiration;
    private Params params;


    public StartPushChannel() {
        id = UUID.randomUUID().toString();
    }


    public StartPushChannel(String address, Duration duration) {
        this();
        this.address = address;
        this.expiration = System.currentTimeMillis() + duration.toMillis();
//        this.params = new Params();
//        this.params.ttl = duration.getSeconds();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Params {

        private long ttl;
    }
}
