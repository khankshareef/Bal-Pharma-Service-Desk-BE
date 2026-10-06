package Service_Desk.BalPharma.template.service;

import Service_Desk.BalPharma.category.entity.CategoryEntity;
import Service_Desk.BalPharma.category.entity.SubCategoryEntity;
import Service_Desk.BalPharma.category.repository.CategoryRepository;
import Service_Desk.BalPharma.category.repository.SubCategoryRepository;
import Service_Desk.BalPharma.department.entity.DepartmentEntity;
import Service_Desk.BalPharma.department.repository.DepartmentRepository;
import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.template.dto.*;
import Service_Desk.BalPharma.template.entity.TicketTemplateEntity;
import Service_Desk.BalPharma.template.repository.TicketTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final TicketTemplateRepository templateRepository;
    private final DepartmentRepository departmentRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    @Override
    public TemplateResponseDto create(CreateTemplateDto dto) {

        if (dto.getTemplateName() == null || dto.getTemplateName().isBlank())
            throw new AuthException("Template name is required");
        if (dto.getDepartmentId() == null)
            throw new AuthException("Department is required");
        if (dto.getCategoryId() == null)
            throw new AuthException("Category is required");
        if (dto.getPriority() == null || dto.getPriority().isBlank())
            throw new AuthException("Priority is required");

        DepartmentEntity dep = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new AuthException("Department not found"));

        CategoryEntity cat = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new AuthException("Category not found"));

        SubCategoryEntity sub = null;
        if (dto.getSubCategoryId() != null) {
            sub = subCategoryRepository.findById(dto.getSubCategoryId())
                    .orElseThrow(() -> new AuthException("Sub-category not found"));
        }

        TicketTemplateEntity t = new TicketTemplateEntity();
        t.setTemplateName(dto.getTemplateName().trim());
        t.setDepartment(dep);
        t.setCategory(cat);
        t.setSubCategory(sub);
        t.setPriority(dto.getPriority().toUpperCase());
        t.setActive(dto.getActive() != null ? dto.getActive() : true);

        TicketTemplateEntity saved = templateRepository.save(t);

        return TemplateResponseDto.from(
                templateRepository.findByIdWithRelations(saved.getId()).orElse(saved)
        );
    }

    @Override
    public List<TemplateResponseDto> getAll() {
        return templateRepository.findAllWithRelations().stream()
                .map(TemplateResponseDto::from)
                .toList();
    }

    @Override
    public TemplateResponseDto getById(Long id) {
        return TemplateResponseDto.from(
                templateRepository.findByIdWithRelations(id)
                        .orElseThrow(() -> new AuthException("Template not found: " + id))
        );
    }

    @Override
    public TemplateResponseDto update(Long id, UpdateTemplateDto dto) {
        TicketTemplateEntity t = templateRepository.findById(id)
                .orElseThrow(() -> new AuthException("Template not found: " + id));

        if (dto.getTemplateName() != null && !dto.getTemplateName().isBlank())
            t.setTemplateName(dto.getTemplateName().trim());

        if (dto.getDepartmentId() != null)
            t.setDepartment(departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new AuthException("Department not found")));

        if (dto.getCategoryId() != null)
            t.setCategory(categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new AuthException("Category not found")));

        if (dto.getSubCategoryId() != null)
            t.setSubCategory(subCategoryRepository.findById(dto.getSubCategoryId())
                    .orElseThrow(() -> new AuthException("Sub-category not found")));

        if (dto.getPriority() != null && !dto.getPriority().isBlank())
            t.setPriority(dto.getPriority().toUpperCase());

        if (dto.getActive() != null)
            t.setActive(dto.getActive());

        TicketTemplateEntity saved = templateRepository.save(t);

        return TemplateResponseDto.from(
                templateRepository.findByIdWithRelations(saved.getId()).orElse(saved)
        );
    }

    @Override
    public void delete(Long id) {
        if (!templateRepository.existsById(id))
            throw new AuthException("Template not found: " + id);
        templateRepository.deleteById(id);
    }

    @Override
    public TemplateResponseDto toggleActive(Long id) {
        TicketTemplateEntity t = templateRepository.findById(id)
                .orElseThrow(() -> new AuthException("Template not found: " + id));

        t.setActive(!Boolean.TRUE.equals(t.getActive()));
        templateRepository.save(t);

        return TemplateResponseDto.from(
                templateRepository.findByIdWithRelations(id).orElse(t)
        );
    }
}