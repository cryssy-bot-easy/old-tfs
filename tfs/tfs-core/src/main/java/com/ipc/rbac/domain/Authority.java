package com.ipc.rbac.domain;

import com.incuventure.ddd.domain.annotations.DomainEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * User: Jett Date: 6/20/12
 */
@DomainEntity
public class Authority implements Serializable {

	private Long id;

    private AuthorityTypeId authorityTypeId;

	private Date effectiveFrom;
	private Date effectiveTo;

    public AuthorityTypeId getAuthorityTypeId() {
        return authorityTypeId;
    }

    public void setAuthorityTypeId(AuthorityTypeId authorityTypeId) {
        this.authorityTypeId = authorityTypeId;
    }

    public Date getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public Date getEffectiveTo() {
		return effectiveTo;
	}

	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}

}
