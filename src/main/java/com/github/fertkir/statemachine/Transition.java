package com.github.fertkir.statemachine;

import com.github.fertkir.statemachine.exceptions.TransitionCreationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import java.lang.ref.WeakReference;
import java.util.function.Predicate;

@ToString
@Getter(value = AccessLevel.PACKAGE)
public class Transition<TState, TEvent, TContext> {

    private final TState from;
    private final TState to;
    private final TEvent on;
    private final Predicate<TContext> condition;

    Transition(TransitionBuilder<TState, TEvent, TContext> builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.on = builder.on;
        this.condition =  builder.condition;
    }

    public static class TransitionBuilder<TState, TEvent, TContext> {

        private final WeakReference<StateMachineConfig<TState, TEvent, TContext>> configWeakReference;
        private TState from;
        private TState to;
        private TEvent on;
        private Predicate<TContext> condition;

        TransitionBuilder(StateMachineConfig<TState, TEvent, TContext> stateMachineConfig) {
            configWeakReference = new WeakReference<>(stateMachineConfig);
        }

        public TransitionBuilder<TState, TEvent, TContext> from(TState fromState) {
            from = fromState;
            return this;
        }

        public TransitionBuilder<TState, TEvent, TContext> to(TState toState) {
            to = toState;
            return this;
        }

        public TransitionBuilder<TState, TEvent, TContext> on(TEvent onEvent) {
            on = onEvent;
            return this;
        }

        public TransitionBuilder<TState, TEvent, TContext> when(Predicate<TContext> condition) {
            this.condition = condition;
            return this;
        }

        public StateMachineConfig<TState, TEvent, TContext> create() throws TransitionCreationException {
            final StateMachineConfig<TState, TEvent, TContext> config = configWeakReference.get();
            if (config != null) {
                config.apply(new Transition<>(this));
                return config;
            }
            return null;
        }
    }
}
