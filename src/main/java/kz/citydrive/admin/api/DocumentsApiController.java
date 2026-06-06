package kz.citydrive.admin.api;

import kz.citydrive.admin.dto.DocumentsListResponse;
import kz.citydrive.admin.service.DocumentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
public class DocumentsApiController {

    private final DocumentService documentService;

    public DocumentsApiController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public DocumentsListResponse list(
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {
        return new DocumentsListResponse(documentService.findActiveDocuments(acceptLanguage));
    }
}
