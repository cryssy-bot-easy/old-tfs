package com.ipc.rbac.domain;

import com.incuventure.ddd.domain.annotations.DomainEntity;

import java.io.Serializable;

/**
 * User: Jett
 * Date: 6/20/12
 */
@DomainEntity
public class User implements Serializable {

    private UserActiveDirectoryId userActiveDirectoryId;
    private String firstName;
    private String lastName;
    private String tellerId;

    private String unitCode;

    public User() {
    }

    public User(UserActiveDirectoryId userActiveDirectoryId, String firstName, String lastName) {
        this.userActiveDirectoryId = userActiveDirectoryId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserActiveDirectoryId getUserActiveDirectoryId() {
        return userActiveDirectoryId;
    }

    public void setUserActiveDirectoryId(UserActiveDirectoryId userActiveDirectoryId) {
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    @Override
    public String toString() {
        return "User{" +
                "userActiveDirectoryId=" + userActiveDirectoryId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", unitCode='" + unitCode + '\'' +
                '}';
    }

    public String getTellerId() {
        return tellerId;
    }

    public void setTellerId(String tellerId) {
        this.tellerId = tellerId;
    }
}
