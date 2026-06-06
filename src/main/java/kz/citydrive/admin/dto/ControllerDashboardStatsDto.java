package kz.citydrive.admin.dto;

public class ControllerDashboardStatsDto {

    private long newCount;
    private long applicationsCount;
    private long inWorkCount;
    private long doneCount;

    public ControllerDashboardStatsDto(long newCount, long applicationsCount, long inWorkCount, long doneCount) {
        this.newCount = newCount;
        this.applicationsCount = applicationsCount;
        this.inWorkCount = inWorkCount;
        this.doneCount = doneCount;
    }

    public long getNewCount() {
        return newCount;
    }

    public void setNewCount(long newCount) {
        this.newCount = newCount;
    }

    public long getApplicationsCount() {
        return applicationsCount;
    }

    public void setApplicationsCount(long applicationsCount) {
        this.applicationsCount = applicationsCount;
    }

    public long getInWorkCount() {
        return inWorkCount;
    }

    public void setInWorkCount(long inWorkCount) {
        this.inWorkCount = inWorkCount;
    }

    public long getDoneCount() {
        return doneCount;
    }

    public void setDoneCount(long doneCount) {
        this.doneCount = doneCount;
    }
}
