package com.cadence.task;

import com.cadence.common.DueDates;
import com.cadence.task.dto.TaskResponse;

public final class TaskMapper {

    private TaskMapper() {
    }

    public static TaskResponse toResponse(Task t) {
        Long assigneeId = (t.getAssignee() != null) ? t.getAssignee().getId() : null;
        boolean terminal = t.getStatus().isTerminal();
        return new TaskResponse(
                t.getId(), t.getTitle(), t.getDescription(), t.getStatus(),
                t.getStoryPoints(), t.getDueDate(),
                DueDates.remainingDays(t.getDueDate()),
                DueDates.isOverdue(t.getDueDate(), terminal),
                t.getProject().getId(), assigneeId, t.getCreatedAt());
    }

}
