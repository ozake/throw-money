package com.kakaopay.throwmoney.dto.type.converter;

import com.kakaopay.throwmoney.dto.type.common.YesNoType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class YesNoTypeConverter implements AttributeConverter<YesNoType, String> {
    @Override
    public String convertToDatabaseColumn(YesNoType attribute) {
        return attribute.getValue();
    }

    @Override
    public YesNoType convertToEntityAttribute(String dbData) {
        return YesNoType.of(dbData);
    }
}
