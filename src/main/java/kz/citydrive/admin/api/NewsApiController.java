package kz.citydrive.admin.api;

import kz.citydrive.admin.dto.NewsDto;
import kz.citydrive.admin.service.NewsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsApiController {

    private final NewsService newsService;

    public NewsApiController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public List<NewsDto> list(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return newsService.findPublished(limit, offset);
    }

    @GetMapping("/{id}")
    public NewsDto get(@PathVariable Long id) {
        return newsService.getById(id);
    }
}
