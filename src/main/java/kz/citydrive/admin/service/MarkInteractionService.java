package kz.citydrive.admin.service;

import kz.citydrive.admin.domain.MarkComment;
import kz.citydrive.admin.domain.MarkLike;
import kz.citydrive.admin.domain.MarkStatus;
import kz.citydrive.admin.domain.RoadMark;
import kz.citydrive.admin.domain.User;
import kz.citydrive.admin.dto.MarkCommentCreateRequest;
import kz.citydrive.admin.dto.MarkCommentDto;
import kz.citydrive.admin.dto.MarkCommentAdminDto;
import kz.citydrive.admin.dto.MarkCommentPageResponse;
import kz.citydrive.admin.dto.MarkLikeAdminDto;
import kz.citydrive.admin.dto.MarkLikeDto;
import kz.citydrive.admin.dto.MarkLikePageResponse;
import kz.citydrive.admin.dto.MarkLikeResponse;
import kz.citydrive.admin.repository.MarkCommentRepository;
import kz.citydrive.admin.repository.MarkLikeRepository;
import kz.citydrive.admin.repository.RoadMarkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class MarkInteractionService {

    private static final List<MarkStatus> INTERACTIVE_STATUSES =
            List.of(MarkStatus.CONFIRMED, MarkStatus.IN_PROGRESS, MarkStatus.FIXED);

    private final RoadMarkRepository roadMarkRepository;
    private final MarkLikeRepository markLikeRepository;
    private final MarkCommentRepository markCommentRepository;
    private final UserService userService;

    public MarkInteractionService(
            RoadMarkRepository roadMarkRepository,
            MarkLikeRepository markLikeRepository,
            MarkCommentRepository markCommentRepository,
            UserService userService) {
        this.roadMarkRepository = roadMarkRepository;
        this.markLikeRepository = markLikeRepository;
        this.markCommentRepository = markCommentRepository;
        this.userService = userService;
    }

    @Transactional
    public MarkLikeResponse toggleLike(Long markId, Long userId) {
        requireUser(userId);
        RoadMark mark = getMark(markId);
        ensureInteractive(mark);

        boolean liked;
        var existing = markLikeRepository.findByMarkIdAndUserId(markId, userId);
        if (existing.isPresent()) {
            markLikeRepository.delete(existing.get());
            liked = false;
        } else {
            MarkLike like = new MarkLike();
            like.setMarkId(markId);
            like.setUserId(userId);
            like.setCreatedAt(Instant.now());
            markLikeRepository.save(like);
            liked = true;
        }

        int likes = (int) markLikeRepository.countByMarkId(markId);
        mark.setLikes(likes);
        roadMarkRepository.save(mark);

        return new MarkLikeResponse(markId, likes, liked);
    }

    public MarkCommentPageResponse getComments(Long markId, int page, int size) {
        getMark(markId);
        Page<MarkComment> result =
                markCommentRepository.findByMarkIdOrderByCreatedAtAsc(markId, PageRequest.of(page, size));
        List<MarkCommentDto> content = result.getContent().stream()
                .map(comment -> MarkCommentDto.fromEntity(comment, resolveAuthor(comment.getUserId())))
                .toList();
        return new MarkCommentPageResponse(
                content, result.getTotalElements(), result.getTotalPages(), page, size);
    }

    public MarkLikePageResponse getLikes(Long markId, int page, int size) {
        getMark(markId);
        Page<MarkLike> result =
                markLikeRepository.findByMarkIdOrderByCreatedAtDesc(markId, PageRequest.of(page, size));
        List<MarkLikeDto> content = result.getContent().stream()
                .map(like -> MarkLikeDto.fromEntity(like, resolveAuthor(like.getUserId())))
                .toList();
        return new MarkLikePageResponse(
                content, result.getTotalElements(), result.getTotalPages(), page, size);
    }

    public List<MarkCommentAdminDto> findAllCommentsForAdmin(Long markId) {
        getMark(markId);
        return markCommentRepository.findByMarkIdOrderByCreatedAtAsc(markId).stream()
                .map(comment -> MarkCommentAdminDto.fromEntity(comment, resolveAuthor(comment.getUserId())))
                .toList();
    }

    public List<MarkLikeAdminDto> findAllLikesForAdmin(Long markId) {
        getMark(markId);
        return markLikeRepository.findByMarkIdOrderByCreatedAtDesc(markId).stream()
                .map(like -> MarkLikeAdminDto.fromEntity(like, resolveAuthor(like.getUserId())))
                .toList();
    }

    @Transactional
    public MarkCommentDto addComment(Long markId, Long userId, MarkCommentCreateRequest request) {
        requireUser(userId);
        User user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        RoadMark mark = getMark(markId);
        ensureInteractive(mark);

        String text = request.getText() != null ? request.getText().trim() : "";
        if (text.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "text is required");
        }
        if (text.length() > 1000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "text must be at most 1000 characters");
        }

        MarkComment comment = new MarkComment();
        comment.setMarkId(markId);
        comment.setUserId(userId);
        comment.setText(text);
        comment.setCreatedAt(Instant.now());
        markCommentRepository.save(comment);

        int commentsCount = (int) markCommentRepository.countByMarkId(markId);
        mark.setCommentsCount(commentsCount);
        roadMarkRepository.save(mark);

        MarkCommentDto dto = MarkCommentDto.fromEntity(comment, user);
        dto.setCommentsCount(commentsCount);
        return dto;
    }

    public Set<Long> findLikedMarkIds(Long userId) {
        if (userId == null) {
            return Set.of();
        }
        return markLikeRepository.findMarkIdsByUserId(userId);
    }

    public MarkCommentDto findLatestComment(Long markId) {
        return markCommentRepository.findTopByMarkIdOrderByCreatedAtDesc(markId)
                .map(comment -> MarkCommentDto.fromEntity(comment, resolveAuthor(comment.getUserId())))
                .orElse(null);
    }

    @Transactional
    public void deleteByMarkId(Long markId) {
        markLikeRepository.deleteByMarkId(markId);
        markCommentRepository.deleteByMarkId(markId);
    }

    private User resolveAuthor(Long userId) {
        return userService.findById(userId).orElse(null);
    }

    private void requireUser(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    private void ensureInteractive(RoadMark mark) {
        if (!INTERACTIVE_STATUSES.contains(mark.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Likes and comments are only allowed for confirmed marks");
        }
    }

    private RoadMark getMark(Long markId) {
        return roadMarkRepository.findById(markId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mark not found"));
    }
}
