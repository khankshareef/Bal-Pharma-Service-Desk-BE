package Service_Desk.BalPharma.template.service;

import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.template.dto.InvestigationTemplateDto;
import Service_Desk.BalPharma.template.entity.InvestigationTemplateEntity;
import Service_Desk.BalPharma.template.repository.InvestigationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvestigationTemplateService {

    private final InvestigationTemplateRepository repository;

    public List<InvestigationTemplateDto> getByType(String type) {
        return repository.findByTypeAndActiveTrueOrderByDisplayOrderAsc(type.toUpperCase())
                .stream()
                .map(InvestigationTemplateDto::from)
                .toList();
    }

    public InvestigationTemplateDto create(InvestigationTemplateDto dto) {
        InvestigationTemplateEntity e = new InvestigationTemplateEntity();
        e.setTitle(dto.getTitle());
        e.setIcon(dto.getIcon() != null ? dto.getIcon() : "tool");
        e.setColor(dto.getColor() != null ? dto.getColor() : "gray");
        e.setType(dto.getType() != null ? dto.getType().toUpperCase() : "INVESTIGATION");
        e.setContent(dto.getContent());
        e.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        e.setActive(true);
        return InvestigationTemplateDto.from(repository.save(e));
    }

    public void delete(Long id) {
        if (!repository.existsById(id))
            throw new AuthException("Template not found: " + id);
        repository.deleteById(id);
    }
}