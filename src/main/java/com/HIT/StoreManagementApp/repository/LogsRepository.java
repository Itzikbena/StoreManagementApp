package com.HIT.StoreManagementApp.repository;

import com.HIT.StoreManagementApp.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogsRepository extends JpaRepository<Log, Long> {
    // Find logs by action type
    List<Log> findByActionType(String actionType);

    // Find logs by branch ID
    List<Log> findByBranchId(Long branchId);

    // Find logs by action type and branch ID
    List<Log> findByActionTypeAndBranchId(String actionType, Long branchId);

    // Find logs by type
    List<Log> findByType(int type);

    // Find logs by action type and type
    List<Log> findByActionTypeAndType(String actionType, int type);

    // Find logs by branch ID and type
    List<Log> findByBranchIdAndType(Long branchId, int type);

    // Find logs by action type, branch ID, and type
    List<Log> findByActionTypeAndBranchIdAndType(String actionType, Long branchId, int type);
}
