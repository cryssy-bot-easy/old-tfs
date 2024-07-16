package com.ucpb.tfs2.application.service;

import com.ucpb.tfs.domain.security.*;
import com.ucpb.tfs2.application.infrastructure.LDAPService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.Object;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
	(revision)
	SCR/ER Number: ER# 20151124-078
	SCR/ER Description: System do not update the DB session termination(normal Logout and session timeout).
	[Revised by:] Jesse James Joson
	[Date revised:] 12/03/2015
	Program [Revision] Details: Create method that will receive the passed parameter by SecurityRestServices.groovy.
	PROJECT: CORE
	MEMBER TYPE  : JAVA

	(revision)
	SCR/ER Number: 20160321-081  
	SCR/ER Description: No "Create Transaction" under NON-LC for IBDMCB user.
	[Revised by:] Lymuel Arrome Saul
	[Date revised:] 06/08/2016
	Program [Revision] Details: The User Maintenance was revised by making the position required for users with unit code 909 and 910
								and the level is automatically updated in SEC_EMPLOYEE based from the position of the user.
	Date deployment: 06/17/2016
	Member Type: JAVA
	Project: CORE
	Project Name: Employee.java

	(revision)
	SCR/ER Number: ER_20190625-104
	SCR/ER Description: Issue on wrong name (IBDJCR).
	[Revised by:] Cedrick C. Nungay
	[Date revised:] 06/24/2019
	Program [Revision] Details: Added saving of first name and last name on update.
	PROJECT: CORE
	MEMBER TYPE  : JAVA
*/

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class SecurityService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    LDAPService ldapService;

    @Autowired
    DesignationRepository designationRepository;

    UserAuthenticationProvider authenticationProvider;

    public void setAuthenticationProvider(UserAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    public void saveUserAndRoles(Map parameters) {

        System.out.println("parameters " + parameters);

        String userId = (String) parameters.get("userId");
//        String firstName = (String) parameters.get("firstName");
        String fullName = (String) parameters.get("fullName");
//        String lastName = (String) parameters.get("lastName");
        String unitCode = (String) parameters.get("unitCode");
        String postingAuthority = (String) parameters.get("postingAuthority");
//        String postingLimit = parameters.get("postingLimit").toString().replaceAll(",", "");

        String postingLimit = (parameters.get("postingLimit") != null) ? parameters.get("postingLimit").toString().replaceAll(",", "") : "";

        String email = (String) parameters.get("email");

        String position = (String) parameters.get("position");
        
        Integer level = 0;
        
        try {
        	if(parameters.get("level") != null){
        		level = Integer.parseInt(parameters.get("level").toString());
        	}        	
        } catch(Exception e) {
        	level = 0;
        }
        
        String isSuspended = (String) parameters.get("isSuspended");

        String tellerId = (String) parameters.get("tellerId");

        List<Role> userRoles = new ArrayList<Role>();

        Object tempRoles = parameters.get("userRoles");
        
        Boolean receiveEmail = parameters.get("receiveEmail") != null ? Boolean.valueOf(parameters.get("receiveEmail").toString()) : false;
        
        if(tempRoles != null) {
            if(tempRoles instanceof java.lang.String) {
                userRoles.add(roleRepository.getRole(new RoleId((String) tempRoles)));
            } else {
                // add each element to the list
                for(Object role : (List) tempRoles) {
                    userRoles.add(roleRepository.getRole(new RoleId(role.toString())));
                }
            }
        }

        if(userId != null && !userId.equalsIgnoreCase("")) {

            Boolean userExists;

            User user = userRepository.getUser(new UserId(userId));
            Employee employee = null;

            userExists = (user != null);
            System.out.println("userExists " + userExists);

            if(userExists == false) {
                System.out.println("creating new user");
                user = new User(userId);

                //employee = new Employee(user.getUserId(), firstName, lastName, unitCode);
//                employee = new Employee(user.getUserId(), fullName, unitCode);
                employee = new Employee(new UserId(userId), fullName, unitCode);
            } else {
                System.out.println("retrieving existing user");
                employee = employeeRepository.getEmployee(user.getUserId());

                employee.setFullName(fullName);

                String lastName = fullName.trim();
                String firstName = "";
                int spaceLastIndex = lastName.lastIndexOf(" ");
                
                if (spaceLastIndex >= 0) {
                    firstName = fullName.substring(0, spaceLastIndex);
                    lastName = fullName.substring(spaceLastIndex + 1, fullName.length());
                }

                employee.setLastName(lastName);
                employee.setFirstName(firstName);
                employee.setUnitCodes(unitCode);
            }

            System.out.println("new employee " + employee);

            if(postingAuthority != null) {

                // set other parameters
                employee.setPostingAuthority(postingAuthority.equalsIgnoreCase("Y"));
            }

            if (isSuspended != null) {
                employee.suspendEmployee(isSuspended);
            }

            if(email != null) {
                employee.setEmail(email);
            }

            if(position != null && !position.isEmpty()) {
                employee.setPosition(position);
            }
            
            if(level != null && level != 0) {
                employee.setLevel(level);
            }
            
            if(receiveEmail != null){
            	employee.setReceiveEmail(receiveEmail);
            }
            
            BigDecimal limit;

            try {
                limit = new BigDecimal(postingLimit);
            }
            catch(Exception e) {
                limit = BigDecimal.ZERO;
            }

            employee.setPostingLimit(limit);

            String casaLimitParam = (parameters.get("casaLimit") != null) ? parameters.get("casaLimit").toString().replaceAll(",", "") : "";

            BigDecimal casaLimit;

            try {
                casaLimit = new BigDecimal(casaLimitParam);
            } catch (Exception e) {
                casaLimit = BigDecimal.ZERO;
            }

            employee.setCasaLimit(casaLimit);

            employee.setTellerId(tellerId);

            if (parameters.get("designation") != null && StringUtils.isNotBlank(parameters.get("designation").toString())) {
                Designation designation = designationRepository.load(new Long(parameters.get("designation").toString()));
                System.out.println(designation);
                employee.assignDesignation(designation);
            }

            user.updateRoles(userRoles);

            if(!userExists) {
                userRepository.save(user);
            }

            if (parameters.get("sessionUsername") != null) {
                employee.setUpdatedByUserId((String)parameters.get("sessionUsername"));
            }
            if (parameters.get("sessionFullname") != null) {
                employee.setUpdatedByFullName((String)parameters.get("sessionFullname"));
            }

            employeeRepository.save(employee);
        }
    }

    public Map authenticateUser(String username, String password) {

        Map userDetails = authenticationProvider.authenticate(username, password);

        Boolean status = (Boolean) userDetails.get("authenticated");

        if(userDetails.containsKey("authenticated") && status) {
            Map employeeDetails = employeeRepository.getEmployeeById(new UserId(username));
            userDetails.put("employee", employeeDetails);
        }

        return userDetails;
    }

    public Map authenticateUser(String ldapDomain, String username, String password) {
        System.out.println("LDAP DOMAIN: " + ldapDomain);

        Map userDetails = authenticationProvider.authenticate(ldapDomain, username, password);

        Boolean status = (Boolean) userDetails.get("authenticated");

        if(userDetails.containsKey("authenticated") && status) {
            Map employeeDetails = employeeRepository.getEmployeeById(new UserId(username));
            userDetails.put("employee", employeeDetails);
        }

        return userDetails;
    }

    public Map authenticateUserLdap(String username, String password) {

        Map userDetails = authenticationProvider.authenticateLdap(username, password);

        Boolean status = (Boolean) userDetails.get("authenticated");

        System.out.println("authenticated is " + status);

        return userDetails;
    }

    public Map getUserDetails(String username) {

        Map userDetails = new HashMap();

        Map employeeDetails = employeeRepository.getEmployeeById(new UserId(username));
        userDetails.put("employee", employeeDetails);

        return userDetails;
    }

    public void recordLogIn(User user) {
        user.recordLogIn();
        userRepository.save(user);
    }

    public void removeLogIn(User user) {
        user.removeLogIn();
        userRepository.save(user);
    }
    
    public void removeLogIn(User user, String referrer) {    
        user.removeLogIn();
        userRepository.save(user);
    }
    
}
