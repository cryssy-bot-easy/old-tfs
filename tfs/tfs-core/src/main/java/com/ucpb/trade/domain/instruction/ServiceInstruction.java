package com.ucpb.trade.domain.instruction;

import com.incuventure.ddd.domain.annotations.DomainAggregateRoot;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: Jett
 * Date: 6/28/12
 * @author Jett Gamboa
 */
@DomainAggregateRoot
public class ServiceInstruction implements Instruction {

    public enum ServiceInstructionType {
        ETS, OTHER
    }

    public enum ServiceInstructionStatus {
        DRAFT, PENDING, PREPARED, ABORTED, CHECKED, RETURNED, APPROVED, DISAPPROVED, FOR_REVERSAL
    }

    private String userId;
    private DateTime dateApproved;

    private ServiceInstructionType type;

    private String serviceInstructionNumber;
    private String documentNumber;

    private ServiceInstructionStatus status;

    private HashMap<String, String> details;

    private List<RoutingInformation> routes;

    Process process;

    // constructor
    public ServiceInstruction() {
        routes = new ArrayList<RoutingInformation>();
    }

    @Override
    public String toString() {
        return serviceInstructionNumber;
    }

	// add a route to the next assignee
    public void addRouteInformation(String assignee) {

    }

    public void tagStatus(ServiceInstructionStatus status) {
        this.status = status;
    }

    public void delete() {

    }
}
