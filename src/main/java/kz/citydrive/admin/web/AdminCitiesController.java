package kz.citydrive.admin.web;

import kz.citydrive.admin.domain.City;
import kz.citydrive.admin.dto.CityCreateRequest;
import kz.citydrive.admin.service.CityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/cities")
public class AdminCitiesController {

    private final CityService cityService;

    public AdminCitiesController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("cities", cityService.findAllEntities());
        return "admin/cities";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        CityCreateRequest form = new CityCreateRequest();
        form.setSortOrder(cityService.nextSortOrder());
        model.addAttribute("city", form);
        model.addAttribute("isNew", true);
        return "admin/city-form";
    }

    @PostMapping("/new")
    public String create(
            @ModelAttribute CityCreateRequest request,
            @RequestParam(value = "active", required = false) String active,
            RedirectAttributes ra) {
        request.setActive("true".equalsIgnoreCase(active));
        cityService.create(request);
        ra.addFlashAttribute("message", "Город добавлен");
        return "redirect:/admin/cities";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        City city = cityService.getEntity(id);
        CityCreateRequest form = new CityCreateRequest();
        form.setName(city.getName());
        form.setSortOrder(city.getSortOrder());
        form.setActive(city.isActive());
        model.addAttribute("city", form);
        model.addAttribute("cityId", id);
        model.addAttribute("isNew", false);
        return "admin/city-form";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @ModelAttribute CityCreateRequest request,
            @RequestParam(value = "active", required = false) String active,
            RedirectAttributes ra) {
        request.setActive("true".equalsIgnoreCase(active));
        cityService.update(id, request);
        ra.addFlashAttribute("message", "Город обновлён");
        return "redirect:/admin/cities";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            cityService.delete(id);
            ra.addFlashAttribute("message", "Город удалён");
        } catch (ResponseStatusException e) {
            ra.addFlashAttribute("error", e.getReason());
        }
        return "redirect:/admin/cities";
    }
}
