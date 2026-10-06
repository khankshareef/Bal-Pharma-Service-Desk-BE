package Service_Desk.BalPharma.template.repository;

import Service_Desk.BalPharma.template.entity.InvestigationTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestigationTemplateRepository
        extends JpaRepository<InvestigationTemplateEntity, Long> {

    List<InvestigationTemplateEntity>
    findByTypeAndActiveTrueOrderByDisplayOrderAsc(String type);
}