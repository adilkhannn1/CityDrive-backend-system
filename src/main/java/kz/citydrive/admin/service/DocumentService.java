package kz.citydrive.admin.service;

import kz.citydrive.admin.domain.LegalDocument;
import kz.citydrive.admin.dto.DocumentCreateRequest;
import kz.citydrive.admin.dto.DocumentDto;
import kz.citydrive.admin.repository.LegalDocumentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class DocumentService {

    private final LegalDocumentRepository legalDocumentRepository;

    public DocumentService(LegalDocumentRepository legalDocumentRepository) {
        this.legalDocumentRepository = legalDocumentRepository;
    }

    public List<DocumentDto> findActiveDocuments(String lang) {
        return legalDocumentRepository.findByActiveTrueOrderBySortOrderAsc().stream()
                .map(document -> DocumentDto.fromEntity(document, lang))
                .toList();
    }

    public List<LegalDocument> findAllEntities() {
        return legalDocumentRepository.findAllByOrderBySortOrderAsc();
    }

    public LegalDocument getEntity(Long id) {
        return legalDocumentRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
    }

    public int nextSortOrder() {
        return legalDocumentRepository.findTopByOrderBySortOrderDesc()
                .map(doc -> doc.getSortOrder() + 1)
                .orElse(1);
    }

    @Transactional
    public LegalDocument create(DocumentCreateRequest request) {
        validateRequest(request);
        LegalDocument document = new LegalDocument();
        applyRequest(document, request);
        document.setCreatedAt(Instant.now());
        document.setUpdatedAt(Instant.now());
        return legalDocumentRepository.save(document);
    }

    @Transactional
    public LegalDocument update(Long id, DocumentCreateRequest request) {
        validateRequest(request);
        LegalDocument document = getEntity(id);
        applyRequest(document, request);
        document.setUpdatedAt(Instant.now());
        return legalDocumentRepository.save(document);
    }

    @Transactional
    public void delete(Long id) {
        legalDocumentRepository.delete(getEntity(id));
    }

    private void applyRequest(LegalDocument document, DocumentCreateRequest request) {
        String titleRu = request.getTitleRu().trim();
        String titleKk = blankToDefault(request.getTitleKk(), titleRu);
        String titleEn = blankToDefault(request.getTitleEn(), titleRu);
        String url = request.getUrl().trim();

        document.setTitleRu(titleRu);
        document.setTitleKk(titleKk);
        document.setTitleEn(titleEn);
        document.setContentRu(url);
        document.setContentKk(url);
        document.setContentEn(url);
        document.setType("url");
        document.setSortOrder(request.getSortOrder());
        document.setActive(request.isActive());
    }

    private void validateRequest(DocumentCreateRequest request) {
        if (request.getTitleRu() == null || request.getTitleRu().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required");
        }
        if (request.getUrl() == null || request.getUrl().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "url is required");
        }
        String url = request.getUrl().trim();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "url must start with http:// or https://");
        }
    }

    private String blankToDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }
}
