package Service_Desk.BalPharma.rating.service;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.auth.repository.AuthRepository;
import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.rating.dto.CreateRatingDto;
import Service_Desk.BalPharma.rating.dto.RatingResponseDto;
import Service_Desk.BalPharma.rating.dto.UpdateRatingDto;
import Service_Desk.BalPharma.rating.entity.TicketRatingEntity;
import Service_Desk.BalPharma.rating.repository.TicketRatingRepository;
import Service_Desk.BalPharma.ticket.entity.TicketEntity;
import Service_Desk.BalPharma.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingServiceImpl implements RatingService {

    private final TicketRatingRepository ratingRepository;
    private final TicketRepository ticketRepository;
    private final AuthRepository authRepository;

    @Override
    public RatingResponseDto create(CreateRatingDto dto, String employeeId) {

        if (dto.getTicketId() == null)
            throw new AuthException("Ticket is required");
        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5)
            throw new AuthException("Rating must be between 1 and 5");

        TicketEntity ticket = ticketRepository.findById(dto.getTicketId())
                .orElseThrow(() -> new AuthException("Ticket not found: " + dto.getTicketId()));

        AuthEntity user = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Employee not found"));

        if (ratingRepository.existsByTicketIdAndRatedById(ticket.getId(), user.getId())) {
            throw new AuthException("You have already rated this ticket");
        }

        TicketRatingEntity r = new TicketRatingEntity();
        r.setTicket(ticket);
        r.setRatedBy(user);
        r.setRating(dto.getRating());
        r.setComments(dto.getComments());

        TicketRatingEntity saved = ratingRepository.save(r);
        return RatingResponseDto.from(saved);
    }

    @Override
    public RatingResponseDto getById(Long id) {
        return RatingResponseDto.from(
                ratingRepository.findByIdWithRelations(id)
                        .orElseThrow(() -> new AuthException("Rating not found: " + id))
        );
    }

    @Override
    public List<RatingResponseDto> getAll() {
        return ratingRepository.findAllWithRelations().stream()
                .map(RatingResponseDto::from)
                .toList();
    }

    @Override
    public List<RatingResponseDto> getByTicket(Long ticketId) {
        return ratingRepository.findByTicketId(ticketId).stream()
                .map(RatingResponseDto::from)
                .toList();
    }

    @Override
    public List<RatingResponseDto> getByEmployee(String employeeId) {
        AuthEntity user = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Employee not found"));
        return ratingRepository.findByRatedById(user.getId()).stream()
                .map(RatingResponseDto::from)
                .toList();
    }

    @Override
    public RatingResponseDto update(Long id, UpdateRatingDto dto, String employeeId) {

        TicketRatingEntity r = ratingRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new AuthException("Rating not found: " + id));

        if (r.getRatedBy() == null ||
                !r.getRatedBy().getEmployeeId().equals(employeeId))
            throw new AuthException("You are not allowed to edit this rating");

        if (dto.getRating() != null) {
            if (dto.getRating() < 1 || dto.getRating() > 5)
                throw new AuthException("Rating must be between 1 and 5");
            r.setRating(dto.getRating());
        }
        if (dto.getComments() != null)
            r.setComments(dto.getComments());

        return RatingResponseDto.from(ratingRepository.save(r));
    }

    @Override
    public void delete(Long id) {
        if (!ratingRepository.existsById(id))
            throw new AuthException("Rating not found: " + id);
        ratingRepository.deleteById(id);
    }

    @Override
    public Double averageForTicket(Long ticketId) {
        return ratingRepository.averageRatingForTicket(ticketId);
    }
}