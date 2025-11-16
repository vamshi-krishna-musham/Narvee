package com.narvee.service.service;
import com.narvee.dto.LeaveUserDTO;
import com.narvee.entity.TmsLeave;
import com.narvee.repository.TmsLeaveRepository;
import com.narvee.service.serviceimpl.TmsEmailServiceImpl;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TmsLeaveService {
    @Autowired
	private TmsEmailServiceImpl tmsEmailService;
    private static final Logger logger = LoggerFactory.getLogger(TmsLeaveService.class);
    private final TmsLeaveRepository repo;
    
    public TmsLeave getById(Long id) {
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
            logger.info("✅ sendLeaveAppliedEmail() executed for {}", user.getEmail());
        } catch (Exception e) {
            logger.error("❌ Error while sending Leave Applied email: {}", e.getMessage(), e);
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
            return repo.save(existing);
        }).orElse(null);
    }

    public TmsLeave cancelLeave(Long id, TmsLeave partial) {
        return repo.findById(id).map(existing -> {
            if ("PENDING".equalsIgnoreCase(existing.getStatus())) {
                existing.setStatus("CANCELED");
                existing.setReason("Cancelled by user");
                return repo.save(existing);  // ✅ save & return updated entity
            }
            return existing;                 // ✅ return unchanged entity
        }).orElse(null);                      // ✅ return null if not found
    }

    public List<TmsLeave> findPending(Long managerId) {
    return repo.findByStatusAndUserIdNot("PENDING", managerId);
    }

    public List<TmsLeave> findApproved(Long managerId) {
    return repo.findByStatusAndUserIdNot("APPROVED", managerId);
    }

    public TmsLeave approve(Long id, TmsLeave partial) {
    return repo.findById(id).map(existing -> {
        // TODO: copy only the fields you want to update
        if (partial.getStatus() != null) {
            existing.setStatus(partial.getStatus());
        }
        if (partial.getReason()!=null){
            existing.setReason(partial.getReason());
        }
        if (partial.getAdminComment()!=null){
            existing.setAdminComment(partial.getAdminComment());
        }

        return repo.save(existing); // save updated entity
    }).orElse(null); // return null if not found
    }

    public TmsLeave deny(Long id, TmsLeave partial) {
    return repo.findById(id).map(existing -> {
        // TODO: copy only the fields you want to update
        if (partial.getStatus() != null) {
            existing.setStatus(partial.getStatus());
        }
        if (partial.getReason()!=null){
            existing.setReason(partial.getReason());
        }
        if (partial.getAdminComment()!=null){
            existing.setAdminComment(partial.getAdminComment());
        }
        return repo.save(existing); // save updated entity
    }).orElse(null); // return null if not found
    }

    public List<TmsLeave> findByUserId(Long userId) {
      return repo.findByUserId(userId);
    }

}
