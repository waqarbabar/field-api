package com.task.tech.dtos;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.task.tech.serializers.PropertyDTOSerializer;
import lombok.Data;

@Data
@JsonSerialize(using = PropertyDTOSerializer.class)
public class PropertyDTO {
}
