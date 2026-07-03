package com.cadence.task;

import java.util.EnumSet;
import java.util.Set;

import com.cadence.common.workflow.WorkflowStatus;

public enum TaskStatus implements WorkflowStatus<TaskStatus> {
    TODO, IN_PROGRESS, BLOCKED, READY_FOR_TESTING, DONE, CANCELLED;

    @Override
    public Set<TaskStatus> allowedNext() {
        return switch (this) {
            case TODO -> EnumSet.of(IN_PROGRESS, CANCELLED);
            case IN_PROGRESS -> EnumSet.of(READY_FOR_TESTING, BLOCKED, CANCELLED);
            case READY_FOR_TESTING -> EnumSet.of(DONE, BLOCKED, CANCELLED);
            case BLOCKED -> EnumSet.of(CANCELLED); // + resume handled by validator
            case DONE, CANCELLED -> EnumSet.noneOf(TaskStatus.class);
        };
    }

    @Override
    public boolean isTerminal() {
        return this == DONE || this == CANCELLED;
    }

    @Override
    public boolean isBlockedState() {
        return this == BLOCKED;
    }
}
