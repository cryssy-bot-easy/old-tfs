/**
 * 
 */
package com.ipc.rbac.domain;

import com.incuventure.ddd.domain.annotations.DomainEntity;
import com.ipc.rbac.domain.enumTypes.AuthorityTypeEnum;

import java.io.Serializable;

/**
 * @author itdipc6
 *
 */
@DomainEntity
public class AuthorityType implements Serializable {
	
	private Long id;
	
	private AuthorityTypeEnum type;
	
	private String name;
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AuthorityTypeEnum getType() {
		return type;
	}
	
	protected void setType(AuthorityTypeEnum type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
