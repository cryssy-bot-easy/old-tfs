package com.ucpb.tfs.infrastructure.query;

import com.ucpb.tfs.application.query.SelectBoxDataProvider;
import com.ucpb.tfs.domain.product.BankGuarantee;
import com.ucpb.tfs.domain.product.LetterOfCredit;
import com.ucpb.tfs.utils.DomainFieldUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Jett
 * Date: 8/16/12
 */
public class ReflectionSelectBoxDataProvider implements SelectBoxDataProvider{

    @Override
    public Map<String, Object> getAllProductSelectData() {

        Map<String, Object> fieldLookups = new HashMap<String, Object>();

        fieldLookups.put("LetterOfCredit", DomainFieldUtils.getFieldChoices(LetterOfCredit.class));
        fieldLookups.put("BankGuarantee", DomainFieldUtils.getFieldChoices(BankGuarantee.class));

        return(fieldLookups);
    }
}
