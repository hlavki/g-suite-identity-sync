package eu.hlavki.identity.services.push.config.impl;

import java.time.ZonedDateTime;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ZonedDateTimeAdapter extends XmlAdapter<String, ZonedDateTime> {

    @Override
    public ZonedDateTime unmarshal(String value) {
        return (XMLDateTimeAdapter.parseDateTime(value));
    }

    @Override
    public String marshal(ZonedDateTime value) {
        return (XMLDateTimeAdapter.printDateTime(value));
    }
}
