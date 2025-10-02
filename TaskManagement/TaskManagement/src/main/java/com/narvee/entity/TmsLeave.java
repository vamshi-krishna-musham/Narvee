package com.narvee.entity;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tms_leaves")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TmsLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="from_date", nullable=false)
    private LocalDate fromDate;

    @Column(name="to_date", nullable=false)
    private LocalDate toDate;

    // was Enum -> now plain text (VARCHAR in DB)
    @Column(name="leave_category", nullable=false, length = 20)
    private String leaveCategory;   // e.g. "VACATION", "SICK", ...

    @Column(name="status", nullable=false, length = 20)
    private String status;          // e.g. "PENDING", "APPROVED", ...

    @Column(name="reason",length=500)
    private String reason;

    @Column(name="created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name="duration", length=100)
    private Long duration;
    public Long getDuration() {
    return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    // Optional: default status if not set
    @PrePersist
    public void prePersist() {
        if (status == null || status.isBlank()) status = "PENDING";
    }
    public String getStatus() {
    return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getReason() {
    return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    @Column(name="adminComment", length=500)
    private String adminComment;

    public String getAdminComment() {
    return adminComment;
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }
}
