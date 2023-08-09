package net.ddns.encante.telegram.hr.events;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class EventTypeConverter implements AttributeConverter<EventType, String> {

    @Override
    public String convertToDatabaseColumn(EventType eventType) {
        return eventType==null ? null : eventType.getCode();
    }

    @Override
    public EventType convertToEntityAttribute(String s) {
        return s==null ? null : Stream.of(EventType.values())
                .filter(eT -> eT.getCode().equals(s))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
