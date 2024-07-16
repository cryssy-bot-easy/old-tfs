package com.ucpb.tfs.swift.message;


import javax.xml.bind.annotation.*;

/**
 */
@XmlRootElement(name =  "application_header",namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
@XmlType(propOrder={"ioIdentifier","messageType","receiverAddress","messagePriority","deliveryMonitoring", "obsolescencePeriod","inputDate","inputTime","outputDate","outputTime","sessionNumber","messageInputReference"})
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ApplicationHeader {

    private String ioIdentifier;
    private String messageType ;
    private SwiftAddress receiverAddress;
    private String messagePriority;
    private String deliveryMonitoring;
    private String obsolescencePeriod;

    private String inputTime;
    private String inputDate;
    private String swiftAddress;
    private String sessionNumber;
    private String messageInputReference;
    private String outputDate;
    private String outputTime;


    public boolean isEmpty(){
        return ioIdentifier != null || messageType != null || receiverAddress != null ||
                messagePriority != null || deliveryMonitoring != null || obsolescencePeriod != null;
    }

    @XmlElement(name = "io_identifier", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public String getIoIdentifier() {
        return ioIdentifier;
    }

    public void setIoIdentifier(String ioIdentifier) {
        this.ioIdentifier = ioIdentifier;
    }

    @XmlElement(name = "message_type", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    @XmlElement(name = "receiver_address", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public SwiftAddress getReceiverAddress() {
        return receiverAddress;
    }

    @XmlTransient
    public String getReceiverAddressWithLtPadding(){
        if(receiverAddress != null){
            return receiverAddress.getAddressWithLtPadding();
        }
        return null;
    }

    public void setReceiverAddress(SwiftAddress receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    @XmlElement(name = "message_priority", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public String getMessagePriority() {
        return messagePriority;
    }

    public void setMessagePriority(String messagePriority) {
        this.messagePriority = messagePriority;
    }

    @XmlElement(name = "delivery_monitoring", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public String getDeliveryMonitoring() {
        return deliveryMonitoring;
    }

//    public String getReceiverBranchCode() {
//        return receiverBranchCode;
//    }
//
//    public void setReceiverBranchCode(String receiverBranchCode) {
//        this.receiverBranchCode = receiverBranchCode;
//    }

    public void setDeliveryMonitoring(String deliveryMonitoring) {
        this.deliveryMonitoring = deliveryMonitoring;
    }

    @XmlElement(name = "obsolescence_period", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public String getObsolescencePeriod() {
        return obsolescencePeriod;
    }

    public void setObsolescencePeriod(String obsolescencePeriod) {
        this.obsolescencePeriod = obsolescencePeriod;
    }

    public String asMTFormat(){
        StringBuilder builder = new StringBuilder();
        builder.append(getIoIdentifier())
                .append(getMessageType());
                if(getReceiverAddress() != null){
                    builder.append(getReceiverAddress().getAddressWithLtPadding());
                }
                builder.append(getMessagePriority())
                .append(getDeliveryMonitoring())
                .append(getObsolescencePeriod());
        return builder.toString();
    }

    public String getValue() {
        return  String.format("%1$8s%2$8s%3$3s%4$4s%5$4s%6$12s%7$8s",
                ioIdentifier,
                messageType,
                receiverAddress,
                messagePriority,
                deliveryMonitoring,
                obsolescencePeriod);
    }

    @XmlElement(name = "input_date", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)    public String getInputDate() {
        return inputDate;
    }

    public void setInputDate(String inputDate) {
        this.inputDate = inputDate;
    }

    @XmlElement(name = "input_time", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)    public String getInputTime() {
        return inputTime;
    }

    public void setInputTime(String inputTime) {
        this.inputTime = inputTime;
    }

    @XmlElement(name = "output_date", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)    public String getOutputDate() {
        return outputDate;
    }

    public void setOutputDate(String outputDate) {
        this.outputDate = outputDate;
    }

    @XmlElement(name = "output_time", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)    public String getOutputTime() {
        return outputTime;
    }

    public void setOutputTime(String outputTime) {
        this.outputTime = outputTime;
    }

    @XmlElement(name = "session_number", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)    public String getSessionNumber() {
        return sessionNumber;
    }

    public void setSwiftAddress(String swiftAddress) {
        this.swiftAddress = swiftAddress;
    }

    @XmlElement(name = "message_input_reference", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)    public String getMessageInputReference() {
        return messageInputReference;
    }

    public void setMessageInputReference(String messageInputReference) {
        this.messageInputReference = messageInputReference;
    }
}
