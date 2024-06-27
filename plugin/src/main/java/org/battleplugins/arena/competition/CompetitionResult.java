package org.battleplugins.arena.competition;

import org.jetbrains.annotations.Nullable;

public record CompetitionResult(@Nullable Competition<?> competition, JoinResult result) {
}
