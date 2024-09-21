package com.HIT.StoreManagementApp.service;

import com.HIT.StoreManagementApp.model.Log;
import com.HIT.StoreManagementApp.repository.LogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogsService {

    @Autowired
    private LogsRepository logsRepository;

    public List<Log> getAllLogs(String actionType, Long branchId, Integer type) {
        if (actionType != null && branchId != null && type != null) {
            return logsRepository.findByActionTypeAndBranchIdAndType(actionType, branchId, type);
        } else if (actionType != null && type != null) {
            return logsRepository.findByActionTypeAndType(actionType, type);
        } else if (type != null) {
            return logsRepository.findByType(type);
        } else {
            return logsRepository.findAll();
        }
    }

    public Log addLog(String actionType, String actionDescription, Long branchId, int type) {
        Log log = new Log(actionType, actionDescription, LocalDateTime.now(), branchId, type);
        return logsRepository.save(log);
    }

    public void deleteLog(Long id) {
        logsRepository.deleteById(id);
    }


}

