package kz.citydrive.admin.web;

import kz.citydrive.admin.domain.LegalDocument;
import kz.citydrive.admin.dto.DocumentCreateRequest;
import kz.citydrive.admin.service.DocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/documents")
public class AdminDocumentsController {

    private final DocumentService documentService;

    public AdminDocumentsController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("documents", documentService.findAllEntities());
        return "admin/documents";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        DocumentCreateRequest form = new DocumentCreateRequest();
        form.setSortOrder(documentService.nextSortOrder());
        model.addAttribute("document", form);
        model.addAttribute("isNew", true);
        return "admin/document-form";
    }

    @PostMapping("/new")
    public String create(
            @ModelAttribute DocumentCreateRequest request,
            @RequestParam(value = "active", required = false) String active,
            RedirectAttributes ra) {
        request.setActive("true".equalsIgnoreCase(active));
        documentService.create(request);
        ra.addFlashAttribute("message", "Документ добавлен");
        return "redirect:/admin/documents";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        LegalDocument doc = documentService.getEntity(id);
        DocumentCreateRequest form = new DocumentCreateRequest();
        form.setTitleRu(doc.getTitleRu());
        form.setTitleKk(doc.getTitleKk());
        form.setTitleEn(doc.getTitleEn());
        form.setUrl(doc.getContentRu());
        form.setSortOrder(doc.getSortOrder());
        form.setActive(doc.isActive());
        model.addAttribute("document", form);
        model.addAttribute("documentId", id);
        model.addAttribute("isNew", false);
        return "admin/document-form";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @ModelAttribute DocumentCreateRequest request,
            @RequestParam(value = "active", required = false) String active,
            RedirectAttributes ra) {
        request.setActive("true".equalsIgnoreCase(active));
        documentService.update(id, request);
        ra.addFlashAttribute("message", "Документ обновлён");
        return "redirect:/admin/documents";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        documentService.delete(id);
        ra.addFlashAttribute("message", "Документ удалён");
        return "redirect:/admin/documents";
    }
}
