package kz.citydrive.admin.dto;

public class MarkLikeResponse {

    private Long markId;
    private int likes;
    private boolean likedByMe;

    public MarkLikeResponse(Long markId, int likes, boolean likedByMe) {
        this.markId = markId;
        this.likes = likes;
        this.likedByMe = likedByMe;
    }

    public Long getMarkId() {
        return markId;
    }

    public void setMarkId(Long markId) {
        this.markId = markId;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isLikedByMe() {
        return likedByMe;
    }

    public void setLikedByMe(boolean likedByMe) {
        this.likedByMe = likedByMe;
    }
}
