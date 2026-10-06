package Service_Desk.BalPharma.configData.repository;

import Service_Desk.BalPharma.configData.entity.StatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRepository extends JpaRepository<StatusEntity, Long> {
    List<StatusEntity> findAllByOrderByDisplayOrderAsc();
}