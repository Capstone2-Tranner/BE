package com.tranner.account_service.util;

import com.tranner.account_service.type.RegionCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class RegionCodeConverter implements AttributeConverter<RegionCode, Integer> {

    @Override
    public Integer convertToDatabaseColumn(RegionCode attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public RegionCode convertToEntityAttribute(Integer dbData) {
        return dbData != null ? RegionCode.fromCode(dbData) : null;
    }
}

