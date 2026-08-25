package com.foxminded.schoolmanagementapp.console.constants;

import lombok.Getter;

@Getter
public enum ConfirmationOption {
    CANCEL("N"),
    DELETE("Y");

    private final String value;

    ConfirmationOption(String value) {
        this.value = value;
    }
}
