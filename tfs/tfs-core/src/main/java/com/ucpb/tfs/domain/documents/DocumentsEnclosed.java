package com.ucpb.tfs.domain.documents;

import org.hibernate.envers.Audited;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 3/6/13
 * Time: 5:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Audited
public class DocumentsEnclosed implements Serializable {

    private String id;

    private String documentName;

    private String original1;
    private String original2;

    private String duplicate1;
    private String duplicate2;

    public DocumentsEnclosed() {}

    public DocumentsEnclosed(String documentName, String original1, String original2, String duplicate1, String duplicate2) {
        this.documentName = documentName;

        this.original1 = original1;
        this.original2 = original2;

        this.duplicate1 = duplicate1;
        this.duplicate2 = duplicate2;
    }

    public void getProperties() {
        System.out.println("documentName > " + this.documentName+"\n"+
                "original1 > " + this.original1+"\n"+
                "original2 > " + this.original2+"\n"+
                "duplicate1 > " + this.duplicate1+"\n"+
                "duplicate2 > " + this.duplicate2);
        System.out.println("******************");
    }

    public String getId() {
        return id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getOriginal1() {
        return original1;
    }

    public String getOriginal2() {
        return original2;
    }

    public String getDuplicate1() {
        return duplicate1;
    }

    public String getDuplicate2() {
        return duplicate2;
    }
}
