package shop.samgak.mini_board.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;

public class LocalDateTimeToEpochSecondsSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen,
            com.fasterxml.jackson.databind.SerializerProvider serializers) throws IOException {
        if (value != null) {
            long epochSeconds = value.toEpochSecond(ZoneOffset.UTC);
            gen.writeNumber(epochSeconds);
        } else {
            gen.writeNull();
        }
    }
}
