package eu.hlavki.identity.services.google.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceAccount {

    @XmlElement(name = "type")
    private String type;
    @XmlElement(name = "project_id")
    private String projectId;
    @XmlElement(name = "private_key_id")
    private String privateKeyId;
    @XmlElement(name = "private_key")
    private String privateKey;
    @XmlElement(name = "client_email")
    private String clientEmail;
    @XmlElement(name = "client_id")
    private String clientId;
    @XmlElement(name = "auth_uri")
    private String authUri;
    @XmlElement(name = "token_uri")
    private String tokenUri;
    @XmlElement(name = "auth_provider_x509_cert_url")
    private String authProviderUrl;
    @XmlElement(name = "client_x509_cert_url")
    private String clientCertUrl;
}
