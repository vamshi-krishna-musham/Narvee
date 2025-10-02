package com.narvee.service;

import com.narvee.entity.TmsLeave;
import com.narvee.repository.TmsLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TmsLeaveService {

    private final TmsLeaveRepository repo;

    public TmsLeave apply(TmsLeave leave) {
        return repo.save(leave);
    }

    public List<TmsLeave> all() {
        return repo.findAll();
    }

    public TmsLeave getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public boolean deleteById(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }
    public TmsLeave cancelLeave(Long id) {
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

    public TmsLeave addComment(Long id, String comment) {
        TmsLeave leave = repo.findById(id).orElseThrow();
        leave.setAdminComment(comment);
        return repo.save(leave);
    }


    public List<TmsLeave> findByUserId(Long userId) {
      return repo.findByUserId(userId);
    }

}
