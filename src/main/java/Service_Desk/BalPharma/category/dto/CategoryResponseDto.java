package Service_Desk.BalPharma.category.dto;

import Service_Desk.BalPharma.category.entity.CategoryEntity;
import Service_Desk.BalPharma.category.entity.SubCategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto {
    private Long id;
    private String categoryCode;
    private String name;
    private Long departmentId;
    private String departmentName;
    private String scope;
    private Boolean active;

    private List<SubCategoryDto> subCategories;

    private long ticketCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class SubCategoryDto {
        private Long id;
        private String name;
    }

    public static CategoryResponseDto from(CategoryEntity c) {
        return new CategoryResponseDto(
                c.getId(),
                c.getCategoryCode(),
                c.getName(),
                c.getDepartment() != null ? c.getDepartment().getId() : null,
                c.getDepartment() != null ? c.getDepartment().getName() : null,
                c.getScope(),
                c.getActive(),
                c.getSubCategories().stream()
                        .map(s -> new SubCategoryDto(s.getId(), s.getName()))
                        .toList(),
                0L,
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}