package com.HIT.StoreManagementApp.controller;

import com.HIT.StoreManagementApp.model.Log;
import com.HIT.StoreManagementApp.service.LogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogsController {

    @Autowired
    private LogsService logsService;

    // Fetch logs with optional filters for actionType, branchId, and type
    @GetMapping
    public ResponseEntity<List<Log>> getAllLogs(@RequestParam(required = false) String actionType,
                                                @RequestParam(required = false) Long branchId,
                                                @RequestParam(required = false) Integer type) {
        // Get logs filtered by provided parameters (if any)
        List<Log> logs = logsService.getAllLogs(actionType, branchId, type);
        return ResponseEntity.ok(logs);
    }

    // Export logs as CSV with an optional type filter
    @GetMapping("/export")
    public ResponseEntity<Resource> exportLogsToCSV(@RequestParam(required = false) Integer type) {
        // Get logs filtered by type if provided
        List<Log> logs = logsService.getAllLogs(null, null, type);

        // Create a CSV string with the logs
        StringBuilder csvData = new StringBuilder("\uFEFF");
        csvData.append("Action Type,Action Description,Action Time,Branch ID,Type\n");
        for (Log log : logs) {
            csvData.append(log.getActionType()).append(",");
            csvData.append(log.getActionDescription()).append(",");
            csvData.append(log.getActionTime()).append(",");
            csvData.append(log.getBranchId() != null ? log.getBranchId() : "").append(",");
            csvData.append(log.getType()).append("\n");
        }

        // Convert the CSV data to a byte array input stream
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(csvData.toString().getBytes(StandardCharsets.UTF_8));
        Resource resource = new InputStreamResource(byteArrayInputStream);

        // Return the CSV as a downloadable file
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=logs_report.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
