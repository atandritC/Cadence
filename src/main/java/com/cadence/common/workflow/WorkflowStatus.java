package com.cadence.common.workflow;

import java.util.Set;

public interface WorkflowStatus<S extends Enum<S> & WorkflowStatus<S>> {

    Set<S> allowedNext();

    boolean isTerminal();

    boolean isBlockedState();

}
