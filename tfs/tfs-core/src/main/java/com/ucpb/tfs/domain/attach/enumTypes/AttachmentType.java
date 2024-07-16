package com.ucpb.tfs.domain.attach.enumTypes;

public enum AttachmentType {
    Contract("Contract"), LetterOfCredit("Letter of Credit"), BillOfLading("Bill of Lading"), Unnamed("Unnamed");

    private final String code;

    AttachmentType(String code) {
        this.code = code;
    }
}
