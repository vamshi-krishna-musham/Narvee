package com.narvee.controller;
import com.narvee.entity.TmsLeave;
import com.narvee.service.service.TmsLeaveService;

import lombok.Data;
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
    @Data
    public class PendingRequest {
        private Long managerId;
        private String organisationName;
    }

    // health check
    @GetMapping("/ping")
    public String ping() {
        return "OK";
    }
    @GetMapping("/{id}")
    public TmsLeave getById(@PathVariable Long id) {
        return service.getById(id);
    }
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    @PostMapping("/apply")
    public TmsLeave apply(@RequestBody TmsLeave leave) {
        return service.apply(leave);
    }
    @PutMapping("/update/{id}")
    public TmsLeave update(@PathVariable Long id, @RequestBody TmsLeave leave) {
        return service.update(id, leave);
    }
    @GetMapping("/pending")
    public List<TmsLeave> getPending(
        @RequestParam Long managerId,
        @RequestParam String organisationName) {
        return service.getPending(organisationName, managerId);
    }

    @GetMapping("/approved/{managerId}")
    public List<TmsLeave> approvedLeavesForManager(@PathVariable Long managerId) {
        return service.findApproved(managerId);
    }
    @GetMapping("/user/{userId}")
    public List<TmsLeave> leavesByUser(@PathVariable Long userId) {
        return service.findByUserId(userId);
    }
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TmsLeave> patchLeaveCancel(@PathVariable Long id, @RequestBody TmsLeave partial) {
        TmsLeave cancelled = service.cancelLeave(id, partial);
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
        TmsLeave updated = service.deny(id, partial);
        return updated != null ? ResponseEntity.ok(updated)
                               : ResponseEntity.notFound().build();
    }     
}
