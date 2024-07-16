package com.ucpb.tfs.domain.instruction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.incuventure.ddd.domain.annotations.DomainAggregateRoot;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.instruction.enumTypes.ProductType;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionType;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.utils.UtilSetFields;

import java.io.Serializable;
import java.util.*;

/**
 * User: Jett
 * Date: 6/28/12
 */
@DomainAggregateRoot
public class ServiceInstruction implements Instruction, Serializable {

    // TODO: Enum issue with DB2

    private ServiceInstructionId serviceInstructionId;

    private UserActiveDirectoryId userActiveDirectoryId;

    private UserId createdBy;
    private UserId approvedBy;

    private String approvers;

    private Date dateApproved;

    private ServiceInstructionType type;

    private ServiceInstructionStatus status;

    // product-specific details (to be transferred to product class)
    private Map<String, Object> details;

    // product indicates general trade product
    private ProductType product;

    // product attributes indicate if product is Import/Export, Cash/Regular, etc ...
    private HashMap<String, Object> productAttributes;

    // Created date
    private Date createdDate;

    // Modified date
    private Date modifiedDate;

    private Integer approvalCount;

    Process process;

    private UserId lastUser;

    // constructor
    public ServiceInstruction() {

        this.type = ServiceInstructionType.ETS;
        this.createdDate = new Date();
        this.modifiedDate = this.createdDate;
        this.approvalCount = 0;
        this.approvers = "";

        this.details =  new HashMap<String, Object>();
        this.productAttributes = new HashMap<String, Object>();
    }

    // constructor
    public ServiceInstruction(String serviceInstructionId) {

        this();

        // String serviceInstructionId = EtsNumberGenerator.generateServiceInstructionId();
        this.serviceInstructionId = new ServiceInstructionId(serviceInstructionId);

        this.type = ServiceInstructionType.ETS;
        this.createdDate = new Date();
        this.modifiedDate = this.createdDate;
        this.approvalCount = 0;
        this.approvers = "";

        this.details =  new HashMap<String, Object>();
        this.productAttributes = new HashMap<String, Object>();
    }

    public ServiceInstruction(String serviceInstructionId, Map<String, Object> details, UserActiveDirectoryId userActiveDirectoryId) {

        this(serviceInstructionId);
        this.details = details;
        this.userActiveDirectoryId = userActiveDirectoryId;
        this.approvalCount = 0;
        this.approvers = "";

        System.out.println("ETS instantiated");
    }

    public ServiceInstruction(String serviceInstructionId, Map<String, Object> details, UserId userId) {

        this(serviceInstructionId);

        // this is a temporary workaround to get both fields populated
        // todo: remove this once we transition to fully using userId
        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(userId.toString());

        this.details = details;
        this.userActiveDirectoryId = userActiveDirectoryId;
        this.createdBy = userId;
        this.approvalCount = 0;
        this.approvers = "";

        System.out.println("ETS instantiated");
    }

    // temporary constructor for testing
    public ServiceInstruction(ServiceInstructionId serviceInstructionId, Map<String, Object> details, Map<String, Object> attributes) {

        this.serviceInstructionId = serviceInstructionId;

        // copy product details to our internal structures
        this.details = new HashMap<String, Object>(details);
        this.productAttributes = new HashMap<String, Object>(attributes);
        this.approvalCount = 0;
        this.approvers = "";

        System.out.println("instantiated");
    }

    @Override
    public String toString() {
        return this.serviceInstructionId.toString();
    }

    public void updateDetails(Map<String, Object> details, UserActiveDirectoryId userActiveDirectoryId) {

        this.details.putAll(details);

        this.userActiveDirectoryId = userActiveDirectoryId;
        this.modifiedDate = new Date();
    }

    public void updateDetails(Map<String, Object> details) {

        this.details.putAll(details);

        UtilSetFields.copyMapToObject(this, (HashMap) this.details);
    }

    public void updateDetails(Map<String, Object> details, UserId userId) {

        // this is a temporary workaround to get both fields populated
        // todo: remove this once we transition to fully using userId
        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(userId.toString());

        this.details.putAll(details);

        this.userActiveDirectoryId = userActiveDirectoryId;
//        this.createdBy = userId;

        this.modifiedDate = new Date();
    }

    public void clearChargesSavedInDetails(UserActiveDirectoryId userActiveDirectoryId){
        System.out.println("in clearChargesSavedInDetails>>");
        //The formula for these charges mainly print back or remove the values contained in the details map corresponding to these keys
        //In order for the charges to refer to that included in the defaults map it should not already exist in the details map.
        this.details.remove("suppliesFee");
        this.details.remove("advisingFee");
        this.details.remove("cableFee");
    }

    public Map<String, Object> getDetails() {

        System.out.println("details:::: " + details );

        return details;
    }

    // this is used to store details as JSON into our database
    // this is not meant to be called by anything other than by the persistence mechanism
    private void setInstructionDetails(String instructionDetails) {

        Gson gson = new Gson();

        // use GSON to deserialize from JSON to our HashMap
        details = gson.fromJson(instructionDetails, new TypeToken<HashMap<String, String>>() {}.getType());
    }

    // this is used to retrieve details stored as JSON in the database to our HashMap
    // this is not meant to be called by anything other than by the persistence mechanism
    private String getInstructionDetails() {

        Gson gson = new Gson();

        // use GSON to serialize our HashMap to a JSON string that will be stored in the DB
        return gson.toJson(details);
    }

    public ServiceInstructionId getServiceInstructionId() {
        return serviceInstructionId;
    }

    public void tagStatus(ServiceInstructionStatus status) {

        this.status = status;
        this.modifiedDate = new Date();

        if (status.equals(ServiceInstructionStatus.APPROVED)) {
            this.dateApproved = this.modifiedDate;
        }
    }

    public void updateStatus(ServiceInstructionStatus status, UserActiveDirectoryId userActiveDirectoryId) {

        tagStatus(status);
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public void updateStatus(ServiceInstructionStatus status, UserId userId) {

        System.out.println("updating status to : " + status.toString() + " for " + userId.toString());

        // increment approval count if this was tagged as checked or approved
        if (status.equals(ServiceInstructionStatus.CHECKED) || status.equals(ServiceInstructionStatus.APPROVED)) {
            this.approvalCount++;

            // add to the list of approvers
            this.approvers = this.approvers + (this.approvers.isEmpty() ? "":",") + userId.toString();
        }

        // if the SI did not move forward, reset approval count back to 0 and approvers list reset
        if (status.equals(ServiceInstructionStatus.ABORTED) ||
                status.equals(ServiceInstructionStatus.DISAPPROVED) ||
                status.equals(ServiceInstructionStatus.RETURNED)) {
            this.approvalCount = 0;
            this.approvers = "";
        }

        if(status == ServiceInstructionStatus.APPROVED) {
            this.approvedBy = userId;
        }

        tagStatus(status);
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public ServiceInstructionStatus getStatus() {
        return status;
    }

    public void delete() {
    }

    public UserId getCreatedBy() {
        return createdBy;
    }

    public void resetApprovers() {
        this.approvers = "";
    }

    public void includeChargesInDetails(List<String> serviceChargeList) {
        System.out.println("including service charges in details of service instruction...");

        Map<String, Object> originalDetails = getDetails();

        originalDetails.put("serviceCharges", serviceChargeList.toString());

        this.details = originalDetails;
    }

    public void setLastUser(UserId lastUser) {
        this.lastUser = lastUser;
    }

    public UserId getLastUser() {
        return lastUser;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public void setStatus(ServiceInstructionStatus serviceInstructionStatus) {
        this.status = serviceInstructionStatus;
    }

    public void setServiceInstructionId(ServiceInstructionId serviceInstructionId) {
        this.serviceInstructionId = serviceInstructionId;
    }
}

