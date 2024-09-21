package com.HIT.StoreManagementApp.model;

import com.HIT.StoreManagementApp.service.LogsService;
import com.HIT.StoreManagementApp.util.SpringContext;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action_type")
    private String actionType; // Action type (e.g., "User registration", "Customer update")

    @Column(name = "action_description", length = 1000)
    private String actionDescription; // Description of the action

    @Column(name = "action_time")
    private LocalDateTime actionTime; // Time of the action

    @Column(name = "branch_id")
    private Long branchId; // Branch ID (if relevant)

    @Column(name = "log_type")
    private int type; // Type of the log (e.g., 1 = Customers, 2 = Users)

    // Constructors, Getters, and Setters

    public Log() {
    }

    public Log(String actionType, String actionDescription, LocalDateTime actionTime, Long branchId, int type) {
        this.actionType = actionType;
        this.actionDescription = actionDescription;
        this.actionTime = actionTime;
        this.branchId = branchId;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    public LocalDateTime getActionTime() {
        return actionTime;
    }

    public void setActionTime(LocalDateTime actionTime) {
        this.actionTime = actionTime;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public void save() {
        LogsService logsService = SpringContext.getBean(LogsService.class);
        logsService.addLog(this.actionType, this.actionDescription, this.branchId, this.type);
    }

}
