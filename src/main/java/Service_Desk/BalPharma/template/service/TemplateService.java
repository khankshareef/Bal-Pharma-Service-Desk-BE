package Service_Desk.BalPharma.template.service;

import Service_Desk.BalPharma.template.dto.*;

import java.util.List;

public interface TemplateService {
    TemplateResponseDto create(CreateTemplateDto dto);
    List<TemplateResponseDto> getAll();
    TemplateResponseDto getById(Long id);
    TemplateResponseDto update(Long id, UpdateTemplateDto dto);
    void delete(Long id);
    TemplateResponseDto toggleActive(Long id);
}