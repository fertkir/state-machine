package com.github.fertkir.statemachine.test.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MyEvent {

    CREATE("create"), HOLD("hold"), DELIVER("deliver"), CANCEL("cancel"), DO_NOTHING("do nothing");

    private final String value;

}
