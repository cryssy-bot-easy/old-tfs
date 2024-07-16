package com.ucpb.tfs.domain.product;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 3/20/13
 * Time: 7:11 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RebateRepository {

//    public Rebate load(DocumentNumber documentNumber);

    public Rebate load(String id);

    public List<Rebate> getAllRebateBy(String corresBankCode, String unitCode);

    public void persist(Rebate rebate);
}
