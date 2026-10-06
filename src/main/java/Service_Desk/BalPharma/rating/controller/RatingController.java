package Service_Desk.BalPharma.rating.controller;

import Service_Desk.BalPharma.rating.dto.CreateRatingDto;
import Service_Desk.BalPharma.rating.dto.RatingResponseDto;
import Service_Desk.BalPharma.rating.dto.UpdateRatingDto;
import Service_Desk.BalPharma.rating.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingResponseDto> create(
            @RequestBody CreateRatingDto dto,
            @RequestParam String ratedBy) {
        return ResponseEntity.ok(ratingService.create(dto, ratedBy));
    }

    @GetMapping
    public ResponseEntity<List<RatingResponseDto>> getAll() {
        return ResponseEntity.ok(ratingService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ratingService.getById(id));
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<RatingResponseDto>> getByTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ratingService.getByTicket(ticketId));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<RatingResponseDto>> getByEmployee(@PathVariable String employeeId) {
        return ResponseEntity.ok(ratingService.getByEmployee(employeeId));
    }

    @GetMapping("/ticket/{ticketId}/average")
    public ResponseEntity<Double> average(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ratingService.averageForTicket(ticketId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingResponseDto> update(
            @PathVariable Long id,
            @RequestBody UpdateRatingDto dto,
            @RequestParam String employeeId) {
        return ResponseEntity.ok(ratingService.update(id, dto, employeeId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ratingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}