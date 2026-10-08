package Service_Desk.BalPharma.comment.service;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.auth.repository.AuthRepository;
import Service_Desk.BalPharma.comment.dto.CommentDto;
import Service_Desk.BalPharma.comment.entity.CommentEntity;
import Service_Desk.BalPharma.comment.repository.CommentRepository;
import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.socket.SocketHandlers;
import Service_Desk.BalPharma.ticket.entity.TicketEntity;
import Service_Desk.BalPharma.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final AuthRepository authRepository;
    private final SocketHandlers socketHandlers;

    @Transactional
    public CommentDto addComment(Long ticketId, String body, Long authorId) {

        if (body == null || body.isBlank())
            throw new AuthException("Comment body is required");

        TicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new AuthException("Ticket not found: " + ticketId));

        AuthEntity author = authRepository.findById(authorId)
                .orElseThrow(() -> new AuthException("User not found: " + authorId));

        CommentEntity c = new CommentEntity();
        c.setTicket(ticket);
        c.setAuthor(author);
        c.setBody(body.trim());

        CommentEntity saved = commentRepository.save(c);
        CommentDto dto = toDto(saved);

        System.out.println(">>> addComment reached, broadcasting for ticket " + ticketId);

        try {
            System.out.println(">>> Broadcasting ticket:comment for ticket " + ticketId);
            socketHandlers.broadcast("ticket:comment", dto);

            if (ticket.getCreatedBy() != null
                    && !ticket.getCreatedBy().getId().equals(authorId)) {
                socketHandlers.emitToUser(
                        ticket.getCreatedBy().getId(),
                        "notification",
                        Map.of(
                                "type", "COMMENT",
                                "ticketId", ticketId,
                                "title", "New comment on " + ticket.getTicketCode(),
                                "message", author.getName() + ": " + body
                        )
                );
            }
        } catch (Exception e) {
            System.err.println(">>> Broadcast failed: " + e.getClass().getSimpleName());
            e.printStackTrace();
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public List<CommentDto> listForTicket(Long ticketId) {
        return commentRepository
                .findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private CommentDto toDto(CommentEntity c) {
        CommentDto d = new CommentDto();
        d.setId(c.getId());
        d.setTicketId(c.getTicket() != null ? c.getTicket().getId() : null);
        d.setAuthorId(c.getAuthor() != null ? c.getAuthor().getId() : null);
        d.setAuthorName(c.getAuthor() != null ? c.getAuthor().getName() : "Unknown");
        d.setAuthorInitials(initials(c.getAuthor() != null ? c.getAuthor().getName() : null));
        d.setBody(c.getBody());
        d.setCreatedAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
        return d;
    }

    private String initials(String name) {
        if (name == null || name.isBlank()) return "";
        String[] p = name.trim().split("\\s+");
        if (p.length == 1) return p[0].substring(0, 1).toUpperCase();
        return ("" + p[0].charAt(0) + p[p.length - 1].charAt(0)).toUpperCase();
    }
}