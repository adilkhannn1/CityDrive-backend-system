package kz.citydrive.admin.api;

import kz.citydrive.admin.dto.NewsCreateRequest;
import kz.citydrive.admin.dto.NewsDto;
import kz.citydrive.admin.service.NewsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/news")
public class AdminNewsApiController {

    private final NewsService newsService;

    public AdminNewsApiController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public List<NewsDto> list() {
        return newsService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewsDto create(@RequestBody NewsCreateRequest request) {
        return newsService.create(request);
    }

    @PutMapping("/{id}")
    public NewsDto update(@PathVariable Long id, @RequestBody NewsCreateRequest request) {
        return newsService.update(id, request);
    }

    @PatchMapping("/{id}")
    public NewsDto patch(@PathVariable Long id, @RequestBody NewsCreateRequest request) {
        return newsService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> delete(@PathVariable Long id) {
        newsService.delete(id);
        return Map.of("deleted", true);
    }
}
