package com.personal.RealTimeNotification.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.personal.RealTimeNotification.dto.NotificationDto;
import com.personal.RealTimeNotification.dto.ProcessingResponseDto;
import com.personal.RealTimeNotification.service.NotificationService;
import com.personal.RealTimeNotification.undo.UndoService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    
    private NotificationService notificationService;
    private UndoService undoService;
    
    public NotificationController(NotificationService notificationService,UndoService undoService) {
    	this.notificationService=notificationService;
    	this.undoService=undoService;
    }

    @PostMapping("/create/{userId}")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<ProcessingResponseDto<String>> create(@PathVariable Long userId, @RequestBody NotificationDto dto) {
    	 ProcessingResponseDto<String> response = notificationService.createNotification(userId, dto);
    	    return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<List<NotificationDto>> getAll(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getAllNotifications(userId));
    }

    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<List<NotificationDto>> getUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @PutMapping("/read/{notificationId}")
    @PreAuthorize("@notificationSecurity.isOwnerOrAdmin(#notificationId,authentication)")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("@notificationSecurity.isOwnerOrAdmin(#notificationId, authentication)")
    public ResponseEntity<String> delete(@PathVariable Long notificationId) {
      String responseString =  notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok(responseString);
    }
    
    @PostMapping("/{id}/undo")
    @PreAuthorize("@notificationSecurity.isOwnerOrAdmin(#notificationId, authentication)")
    public ResponseEntity<?> undoDelete(@PathVariable Long id) {
        boolean success = undoService.undoDelete(id);
        if (success) {
            return ResponseEntity.ok("Undo successful!");
        } else {
            return ResponseEntity.status(HttpStatus.GONE).body("Too late! Already deleted.");
        }
    }

    
    @PostMapping("/publish/{userId}")
    @PreAuthorize("hasRole('ADMIN')") // only ADMIN can publish
    public ResponseEntity<NotificationDto> publishNotification(@PathVariable Long userId, @RequestBody NotificationDto dto) {
        return new ResponseEntity<>(notificationService.publishNotification(userId, dto), HttpStatus.CREATED);
    }

}

