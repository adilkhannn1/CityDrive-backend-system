package kz.citydrive.admin.web;

import kz.citydrive.admin.domain.News;
import kz.citydrive.admin.dto.NewsCreateRequest;
import kz.citydrive.admin.service.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/news")
public class AdminNewsController {

    private final NewsService newsService;

    public AdminNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("newsList", newsService.findAll());
        return "admin/news";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("news", new NewsCreateRequest());
        model.addAttribute("isNew", true);
        return "admin/news-form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute NewsCreateRequest request, RedirectAttributes ra) {
        newsService.create(request);
        ra.addFlashAttribute("message", "Новость создана");
        return "redirect:/admin/news";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        News news = newsService.getEntity(id);
        NewsCreateRequest form = new NewsCreateRequest();
        form.setTitle(news.getTitle());
        form.setDescription(news.getDescription());
        form.setContent(news.getContent());
        form.setImageUrl(news.getImageUrl());
        form.setPublishedAt(news.getPublishedAt() != null ? news.getPublishedAt().toString() : "");
        form.setPublished(news.isPublished());
        model.addAttribute("news", form);
        model.addAttribute("newsId", id);
        model.addAttribute("isNew", false);
        return "admin/news-form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute NewsCreateRequest request, RedirectAttributes ra) {
        newsService.update(id, request);
        ra.addFlashAttribute("message", "Новость обновлена");
        return "redirect:/admin/news";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        newsService.delete(id);
        ra.addFlashAttribute("message", "Новость удалена");
        return "redirect:/admin/news";
    }
}
