package kz.citydrive.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ControllerDashboardStatsDto {

    private long newCount;
    private long applicationsCount;
    private long inWorkCount;
    private long doneCount;

    @JsonProperty("pending_review_count")
    private long pendingReviewCount;

    public ControllerDashboardStatsDto(
            long newCount, long applicationsCount, long inWorkCount, long doneCount, long pendingReviewCount) {
        this.newCount = newCount;
        this.applicationsCount = applicationsCount;
        this.inWorkCount = inWorkCount;
        this.doneCount = doneCount;
        this.pendingReviewCount = pendingReviewCount;
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

    public long getPendingReviewCount() {
        return pendingReviewCount;
    }

    public void setPendingReviewCount(long pendingReviewCount) {
        this.pendingReviewCount = pendingReviewCount;
    }
}
