package com.personal.RealTimeNotification.undo;

import com.personal.RealTimeNotification.entity.Notification;
import com.personal.RealTimeNotification.exception.NotificationNotFoundException;
import com.personal.RealTimeNotification.repository.NotificationRepository;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
@Slf4j
public class UndoService {

    private static final long UNDO_WINDOW_MILLIS = 60_000; // 1 minute
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private NotificationRepository notificationRepository;
    public UndoService(NotificationRepository notificationRepository) {
    	this.notificationRepository=notificationRepository;
    }

    
     // Calling for DELETE API - sets isDeleted = true and starts timer for deletion
     
    public void markForDeletionWithDelay(Long notificationId) {
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            deleteIfStillMarked(notificationId);
        }, UNDO_WINDOW_MILLIS, TimeUnit.MILLISECONDS);

        scheduledTasks.put(notificationId, future);
        log.info("Scheduled deletion for notification ID: {} in {} seconds", notificationId, UNDO_WINDOW_MILLIS / 1000);
    }

    
    //  Called from Undo API - Cancels the Deletion and Unmark the soft delete
    public boolean undoDelete(Long notificationId) {
        ScheduledFuture<?> future = scheduledTasks.remove(notificationId);
        if (future != null && !future.isDone()) {
            future.cancel(false);

            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new NotificationNotFoundException("Notification not found: " + notificationId));
            notification.setDeleted(false);
            notificationRepository.save(notification);

            log.info("Undo successful for notification ID: {}", notificationId);
            return true;
        }
        return false;
    }

    
     // Hard delete if still soft deleted
     
    private void deleteIfStillMarked(Long notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId).orElse(null);
            if (notification != null && notification.isDeleted()) {
                notificationRepository.delete(notification);
                log.info("Permanently deleted notification ID: {}", notificationId);
            }
        } finally {
            scheduledTasks.remove(notificationId); // Cleanup
        }
    }

    @PreDestroy
    public void cleanup() {
        scheduler.shutdownNow();
        log.info("Shutting down UndoService scheduler");
    }
}

