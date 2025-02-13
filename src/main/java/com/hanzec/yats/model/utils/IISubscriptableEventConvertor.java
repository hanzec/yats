package com.hanzec.yats.model.utils;

import com.hanzec.yats.model.constants.ISubscriptableEvent;
import jakarta.persistence.AttributeConverter;

public class IISubscriptableEventConvertor implements AttributeConverter<ISubscriptableEvent, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ISubscriptableEvent attribute) {
        return switch (attribute) {
            case NODE_CREATED -> 1;
            case NODE_NEEDS_APPROVAL -> 2;
            case NODE_APPROVED -> 3;
            case NODE_KEY_EXPIRING_ONE_DAY -> 4;
            case NODE_KEY_EXPIRED -> 5;
            case NODE_DELETED -> 6;
            case POLICY_UPDATED -> 7;
            case USER_CREATED -> 8;
            case USER_NEEDS_APPROVAL -> 9;
            case USER_SUSPENDED -> 10;
            case USER_DELETED -> 11;
            case USER_APPROVED -> 12;
            case USER_ROLE_UPDATED -> 13;
            case SUBNET_IP_FORWARDING_NOT_ENABLED -> 14;
            case EXIT_NODE_IP_FORWARDING_NOT_ENABLED -> 15;
        };
    }

    @Override
    public ISubscriptableEvent convertToEntityAttribute(Integer dbData) {
        return switch (dbData) {
            case 1 -> ISubscriptableEvent.NODE_CREATED;
            case 2 -> ISubscriptableEvent.NODE_NEEDS_APPROVAL;
            case 3 -> ISubscriptableEvent.NODE_APPROVED;
            case 4 -> ISubscriptableEvent.NODE_KEY_EXPIRING_ONE_DAY;
            case 5 -> ISubscriptableEvent.NODE_KEY_EXPIRED;
            case 6 -> ISubscriptableEvent.NODE_DELETED;
            case 7 -> ISubscriptableEvent.POLICY_UPDATED;
            case 8 -> ISubscriptableEvent.USER_CREATED;
            case 9 -> ISubscriptableEvent.USER_NEEDS_APPROVAL;
            case 10 -> ISubscriptableEvent.USER_SUSPENDED;
            case 11 -> ISubscriptableEvent.USER_DELETED;
            case 12 -> ISubscriptableEvent.USER_APPROVED;
            case 13 -> ISubscriptableEvent.USER_ROLE_UPDATED;
            case 14 -> ISubscriptableEvent.SUBNET_IP_FORWARDING_NOT_ENABLED;
            case 15 -> ISubscriptableEvent.EXIT_NODE_IP_FORWARDING_NOT_ENABLED;
            default -> throw new IllegalStateException("Unexpected value: " + dbData);
        };
    }
}