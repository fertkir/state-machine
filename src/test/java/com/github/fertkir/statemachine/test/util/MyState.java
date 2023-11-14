package com.github.fertkir.statemachine.test.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MyState {

    CREATED("created"), ONHOLD("onhold"), DELIVERED("delivered"), CANCELLED("cancelled");

    private final String value;
}
