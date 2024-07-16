package com.ucpb.tfs.domain.mt;


import java.util.List;

public interface OutgoingMTRepository {

    public void persist(OutgoingMT outgoingMT);

    public void merge(OutgoingMT outgoingMT);

    public void update(OutgoingMT outgoingMT);

    public OutgoingMT load(Long id);

    public List getAllOutgoingMT();

}
