package com.github.fertkir.statemachine.test;

import com.github.fertkir.statemachine.StateMachine;
import com.github.fertkir.statemachine.StateMachineConfig;
import com.github.fertkir.statemachine.StateMachineRegistry;
import com.github.fertkir.statemachine.exceptions.NoValidTransitionException;
import com.github.fertkir.statemachine.exceptions.TransitionException;
import com.github.fertkir.statemachine.test.util.MyContext;
import com.github.fertkir.statemachine.test.util.MyEvent;
import com.github.fertkir.statemachine.test.util.MyParameter;
import com.github.fertkir.statemachine.test.util.MyState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StateMachineTest {

    private StateMachineConfig<MyState, MyEvent, MyContext> config;
    private StateMachineRegistry registry;

    @BeforeEach
    public void init() {
        config = new StateMachineConfig<>(MyContext.class);
        registry = new StateMachineRegistry(List.of(config));
    }

    @Test
    public void testStateNotChanged() {
        config.transition().from(MyState.CREATED).to(MyState.CREATED).on(MyEvent.DO_NOTHING).create();

        boolean stateChanged = registry.get(MyState.CREATED, MyEvent.class, new MyContext())
                .fire(MyEvent.DO_NOTHING);
        assertFalse(stateChanged);
    }

    @Test
    public void testTransitionExceptionCase() {

        config.transition().from(MyState.CREATED).to(MyState.ONHOLD).on(MyEvent.HOLD).create();
        config.transition().from(MyState.ONHOLD).to(MyState.DELIVERED).on(MyEvent.DELIVER).create();


        StateMachine<MyState, MyEvent, MyContext> stateMachine = registry.get(MyState.CREATED, MyEvent.class, new MyContext());
        try {
            stateMachine.fire(MyEvent.DELIVER);
        } catch (TransitionException e) {
            assertFalse(e instanceof NoValidTransitionException);
            assertEquals(stateMachine.getCurrentState(), MyState.CREATED);
        }
    }

    @Test
    public void testSingleConditionalTransitionSuccess() {
        final int a = 2;
        final int b = 2;
        config.transition().from(MyState.CREATED).to(MyState.ONHOLD).on(MyEvent.HOLD)
                .when(context -> a == b).create();
        config.transition().from(MyState.ONHOLD).to(MyState.DELIVERED).on(MyEvent.DELIVER).create();

        StateMachine<MyState, MyEvent, MyContext> stateMachine = registry.get(MyState.CREATED, MyEvent.class, new MyContext());
        boolean stateChanged = stateMachine.fire(MyEvent.HOLD);
        assertTrue(stateChanged);
        assertEquals(stateMachine.getCurrentState(), MyState.ONHOLD);
    }

    @Test
    public void testSingleConditionNotMetTransition() {
        final int a = 2;
        final int b = 3;
        config.transition().from(MyState.CREATED).to(MyState.ONHOLD).on(MyEvent.HOLD)
                .when(context -> a == b).create();
        config.transition().from(MyState.ONHOLD).to(MyState.DELIVERED).on(MyEvent.DELIVER).create();

        StateMachine<MyState, MyEvent, MyContext> stateMachine = registry.get(MyState.CREATED, MyEvent.class, new MyContext());
        try {
            stateMachine.fire(MyEvent.HOLD);
        } catch (TransitionException e) {
            assertTrue(e instanceof NoValidTransitionException);
        }
        assertEquals(stateMachine.getCurrentState(), MyState.CREATED);
    }

    @Test
    void testParameters() {
        config.parameters(MyParameter.class).state(MyState.CREATED)
                .resolver(context -> "default".equals(context.getValue())
                        ? MyParameter.SMALL
                        : MyParameter.BIG)
                .create();

        MyParameter parameter1 = registry.get(MyState.CREATED, MyEvent.class, new MyContext())
                .getParameter(MyParameter.class);
        assertEquals(MyParameter.SMALL, parameter1);

        MyParameter parameter2 = registry.get(MyState.CREATED, MyEvent.class, new MyContext("no-default"))
                .getParameter(MyParameter.class);
        assertEquals(MyParameter.BIG, parameter2);
    }

    @Test
    void testCollectionParameters() {
        config.parameters(MyParameter.class).state(MyState.CREATED)
                .collectionResolver(context -> "default".equals(context.getValue())
                        ? List.of(MyParameter.SMALL, MyParameter.FRAGILE)
                        : List.of(MyParameter.BIG))
                .create();

        Collection<MyParameter> parameters1 = registry.get(MyState.CREATED, MyEvent.class, new MyContext())
                .getParameterCollection(MyParameter.class);
        assertEquals(List.of(MyParameter.SMALL, MyParameter.FRAGILE), parameters1);

        Collection<MyParameter> parameters2 = registry.get(MyState.CREATED, MyEvent.class, new MyContext("no-default"))
                .getParameterCollection(MyParameter.class);
        assertEquals(List.of(MyParameter.BIG), parameters2);
    }
}
