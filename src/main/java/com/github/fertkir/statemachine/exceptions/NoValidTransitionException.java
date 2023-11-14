package com.github.fertkir.statemachine.exceptions;

public class NoValidTransitionException extends TransitionException {
    public NoValidTransitionException(String message) {
        super(message);
    }
}
