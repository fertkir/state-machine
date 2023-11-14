package com.github.fertkir.statemachine;

import com.github.fertkir.statemachine.exceptions.TransitionCreationException;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class StateMachineConfig<TState, TEvent, TContext> {

    @Getter(value = AccessLevel.MODULE)
    private final Map<TEvent, Map<TState, List<Transition<TState, TEvent, TContext>>>> transitions = new LinkedHashMap<>();

    @Getter(value = AccessLevel.MODULE)
    private final Map<TState, Map<Class<?>, Function<TContext, ?>>> parameterResolvers = new HashMap<>();

    @Getter(value = AccessLevel.MODULE)
    private final Map<TState, Map<Class<?>, Function<TContext, ?>>> collectionParameterResolvers = new HashMap<>();

    @Getter
    private final String id;

    public StateMachineConfig(Class<TContext> contextClass) {
        this(contextClass.getSimpleName());
    }

    public StateMachineConfig(String id) {
        this.id = id;
    }

    public Transition.TransitionBuilder<TState, TEvent, TContext> transition() {
        return new Transition.TransitionBuilder<>(this);
    }

    public <TParameter> Parameters.ParametersBuilder<TState, TEvent, TContext, TParameter> parameters(
            Class<TParameter> parameterType) {
        return new Parameters.ParametersBuilder<>(this, parameterType);
    }

    void apply(Transition<TState, TEvent, TContext> transition) throws TransitionCreationException {
        validateTransition(transition);
        transitions
                .computeIfAbsent(transition.getOn(), k -> new HashMap<>())
                .computeIfAbsent(transition.getFrom(), k -> new ArrayList<>())
                .add(transition);
    }

    <TParameter> void apply(Parameters<TState, TEvent, TContext, TParameter> parameters) throws TransitionCreationException {
        if (parameters.getResolver() != null) {
            this.parameterResolvers
                    .computeIfAbsent(parameters.getState(), k -> new HashMap<>())
                    .put(parameters.getParameterType(), parameters.getResolver());
        }
        if (parameters.getCollectionResolver() != null) {
            this.collectionParameterResolvers
                    .computeIfAbsent(parameters.getState(), k -> new HashMap<>())
                    .put(parameters.getParameterType(), parameters.getCollectionResolver());
        }
    }

    private void validateTransition(Transition<TState, TEvent, TContext> tseTransition) throws TransitionCreationException {
        if (tseTransition.getFrom() == null) {
            throw new TransitionCreationException("From state should be defined");
        }
        if (tseTransition.getOn() == null) {
            throw new TransitionCreationException("On Event should be defined");
        }
        if (tseTransition.getTo() == null) {
            throw new TransitionCreationException("A transition while its creation should either have \"ignore\" or \"To State\"");
        }
    }
}
