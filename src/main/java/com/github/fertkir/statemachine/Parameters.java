package com.github.fertkir.statemachine;

import com.github.fertkir.statemachine.exceptions.TransitionCreationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.function.Function;

@ToString
@Getter(value = AccessLevel.PACKAGE)
public class Parameters<TState, TEvent, TContext, TParameter> {

    private final TState state;
    private final Class<TParameter> parameterType;
    private final Function<TContext, TParameter> resolver;
    private final Function<TContext, Collection<TParameter>> collectionResolver;

    Parameters(ParametersBuilder<TState, TEvent, TContext, TParameter> builder) {
        this.state = builder.state;
        this.parameterType = builder.parameterType;
        this.resolver = builder.resolver;
        this.collectionResolver = builder.collectionResolver;
    }

    public static class ParametersBuilder<TState, TEvent, TContext, TParameter> {

        private final WeakReference<StateMachineConfig<TState, TEvent, TContext>> configWeakReference;
        private TState state;
        private final Class<TParameter> parameterType;
        private Function<TContext, TParameter> resolver;
        private Function<TContext, Collection<TParameter>> collectionResolver;

        ParametersBuilder(StateMachineConfig<TState, TEvent, TContext> stateMachineConfig,
                          Class<TParameter> parameterType) {
            configWeakReference = new WeakReference<>(stateMachineConfig);
            this.parameterType = parameterType;
        }

        public ParametersBuilder<TState, TEvent, TContext, TParameter> state(TState state) {
            this.state = state;
            return this;
        }

        public ParametersBuilder<TState, TEvent, TContext, TParameter> resolver(
                Function<TContext, TParameter> resolver) {
            this.resolver = resolver;
            return this;
        }

        public ParametersBuilder<TState, TEvent, TContext, TParameter> collectionResolver(
                Function<TContext, Collection<TParameter>> collectionResolver) {
            this.collectionResolver = collectionResolver;
            return this;
        }

        public StateMachineConfig<TState, TEvent, TContext> create() throws TransitionCreationException {
            final StateMachineConfig<TState, TEvent, TContext> config = configWeakReference.get();
            if (config != null) {
                config.apply(new Parameters<>(this));
                return config;
            }
            return null;
        }
    }
}
