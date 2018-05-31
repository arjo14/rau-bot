package com.rau.bot.enums;

public enum UserState {
    ARMENIAN_SECTOR("0"),
    DEPARTMENT("1"),
    FACULTY("2"),
    COURSE("3"),
    GROUP("4"),
    PARTITION("5");

    private String value;

    UserState(String value) {
        this.value = value;
    }
}
