package com.ucpb.tfs.swift.message;


import javax.xml.bind.annotation.*;

/**
 */
@XmlRootElement(name =  "basic_header",namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
@XmlType(propOrder={"applicationIdentifier","serviceIndentifier","ltIdentifier","sessionNumber","sequenceNumber"})
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class BasicHeader {

    private String applicationIdentifier;
    private String serviceIndentifier;
    private String ltIdentifier;
    private String sessionNumber;
    private String sequenceNumber;

    @XmlElement(name = "application_identifier", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public String getApplicationIdentifier() {
        return applicationIdentifier;
    }

    public void setApplicationIdentifier(String applicationIdentifier) {
        this.applicationIdentifier = applicationIdentifier;
    }

    @XmlElement(name = "service_identifier", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public String getServiceIndentifier() {
        return serviceIndentifier;
    }

    public void setServiceIndentifier(String serviceIndentifier) {
        this.serviceIndentifier = serviceIndentifier;
    }

    @XmlElement(name = "lt_identifier", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public String getLtIdentifier() {
        return ltIdentifier;
    }

    public void setLtIdentifier(String ltIdentifier) {
        this.ltIdentifier = ltIdentifier;
    }

    @XmlElement(name = "session_number", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public String getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(String sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    @XmlElement(name = "sequence_number", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String asMTFormat(){
        StringBuilder builder = new StringBuilder();
        builder.append(applicationIdentifier)
                .append(serviceIndentifier)
                .append(ltIdentifier)
                .append(sessionNumber)
                .append(sequenceNumber);
        return builder.toString();
    }

    public String getValue() {
        return String.format("%1$8s%2$8s%3$3s%4$4s%5$4s%6$12s%7$8s",
                applicationIdentifier,
                serviceIndentifier,
                ltIdentifier,
                sessionNumber,
                sequenceNumber);
    }

}
