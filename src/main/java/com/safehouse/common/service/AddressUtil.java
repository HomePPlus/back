package com.safehouse.common.service;

public class AddressUtil {
    public static String extractDistrict(String address) {
        if (address == null || address.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
        // "부산광역시 동래구 충렬대로" -> "동래구"
        String[] parts = address.split(" ");
        for (String part : parts) {
            if (part.endsWith("구")) {
                return part;
            }
        }
        throw new IllegalArgumentException("District not found in the address");
    }
}
