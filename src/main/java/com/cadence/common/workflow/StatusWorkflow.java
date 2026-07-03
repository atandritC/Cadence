
package com.cadence.common.workflow;

import com.cadence.common.exception.InvalidStatusTransitionException;

public final class StatusWorkflow {
    private StatusWorkflow() {
    }

    /**
     * Validates a status change. Works for ANY WorkflowStatus enum (Task, Project,
     * ...).
     * 
     * @param resumeState the state to return to when leaving a blocked state (may
     *                    be null)
     */
    public static <S extends Enum<S> & WorkflowStatus<S>> void validate(S current, S target, S resumeState) {
        if (current == target) {
            throw new InvalidStatusTransitionException("Status is already " + current);
        }
        if (current.isTerminal()) {
            throw new InvalidStatusTransitionException(current + " is a final status and cannot be changed");
        }

        boolean allowed = current.allowedNext().contains(target);

        // Special rule: from a blocked/on-hold state you may resume to where you came
        // from.
        if (current.isBlockedState() && resumeState != null && target == resumeState) {
            allowed = true;
        }
        if (!allowed) {
            throw new InvalidStatusTransitionException("Cannot move from " + current + " to " + target);
        }
    }
}
