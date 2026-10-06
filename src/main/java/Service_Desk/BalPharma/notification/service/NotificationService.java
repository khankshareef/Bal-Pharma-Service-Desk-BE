package Service_Desk.BalPharma.notification.service;

import Service_Desk.BalPharma.notification.dto.NotificationResponseDto;

import java.util.List;

public interface NotificationService {

    void notifyUser(Long recipientId,
                    String title,
                    String message,
                    String type,
                    String action,
                    Long ticketId);

    void notifyTicketEvent(Long ticketId,
                           String title,
                           String message,
                           String type,
                           String action,
                           List<Long> recipientIds);

    List<NotificationResponseDto> getForUser(Long userId);
    long getUnreadCount(Long userId);
    void markAsRead(Long id, Long userId);
    void markAllAsRead(Long userId);
    void delete(Long id);
}