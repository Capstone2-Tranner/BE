package com.tranner.external_api_proxy.common.util;

import com.tranner.external_api_proxy.common.type.CountryCode;
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

