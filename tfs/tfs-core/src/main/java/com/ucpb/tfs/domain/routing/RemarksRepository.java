package com.ucpb.tfs.domain.routing;

import java.util.List;

/**
 */
public interface RemarksRepository {

    public void addRemark(Remark remark);

    public void editRemark(Remark remark);

    public List<Remark> getRemarks(String remarkId);

}
