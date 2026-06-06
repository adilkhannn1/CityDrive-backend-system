package kz.citydrive.admin.service;

import kz.citydrive.admin.domain.News;
import kz.citydrive.admin.dto.NewsCreateRequest;
import kz.citydrive.admin.dto.NewsDto;
import kz.citydrive.admin.repository.NewsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<NewsDto> findPublished(int limit, int offset) {
        int page = limit > 0 ? offset / limit : 0;
        return newsRepository.findByPublishedTrueOrderByPublishedAtDesc(PageRequest.of(page, limit))
                .stream().map(NewsDto::fromEntity).toList();
    }

    public NewsDto getById(Long id) {
        return newsRepository.findById(id)
                .filter(News::isPublished)
                .map(NewsDto::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));
    }

    public List<NewsDto> findAll() {
        return newsRepository.findAllByOrderByPublishedAtDesc()
                .stream().map(NewsDto::fromEntity).toList();
    }

    public NewsDto create(NewsCreateRequest req) {
        News news = new News();
        applyRequest(news, req);
        return NewsDto.fromEntity(newsRepository.save(news));
    }

    public NewsDto update(Long id, NewsCreateRequest req) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));
        applyRequest(news, req);
        return NewsDto.fromEntity(newsRepository.save(news));
    }

    public void delete(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found");
        }
        newsRepository.deleteById(id);
    }

    public News getEntity(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));
    }

    public long count() {
        return newsRepository.count();
    }

    private void applyRequest(News news, NewsCreateRequest req) {
        news.setTitle(req.getTitle());
        news.setDescription(req.getDescription());
        news.setContent(req.getContent());
        news.setImageUrl(req.getImageUrl());
        news.setPublished(req.isPublished());
        if (req.getPublishedAt() != null && !req.getPublishedAt().isBlank()) {
            try {
                news.setPublishedAt(Instant.parse(req.getPublishedAt()));
            } catch (Exception e) {
                news.setPublishedAt(Instant.now());
            }
        } else if (news.getPublishedAt() == null) {
            news.setPublishedAt(Instant.now());
        }
    }
}
