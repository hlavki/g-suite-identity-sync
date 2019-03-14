package eu.hlavki.identity.services.push.model;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    @Override
    public LocalDateTime unmarshal(String value) {
        return (XMLDateTimeAdapter.parseDateTime(value));
    }

    @Override
    public String marshal(LocalDateTime value) {
        return (XMLDateTimeAdapter.printDateTime(value));
    }
}
