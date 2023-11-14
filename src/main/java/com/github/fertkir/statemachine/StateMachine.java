package com.github.fertkir.statemachine;

import com.github.fertkir.statemachine.exceptions.NoValidTransitionException;
import com.github.fertkir.statemachine.exceptions.TransitionException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
public class StateMachine<TState, TEvent, TContext> {

    @Getter
    private TState currentState;
    private final TContext context;
    private final StateMachineConfig<TState, TEvent, TContext> config;

    StateMachine(TState initialState, TContext context, StateMachineConfig<TState, TEvent, TContext> config) {
        currentState = initialState;
        this.context = context;
        this.config = config;
    }

    public boolean fire(TEvent event) throws TransitionException {
        if (currentState == null) {
            throw new IllegalStateException("Current state cannot be null");
        }
        if (config.getTransitions().isEmpty()) {
            throw new IllegalStateException("No transitions defined for state machine");
        }
        if (config.getTransitions().get(event) == null) {
            throw new TransitionException("No transitions defined for Event %s".formatted(event));
        }
        if (config.getTransitions().get(event).get(currentState) == null) {
            throw new TransitionException("No transitions defined for current state %s and event %s"
                    .formatted(currentState, event));
        }

        // todo add tests for multiple transitions
        for (Transition<TState, TEvent, TContext> transition : config.getTransitions().get(event).get(currentState)) {

            if (transition.getCondition() != null && !transition.getCondition().test(context)) {
                if (log.isDebugEnabled()) {
                    log.debug("Condition was not satisfied: %s".formatted(transition));
                }
                continue;
            }

            if (log.isDebugEnabled()) {
                log.debug("Found valid transition: %s".formatted(transition));
            }
            currentState = transition.getTo();

            return currentState != transition.getFrom();
        }
        // todo should this exception be thrown?
        throw new NoValidTransitionException("Valid transition not found for current state %s and event %s"
                .formatted(currentState, event));
    }

    public <TParameter> TParameter getParameter(Class<TParameter> parameterType) {
        return (TParameter) config.getParameterResolvers()
                .get(currentState)
                .get(parameterType)
                .apply(context);
    }

    public <TParameter> Collection<TParameter> getParameterCollection(Class<TParameter> parameterType) {
        return (Collection<TParameter>) config.getCollectionParameterResolvers()
                .get(currentState)
                .get(parameterType)
                .apply(context);
    }
}
