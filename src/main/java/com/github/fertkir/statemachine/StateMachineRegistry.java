package com.github.fertkir.statemachine;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class StateMachineRegistry {

    private final Map<String, StateMachineConfig<?, ?, ?>> configById;

    public StateMachineRegistry(List<StateMachineConfig<?, ?, ?>> configs) {
        this.configById = configs.stream().collect(toMap(StateMachineConfig::getId, Function.identity()));
    }

    public <TState, TEvent, TContext> StateMachine<TState, TEvent, TContext> get(
            TState initialState, Class<TEvent> eventType, TContext context) {
        return get(initialState, eventType, context, context.getClass().getSimpleName());
    }

    public <TState, TEvent, TContext> StateMachine<TState, TEvent, TContext> get(
            TState initialState, Class<TEvent> eventType, TContext context, String stateMachineId) {
        StateMachineConfig<TState, TEvent, TContext> config =
                (StateMachineConfig<TState, TEvent, TContext>) configById.get(stateMachineId);
        return new StateMachine<>(initialState, context, config);
    }
}
