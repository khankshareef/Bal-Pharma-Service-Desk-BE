package Service_Desk.BalPharma.notification.service;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.auth.repository.AuthRepository;
import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.notification.dto.NotificationResponseDto;
import Service_Desk.BalPharma.notification.entity.NotificationEntity;
import Service_Desk.BalPharma.notification.repository.NotificationRepository;
import Service_Desk.BalPharma.ticket.entity.TicketEntity;
import Service_Desk.BalPharma.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final AuthRepository authRepository;
    private final TicketRepository ticketRepository;

    @Override
    public void notifyUser(Long recipientId,
                           String title,
                           String message,
                           String type,
                           String action,
                           Long ticketId) {

        if (recipientId == null) {
            log.warn(">>> Notification skipped: recipientId is null");
            return;
        }

        AuthEntity recipient = authRepository.findById(recipientId).orElse(null);
        if (recipient == null) {
            log.warn(">>> Notification skipped: recipient {} not found", recipientId);
            return;
        }

        TicketEntity ticket = ticketId != null
                ? ticketRepository.findById(ticketId).orElse(null)
                : null;

        NotificationEntity n = new NotificationEntity();
        n.setRecipient(recipient);
        n.setTicket(ticket);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setAction(action);
        n.setIsRead(false);

        notificationRepository.save(n);
        log.info(">>> Notification → user {} | action={} | ticket={}",
                recipientId, action, ticketId);
    }

    @Override
    public void notifyTicketEvent(Long ticketId,
                                  String title,
                                  String message,
                                  String type,
                                  String action,
                                  List<Long> recipientIds) {

        if (recipientIds == null || recipientIds.isEmpty()) {
            log.warn(">>> notifyTicketEvent skipped: no recipients");
            return;
        }

        recipientIds.stream()
                .distinct()
                .forEach(id -> notifyUser(id, title, message, type, action, ticketId));
    }

    @Override
    public List<NotificationResponseDto> getForUser(Long userId) {
        if (userId == null) return List.of();
        return notificationRepository.findByRecipientId(userId).stream()
                .map(NotificationResponseDto::from)
                .toList();
    }

    @Override
    public long getUnreadCount(Long userId) {
        if (userId == null) return 0;
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    @Override
    public void markAsRead(Long id, Long userId) {
        NotificationEntity n = notificationRepository.findById(id)
                .orElseThrow(() -> new AuthException("Notification not found: " + id));

        if (!n.getRecipient().getId().equals(userId))
            throw new AuthException("This notification does not belong to you");

        n.setIsRead(true);
        notificationRepository.save(n);
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<NotificationEntity> unread =
                notificationRepository.findByRecipientId(userId).stream()
                        .filter(n -> !Boolean.TRUE.equals(n.getIsRead()))
                        .toList();
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    @Override
    public void delete(Long id) {
        if (!notificationRepository.existsById(id))
            throw new AuthException("Notification not found: " + id);
        notificationRepository.deleteById(id);
    }
}