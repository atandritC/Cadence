package com.cadence.project;

import java.util.EnumSet;
import java.util.Set;

import com.cadence.common.workflow.WorkflowStatus;

public enum ProjectStatus implements WorkflowStatus<ProjectStatus> {
    PLANNED, IN_PROGRESS, ON_HOLD, COMPLETED, CANCELLED;

    @Override
    public Set<ProjectStatus> allowedNext() {
        return switch (this) {
            case PLANNED -> EnumSet.of(IN_PROGRESS, CANCELLED);
            case IN_PROGRESS -> EnumSet.of(COMPLETED, ON_HOLD, CANCELLED);
            case ON_HOLD -> EnumSet.of(CANCELLED); // + resume
            case COMPLETED, CANCELLED -> EnumSet.noneOf(ProjectStatus.class);
        };
    }

    @Override
    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }

    @Override
    public boolean isBlockedState() {
        return this == ON_HOLD;
    }
}
