package com.emna.micro_service4.model.enums;

public enum Operation {
        somme,
        soustraction,
        multiplication,
        division,
        power,
        sqrt;

        public static Operation fromString(String operation) {
            try {
                return Operation.valueOf(operation.toLowerCase());
            } catch (Exception e) {
                return null; // Handle invalid operations gracefully
            }
        }
    }



