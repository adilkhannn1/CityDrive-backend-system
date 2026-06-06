package kz.citydrive.admin.dto;

import java.util.List;

public class ControllerDashboardDto {

    private ControllerDashboardStatsDto stats;
    private List<RoadMarkDto> pendingMarks;
    private List<RoadMarkDto> myMarks;

    public ControllerDashboardDto(
            ControllerDashboardStatsDto stats,
            List<RoadMarkDto> pendingMarks,
            List<RoadMarkDto> myMarks) {
        this.stats = stats;
        this.pendingMarks = pendingMarks;
        this.myMarks = myMarks;
    }

    public ControllerDashboardStatsDto getStats() {
        return stats;
    }

    public void setStats(ControllerDashboardStatsDto stats) {
        this.stats = stats;
    }

    public List<RoadMarkDto> getPendingMarks() {
        return pendingMarks;
    }

    public void setPendingMarks(List<RoadMarkDto> pendingMarks) {
        this.pendingMarks = pendingMarks;
    }

    public List<RoadMarkDto> getMyMarks() {
        return myMarks;
    }

    public void setMyMarks(List<RoadMarkDto> myMarks) {
        this.myMarks = myMarks;
    }
}
