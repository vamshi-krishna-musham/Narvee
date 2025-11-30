package com.narvee.service.service;
import com.narvee.dto.LeaveUserDTO;
import com.narvee.entity.TmsLeave;
import com.narvee.repository.TmsLeaveRepository;
import com.narvee.service.serviceimpl.TmsEmailServiceImpl;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TmsLeaveService {
    @Autowired
	private TmsEmailServiceImpl tmsEmailService;
    private static final Logger logger = LoggerFactory.getLogger(TmsLeaveService.class);
    private final TmsLeaveRepository repo;

    public TmsLeave getById(Long id){
        return repo.findById(id).orElse(null);
    }
    public TmsLeave apply(TmsLeave leave) {
        // Save leave
        TmsLeave saved = repo.save(leave);
        logger.info("Leave saved successfully for userId={}, leaveId={}", saved.getUserId(), saved.getId());

        // Fetch user details
        List<LeaveUserDTO> userDetails = repo.getLeaveUser(saved.getUserId());
        if (userDetails.isEmpty()) {
            logger.warn("No user found for userId={} — skipping email", saved.getUserId());
            return saved;
        }

        LeaveUserDTO user = userDetails.get(0);
        logger.info("User found: {} <{}>", user.getFirstname(), user.getEmail());

        try {
            tmsEmailService.sendLeaveAppliedEmail(user, saved);
            logger.info(" sendLeaveAppliedEmail() executed for {}", user.getEmail());
        } catch (Exception e) {
            logger.error(" Error while sending Leave Applied email: {}", e.getMessage(), e);
        }

        return saved;
    }

    public TmsLeave update(Long id, TmsLeave partial) {
        return repo.findById(id).map(existing -> {

            if (partial.getLeaveCategory() != null) {
                existing.setLeaveCategory(partial.getLeaveCategory());
            }
            if (partial.getReason() != null) {
                existing.setReason(partial.getReason());
            }
            if (partial.getStatus() != null) {
                existing.setStatus(partial.getStatus());
            }
            if (partial.getAdminComment() != null) {
                existing.setAdminComment(partial.getAdminComment());
            }
            if (partial.getDuration() != null) {
                existing.setDuration(partial.getDuration());
            }
            if (partial.getFromDate() != null) {
                existing.setFromDate(partial.getFromDate());
            }
            if (partial.getToDate() != null) {
                existing.setToDate(partial.getToDate());
            }

            existing.setUpdatedAt(java.time.LocalDateTime.now());

            // save updated entity
            TmsLeave saved = repo.save(existing);

            // fetch user details for the email
            List<LeaveUserDTO> userDetails = repo.getLeaveUser(saved.getUserId());
            if (userDetails == null || userDetails.isEmpty()) {
                logger.warn("No user found for userId={} while sending leave updated email", saved.getUserId());
                return saved;
            }

            LeaveUserDTO user = userDetails.get(0);

            try {
                tmsEmailService.sendLeaveUpdatedEmail(user, saved);
                logger.info("Leave updated email sent to {}", user.getEmail());
            } catch (Exception e) {
                logger.error("Error sending leave updated email to {}: {}", user.getEmail(), e.getMessage(), e);
            }

            return saved;

        }).orElse(null);
    }


    public TmsLeave cancelLeave(Long id, TmsLeave partial) {
        return repo.findById(id).map(existing -> {
            if ("PENDING".equalsIgnoreCase(existing.getStatus())) {
                existing.setStatus("CANCELED");
                existing.setReason("Cancelled by user");
                return repo.save(existing);  // save & return updated entity
            }
            return existing;                 //return unchanged entity
        }).orElse(null);                      // return null if not found
    }

    public List<TmsLeave> getPending(String orgName, Long managerId, String profileRole) {
    if ("ADMIN".equals(profileRole.toUpperCase())) {
            return repo.findPendingLeavesForAdmin(orgName, managerId);
        }
    else if ("PROJECT MANAGER".equals(profileRole.toUpperCase())){
        return repo.findPendingByOrganisationForProjectManager(orgName, managerId);
    }
    else{
            return repo.findPendingByOrganisationForSuperAdmin(orgName, managerId);
        }
    }

    public List<TmsLeave> findApproved(Long managerId) {
    return repo.findByStatusAndUserIdNot("APPROVED", managerId);
    }

    public TmsLeave approve(Long id, TmsLeave partial) {
    return handleLeaveDecision(id, partial, "Approved");
}

    public TmsLeave deny(Long id, TmsLeave partial) {
        return handleLeaveDecision(id, partial, "Denied");
    }

    // Shared private helper
    private TmsLeave handleLeaveDecision(Long id, TmsLeave partial, String decision) {
        return repo.findById(id).map(existing -> {
            if (partial.getStatus() != null) existing.setStatus(partial.getStatus());
            if (partial.getReason() != null) existing.setReason(partial.getReason());
            if (partial.getAdminComment() != null) existing.setAdminComment(partial.getAdminComment());
            existing.setUpdatedAt(LocalDateTime.now());

            TmsLeave saved = repo.save(existing);

            // Fetch user details for email
            List<LeaveUserDTO> userDetails = repo.getLeaveUser(saved.getUserId());
            if (!userDetails.isEmpty()) {
                LeaveUserDTO user = userDetails.get(0);
                try {
                    tmsEmailService.sendLeaveDecisionEmail(user, saved, decision);
                } catch (Exception e) {
                    logger.error("Error sending leave decision email: {}", e.getMessage(), e);
                }
            }

            return saved;
        }).orElse(null);
    }

    public List<TmsLeave> findByUserId(Long userId) {
      return repo.findByUserId(userId);
    }
    //@Scheduled(cron = "*/10 * * * * *", zone = "America/Chicago")
    public void sendDailyAdminSummary() {

        logger.info("Starting daily summary cron...");

        List<LeaveUserDTO> admins = repo.getSuperAdmins();
        if (admins.isEmpty()) {
            logger.warn("No super admins found!");
            return;
        }

        List<TmsLeave> pending = repo.findAllPendingLeaves();
        logger.info("Pending leaves: {}", pending);
        long count = pending.size();

        for (LeaveUserDTO admin : admins) {
            try {
                tmsEmailService.sendSuperAdminPendingSummaryEmail(admin, count);
                logger.info("Summary email sent to {}", admin.getEmail());
            } catch (Exception e) {
                logger.error("Error sending summary to {}: {}", admin.getEmail(), e.getMessage());
            }
        }
    }

}
