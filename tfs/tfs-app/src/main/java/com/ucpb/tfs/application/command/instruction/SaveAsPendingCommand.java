package com.ucpb.tfs.application.command.instruction;

/**
 * @author Marvin Volante
 */

import com.incuventure.cqrs.annotation.Command;
import com.ucpb.tfs.application.command.EtsCommand;

@Command
public class SaveAsPendingCommand extends EtsCommand {

    private boolean isDraft = false;

    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean draft) {
        isDraft = draft;
    }
}
