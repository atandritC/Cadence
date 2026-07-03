package com.cadence.project;

import java.util.Set;
import java.util.stream.Collectors;

import com.cadence.common.DueDates;
import com.cadence.project.dto.ProjectResponse;
import com.cadence.user.User;

public final class ProjectMapper {

    private ProjectMapper() {
    }

    public static ProjectResponse toResponse(Project p) {
        Set<Long> memberIds = p.getMembers().stream().map(User::getId).collect(Collectors.toSet());

        int totalPoints = p.getTasks().stream()
                .map(t -> t.getStoryPoints() == null ? 0 : t.getStoryPoints())
                .reduce(0, Integer::sum);

        boolean terminal = p.getStatus().isTerminal();
        return new ProjectResponse(
                p.getId(), p.getName(), p.getDescription(),
                p.getStatus(), p.getDueDate(),
                totalPoints,
                DueDates.remainingDays(p.getDueDate()),
                DueDates.isOverdue(p.getDueDate(), terminal),
                p.getManager().getId(), memberIds, p.getCreatedAt());
    }

}
