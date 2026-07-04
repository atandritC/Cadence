package com.cadence.common.workflow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.cadence.common.exception.InvalidStatusTransitionException;
import com.cadence.task.TaskStatus;

class StatusWorkflowTest {

    @Test
    void allowsValidForwardTransition() {
        // TODO -> IN_PROGRESS - allowed
        assertDoesNotThrow(() -> StatusWorkflow.validate(TaskStatus.TODO, TaskStatus.IN_PROGRESS, null));
    }

    @Test
    void rejectsSkippingStates() {
        // IN_PROGRESS -> DONE - not allowed
        assertThrows(InvalidStatusTransitionException.class,
                () -> StatusWorkflow.validate(TaskStatus.IN_PROGRESS, TaskStatus.DONE, null));
    }

    @Test
    void rejectsChangingAFinalState() {
        // DONE -> ANY_STATE - not allowed
        assertThrows(InvalidStatusTransitionException.class,
                () -> StatusWorkflow.validate(TaskStatus.DONE, TaskStatus.TODO, null));
    }

    @Test
    void blockedCanOnlyResumeToPreviousState() {
        // IN_PROGRESS -> BLOCKED -> IN_PROGRESS - allowed
        assertDoesNotThrow(
                () -> StatusWorkflow.validate(TaskStatus.BLOCKED, TaskStatus.IN_PROGRESS, TaskStatus.IN_PROGRESS));

        // IN_PROGRESS -> BLOCKED -> NOT_IN_PROGRESS - not allowed
        assertThrows(InvalidStatusTransitionException.class,
                () -> StatusWorkflow.validate(TaskStatus.BLOCKED, TaskStatus.READY_FOR_TESTING,
                        TaskStatus.IN_PROGRESS));
    }

}
