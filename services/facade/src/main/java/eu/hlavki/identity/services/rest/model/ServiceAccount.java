package eu.hlavki.identity.services.rest.model;

import eu.hlavki.identity.services.rest.validator.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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

    @NotNull
    @Pattern(regexp = "service_account")
    @XmlElement(name = "type")
    private String type;
    @NotNull
    @XmlElement(name = "project_id")
    private String projectId;
    @XmlElement(name = "private_key_id")
    @NotNull
    private String privateKeyId;
    @XmlElement(name = "private_key")
    @NotNull
    private String privateKey;
    @NotNull
    @Email
    @XmlElement(name = "client_email")
    private String clientEmail;
    @NotNull
    @XmlElement(name = "client_id")
    private String clientId;
    @NotNull
    @XmlElement(name = "auth_uri")
    private String authUri;
    @NotNull
    @XmlElement(name = "token_uri")
    private String tokenUri;
    @XmlElement(name = "auth_provider_x509_cert_url")
    private String authProviderUrl;
    @XmlElement(name = "client_x509_cert_url")
    private String clientCertUrl;
}
