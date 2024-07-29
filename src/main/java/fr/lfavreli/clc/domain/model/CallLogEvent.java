package fr.lfavreli.clc.domain.model;

import java.util.UUID;

public record CallLogEvent(UUID callLogId, CallLogStatus callLogStatus) {

    // Canonical constructor to ensure non-null status
    public CallLogEvent(UUID callLogId, CallLogStatus callLogStatus) {
        this.callLogId = callLogId;
        this.callLogStatus = callLogStatus != null ? callLogStatus : CallLogStatus.IN_PROGRESS;
    }

    // Compact constructor to set default value for status
    public CallLogEvent(UUID callLogId) {
        this(callLogId, CallLogStatus.IN_PROGRESS);
    }

}
