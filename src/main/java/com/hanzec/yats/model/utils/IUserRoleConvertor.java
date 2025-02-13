package com.hanzec.yats.model.utils;

import com.hanzec.yats.model.constants.IUserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class IUserRoleConvertor implements AttributeConverter<IUserRole, Integer> {
    @Override
    public Integer convertToDatabaseColumn(IUserRole attribute) {
        return switch (attribute) {
            case OWNER -> 1;
            case MEMBER -> 2;
            case ADMIN -> 3;
            case IT_ADMIN -> 4;
            case NETWORK_ADMIN -> 5;
            case BILLING_ADMIN -> 6;
            case AUDITOR -> 7;
        };
    }


    @Override
    public IUserRole convertToEntityAttribute(Integer dbData) {
        return switch (dbData) {
            case 1 -> IUserRole.OWNER;
            case 2 -> IUserRole.MEMBER;
            case 3 -> IUserRole.ADMIN;
            case 4 -> IUserRole.IT_ADMIN;
            case 5 -> IUserRole.NETWORK_ADMIN;
            case 6 -> IUserRole.BILLING_ADMIN;
            case 7 -> IUserRole.AUDITOR;
            default -> throw new IllegalStateException("Unexpected value: " + dbData);
        };
    }
}