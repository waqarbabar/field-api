package com.task.tech.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.task.tech.dtos.PropertyDTO;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class PropertyDTOSerializer extends StdSerializer<PropertyDTO> {

    public PropertyDTOSerializer() {
        this(null);
    }

    public PropertyDTOSerializer(Class<PropertyDTO> t) {
        super(t);
    }

    @Override
    public void serialize(PropertyDTO value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        // to maintain AF compatible format it is required to write {} instead of null
        gen.writeStartObject();
        gen.writeEndObject();
    }
}
