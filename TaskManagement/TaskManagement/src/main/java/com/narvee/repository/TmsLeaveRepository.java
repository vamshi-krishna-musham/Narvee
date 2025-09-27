package com.narvee.repository;
import java.util.List;
import com.narvee.entity.TmsLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TmsLeaveRepository extends JpaRepository<TmsLeave, Long> {
    List<TmsLeave> findByStatus(String status);
    List<TmsLeave> findByUserId(Long userId);

}
