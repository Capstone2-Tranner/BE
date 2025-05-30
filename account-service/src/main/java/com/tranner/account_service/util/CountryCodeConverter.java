package com.tranner.account_service.util;

import com.tranner.account_service.type.CountryCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CountryCodeConverter implements AttributeConverter<CountryCode, Integer> {

    @Override
    public Integer convertToDatabaseColumn(CountryCode attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public CountryCode convertToEntityAttribute(Integer dbData) {
        return dbData != null ? CountryCode.fromCode(dbData) : null;
    }
}

