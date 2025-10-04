package com.narvee.controller;

import com.narvee.entity.TmsLeave;
import com.narvee.service.TmsLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/leaves")   // <-- matches Angular calls
@CrossOrigin(origins = "http://localhost:4200") // allow Angular dev server
@RequiredArgsConstructor
public class TmsLeaveController {

    private final TmsLeaveService service;

    // health check
    @GetMapping("/ping")
    public String ping() {
        return "OK";
    }

    @PostMapping
    public TmsLeave apply(@RequestBody TmsLeave leave) {
        return service.apply(leave);
    }

    @GetMapping
    public List<TmsLeave> all() {
        return service.all();
    }

    @GetMapping("/pending/{managerId}")
    public List<TmsLeave> pendingLeavesForManager(@PathVariable Long managerId) {
        return service.findPending(managerId);
    }

    @GetMapping("/{id}")
    public TmsLeave getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/user/{userId}")
    public List<TmsLeave> leavesByUser(@PathVariable Long userId) {
        return service.findByUserId(userId);
    }


    

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeave(@PathVariable Long id) {
        boolean deleted = service.deleteById(id);
        return deleted ? ResponseEntity.noContent().build()
                       : ResponseEntity.notFound().build();
    }
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TmsLeave> patchLeaveCancel(@PathVariable Long id) {
        TmsLeave cancelled = service.cancelLeave(id);
        return cancelled != null
            ? ResponseEntity.ok(cancelled)
            : ResponseEntity.notFound().build();
    }


    @PatchMapping("/{id}/approve")
    public ResponseEntity<TmsLeave> patchLeaveApprove(@PathVariable Long id, @RequestBody TmsLeave partial) {
        TmsLeave updated = service.approve(id, partial);
        return updated != null ? ResponseEntity.ok(updated)
                               : ResponseEntity.notFound().build();
    }
    @PatchMapping("/{id}/deny")
    public ResponseEntity<TmsLeave> patchLeaveDeny(@PathVariable Long id, @RequestBody TmsLeave partial) {
        TmsLeave updated = service.approve(id, partial);
        return updated != null ? ResponseEntity.ok(updated)
                               : ResponseEntity.notFound().build();
    }
        // ✅ new endpoint for manager comment

            
}
