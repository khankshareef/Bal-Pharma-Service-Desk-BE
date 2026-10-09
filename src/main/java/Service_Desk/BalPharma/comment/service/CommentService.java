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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Kolkata");

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

        System.out.println(">>> addComment reached for ticket " + ticketId);

        try {
            Set<Long> participants = new HashSet<>();
            if (ticket.getCreatedBy() != null)
                participants.add(ticket.getCreatedBy().getId());
            if (ticket.getAssignedTo() != null)
                participants.add(ticket.getAssignedTo().getId());
            participants.add(authorId);   // so the author's own other tabs update too

            System.out.println(
                    ">>> ticket:comment → participants: " + participants);
            for (Long uid : participants) {
                socketHandlers.emitToUser(uid, "ticket:comment", dto);
            }

            Set<Long> notifyIds = new HashSet<>(participants);
            notifyIds.remove(authorId);

            if (!notifyIds.isEmpty()) {
                String preview = body.length() > 90
                        ? body.substring(0, 90) + "…"
                        : body;

                for (Long uid : notifyIds) {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("id", "comment-" + saved.getId() + "-" + uid);
                    payload.put("type", "COMMENT");
                    payload.put("ticketId", ticketId);
                    payload.put("ticketCode", ticket.getTicketCode());
                    payload.put("title",
                            "New comment on " + ticket.getTicketCode());
                    payload.put("message", author.getName() + ": " + preview);
                    payload.put("source",
                            author.hasAnyRole(
                                    Service_Desk.BalPharma.location.Role.EXECUTIVE)
                                    ? "EXECUTIVE" : "USER");
                    payload.put("actorId", authorId);
                    payload.put("createdAt", dto.getCreatedAt());

                    System.out.println(
                            ">>> Notifying user " + uid + " about comment "
                                    + saved.getId());
                    socketHandlers.emitToUser(uid, "notification", payload);
                }
            }
        } catch (Exception e) {
            System.err.println(">>> Broadcast failed: "
                    + e.getClass().getSimpleName());
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
        d.setAuthorName(
                c.getAuthor() != null ? c.getAuthor().getName() : "Unknown");
        d.setAuthorInitials(
                initials(c.getAuthor() != null ? c.getAuthor().getName() : null));
        d.setBody(c.getBody());
        d.setCreatedAt(toIsoUtc(c.getCreatedAt()));
        return d;
    }

    private String toIsoUtc(Object createdAt) {
        if (createdAt == null) return null;
        if (createdAt instanceof Instant instant) return instant.toString();
        if (createdAt instanceof LocalDateTime ldt)
            return ldt.atZone(DEFAULT_ZONE).toInstant().toString();
        return createdAt.toString();
    }

    private String initials(String name) {
        if (name == null || name.isBlank()) return "";
        String[] p = name.trim().split("\\s+");
        if (p.length == 1) return p[0].substring(0, 1).toUpperCase();
        return ("" + p[0].charAt(0) + p[p.length - 1].charAt(0)).toUpperCase();
    }
}