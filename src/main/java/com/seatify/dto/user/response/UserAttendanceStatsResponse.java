package com.seatify.dto.user.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAttendanceStatsResponse {
    private long totalParticipated;
    private long presentCount;
    private long absentCount;
}


