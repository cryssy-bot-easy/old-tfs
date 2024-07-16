package com.ucpb.tfs.application.command;

import java.util.HashMap;

/**
 * User: Jett
 * Date: 7/17/12
 */
public class SaveETSAsDraftCommand {

    String user;

    HashMap<String, String > details;

    SaveETSAsDraftCommand(String user, HashMap<String, String> details) {
        this.user = user;
        this.details = details;
    }

}
