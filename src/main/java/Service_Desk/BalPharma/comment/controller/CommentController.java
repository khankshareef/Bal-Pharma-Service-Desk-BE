package Service_Desk.BalPharma.comment.controller;

import Service_Desk.BalPharma.comment.dto.CommentDto;
import Service_Desk.BalPharma.comment.service.CommentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{ticketId}/comments")
    public ResponseEntity<List<CommentDto>> list(@PathVariable Long ticketId) {
        return ResponseEntity.ok(commentService.listForTicket(ticketId));
    }

    @PostMapping("/{ticketId}/comments")
    public ResponseEntity<CommentDto> add(
            @PathVariable Long ticketId,
            @RequestBody CreateCommentDto req,
            @AuthenticationPrincipal Jwt jwt) {

        Long userId = ((Number) jwt.getClaim("userId")).longValue();
        CommentDto saved = commentService.addComment(ticketId, req.getBody(), userId);
        return ResponseEntity.ok(saved);
    }

    @Data
    public static class CreateCommentDto {
        private String body;
    }
}