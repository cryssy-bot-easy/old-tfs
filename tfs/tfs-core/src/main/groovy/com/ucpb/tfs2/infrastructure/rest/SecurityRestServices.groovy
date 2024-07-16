package com.ucpb.tfs2.infrastructure.rest
import com.google.gson.Gson
import com.ucpb.tfs.domain.security.*
import com.ucpb.tfs.domain.security.enums.LdapDomain
import com.ucpb.tfs2.application.infrastructure.LDAPService
import com.ucpb.tfs2.application.infrastructure.LDAPServiceBranch
import com.ucpb.tfs2.application.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.PropertiesFactoryBean
import org.springframework.stereotype.Component

import javax.ws.rs.*
import javax.ws.rs.core.*

/*
 (revision)
 SCR/ER Number:
 SCR/ER Description:
 [Revised by:] Cedrick C. Nungay
 [Date revised:] 12/20/2017
 Program [Revision] Details: Added null checker for ldapDomain on ldapSearchUser
 Member Type: Groovy
 Project: Core
 Project Name: SecurityRestServices.groovy
*/

@Path("/security")
@Component
class SecurityRestServices {

    @Autowired
    LDAPService ldapService;

    @Autowired
    LDAPServiceBranch ldapServiceBranch;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SecurityService securityService

    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    DesignationRepository designationRepository

    @Autowired
    PropertiesFactoryBean appProperties

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/ldap/search")
    public Response ldapSearchUser(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            String userid = jsonParams.get("userid")

            String ldapDomain = jsonParams.get("ldapDomain")

            println "################################"
            println "RETRIEVING USER DETAILS FROM AD:"
            println "################################"

            if (userid != null) {

                Map results

//                try {
//                    results = ldapService.getUserAttributes(userid);
//                    results = ldapService.getUserAttributes(ldapDomain, userid)
                    println "connecting to AD..."
                    println "SEARCHING FOR USER: " + userid + " FROM AD"

                    // removed the try catch for this block so if an exception was caught during the retrieval of user details
                    // from AD, it will be caught prior to returning the details
                    if( ldapDomain != null ) {
                        if (LdapDomain.BRANCH.equals(LdapDomain.valueOf(ldapDomain))) {
                            System.out.println("is branch");
                            results = ldapServiceBranch.getUserAttributes(userid);
                        } else if (LdapDomain.UCPB8.equals(LdapDomain.valueOf(ldapDomain))) {
                            System.out.println("is main");
                            results = ldapService.getUserAttributes(userid);
                        }
		     }
//                } catch (Exception e1) {
                    // do nothing
//                    e1.printStackTrace()
//                }
//                Map results = ldapService.getUserAttributes(userid);

                if (results == null || results.size() == 0) {
                    returnMap.put("status", "ok");
                    returnMap.put("details", "not found")
                } else {

                    // get the groups from the corresponding TFS roles
                    List<String> adGroups = results?.get("groups");
                    println "adGroups = " + adGroups

                    if(!adGroups.isEmpty()) {
                        List roles = roleRepository.getRolesMatchingADGroups(adGroups);
                        results.put("roles", roles);
                    }

                    returnMap.put("status", "ok");

                    Employee employee = employeeRepository.getEmployee(new UserId(userid))

                    if (employee) {
                        returnMap.put("isExisting", true)
                    } else {
                        returnMap.put("isExisting", false)
                    }

                    returnMap.put("details", results)
                }

            } else {
                returnMap.put("status", "error");
                returnMap.put("details", "missing parameter")
            }


        } catch(Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/roles")
    public Response getAllRoles() {

        Gson gson = new Gson();
        Map returnMap = new HashMap();

        String result="";
        try {

            List<Role> roles = roleRepository.getAllRoles();

            returnMap.put("status", "ok");
            returnMap.put("details", roles);

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/user/{userid}")
    public Response getUserDetails(@PathParam("userid") String userId) {

        Gson gson = new Gson();
        Map returnMap = new HashMap();

        String result="";
		String fullName = "";
		
        try {

            Map user =  userRepository.getUserById(new UserId(userId))

			Employee employee = employeeRepository.getEmployee(new UserId(userId))
			
			employee.each { details ->
				user.put("employeeDetails", details)
			}

            returnMap.put("status", "ok");
            returnMap.put("details", user);

        } catch(Exception e) {

			e.printStackTrace();
            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/employee/{userid}")
    public Response getEmployeeDetails(@PathParam("userid") String userId) {

        Gson gson = new Gson();
        Map returnMap = new HashMap();

        String result="";
        try {

            Map employee = employeeRepository.getEmployeeById(new UserId(userId))

            returnMap.put("status", "ok");
            returnMap.put("details", employee);

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/employee/search")
    public Response searchEmployee(@Context UriInfo allUri) {

        Gson gson = new Gson();
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap();

        String userId = "";
        String fullName = "";

        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        String result="";
        try {

            if (jsonParams.get("userId") != null) {
                userId = jsonParams.get("userId")
            }

            if (jsonParams.get("fullName") != null) {
                fullName = jsonParams.get("fullName")
            }
			
            List<Object> employees = employeeRepository.getEmployeesMatching(userId, fullName)
			
            returnMap.put("status", "ok");
            returnMap.put("details", employees);

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/user/save")
    public Response savePayment(@Context UriInfo allUri, String postRequestBody) {
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        try {

            Map formDetails = gson.fromJson(postRequestBody, Map.class);
            println "formDetails :: " + formDetails
            securityService.saveUserAndRoles(formDetails)

            returnMap.put("details", "ok")
            returnMap.put("status", "ok")

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/authenticate")
    public Response authenticateUser(@Context UriInfo allUri, String postRequestBody) {

        println "authenticating in WS..."

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        Map userDetails = new HashMap();
        Boolean detailsMismatch = false;

        User user = null;

        Map formDetails = gson.fromJson(postRequestBody, Map.class);
        String referrer = formDetails.get("referrer");
        println "referrer = ${referrer}"

        String checkMultipleLoginStr = appProperties.object.getProperty('tfs.check.multiple.login');
        Boolean checkMultipleLogin = (checkMultipleLoginStr != null) ? Boolean.parseBoolean(checkMultipleLoginStr.toLowerCase()) : null;
        println "checkMultipleLogin = ${checkMultipleLogin}"

        try {

            String username = formDetails.get("u");
            String password = formDetails.get("p");
			boolean isSessionLoggedIn = formDetails.get("isSessionLoggedIn");

            // Doing the actual authentication is expensive, so check first
            // if multiple login is attempted before anything else.
            user = userRepository.getUser(new UserId(username))

            if (user != null) {

                println "user.isLoggedIn() = ${user.isLoggedIn()}"

                // Conditions for not checking multiple login
                if (!checkMultipleLogin || referrer == null || (referrer != null && !referrer.equals('login'))) {
                    user.removeLogIn();
                }

                if (!user.isLoggedIn() || !isSessionLoggedIn) {

                    println "authenticating user: " + username

                    String ldapDomain = formDetails.get("ldapDomain");

    //            Map userDetails = securityService.authenticateUser(username, password);
                    userDetails = securityService.authenticateUser(ldapDomain, username, password);
                    println "authenticating in AD..."

    //            if (userDetails.get("authenticated") == false) {
    //                throw new Exception(userDetails.get("errorMessage"));
    //            }

                    Map userDetailsFromLDAP = null;

                    // removed the try catch so the error will be caught prior to returning user details
                    try {
                        //userDetailsFromLDAP = ldapService.getUserAttributes(username);

                        // retrieves user details from AD that will be used to compare against the details stored in TFS
                        // if there is mismatch
                        println "retrieving user details from AD"
                        if (LdapDomain.BRANCH.equals(LdapDomain.valueOf(ldapDomain))) {
                            System.out.println("is branch");
                            userDetailsFromLDAP = ldapServiceBranch.getUserAttributes(username);

                        } else if (LdapDomain.UCPB8.equals(LdapDomain.valueOf(ldapDomain))) {
                            System.out.println("is main");
                            userDetailsFromLDAP = ldapService.getUserAttributes(username);
                        }
                    } catch (Exception e1) {
                        // do nothing
                        println "exception caught on retrieval of user details from AD"
                        e1.printStackTrace()
                    }

                    println "retrieving employee details"

                    Employee employee = employeeRepository.getEmployee(new UserId(username))

                    // check if details from AD matches the details saved in TFS
                    String extensionAttribute5 = null;

                    println "checking for user details mismatch..."

                    if (userDetailsFromLDAP != null && userDetailsFromLDAP.get("extensionAttribute5") != null && !"".equals(userDetailsFromLDAP.get("extensionAttribute5"))) {
                        extensionAttribute5 = userDetailsFromLDAP.get("extensionAttribute5").toString()
                    }

    //            if (extensionAttribute5 != null &&
    //                    (!extensionAttribute5.substring(2,extensionAttribute5.length()).equals(employee.getUnitCode()) ||
    //                    !userDetailsFromLDAP.get("email").equals(employee.getEmail()))) {
                    if (extensionAttribute5 != null &&
                            (!extensionAttribute5.substring(2,extensionAttribute5.length()).equals(employee.getUnitCode()))) {
                        detailsMismatch = true;
                    }

                    // get designation details
                    def designation = employee?.getDesignation()

                    if (designation) {
                        Designation designationObject = designationRepository.load(designation.id)
                        userDetails << [designationDetails : designationObject]
                    }

                    returnMap.put("details", userDetails)
                    returnMap.put("status", "ok")

                    returnMap.put("detailsMismatch", detailsMismatch);

                } else {

                    userDetails << [status : "User is already logged in (" + formDetails.get("ip") + "). Multiple login not allowed."]
                    returnMap.put("data", userDetails);

                    Map errorDetails = new HashMap();
                    errorDetails << [code : "User is already logged in (" + formDetails.get("ip") + ")."]
                    errorDetails << [description : "User is already logged in (" + formDetails.get("ip") + "). Multiple login not allowed."]

                    returnMap.put("status", "error");
                    returnMap.put("error", errorDetails);

                }

            } else {

                userDetails << [status : "User not found."]
                returnMap.put("data", userDetails);

                Map errorDetails = new HashMap();
                errorDetails << [code : "User not found."]
                errorDetails << [description : "User not found."]

                returnMap.put("status", "error");
                returnMap.put("error", errorDetails);
            }

        } catch(Exception e) {

            println "exception caught authenticating... "

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        if (userDetails.get("authenticated") != null) {

            Boolean authenticated = (Boolean)userDetails.get("authenticated")

            println "##### AUTHENTICATED = ${authenticated}"
            println "##### DETAILS MISMATCH = ${detailsMismatch}"

            // Conditions before login is recorded
            if (checkMultipleLogin && (referrer != null && referrer.equals('login'))) {
                // If authenticated and no details mismatch, user has successfully logged in
                if (authenticated && !detailsMismatch) {
                    securityService.recordLogIn(user)
                }
            }
        }

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/authenticateLdap")
    public Response authenticateUserLdap(@Context UriInfo allUri, String postRequestBody) {
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        try {

            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            String username = formDetails.get("u");
            String password = formDetails.get("p");

            Map userDetails = securityService.authenticateUserLdap(username, password);

            returnMap.put("details", userDetails)
            returnMap.put("status", "ok")

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/ldap/searchLdap")
    public Response ldapSearchUserTest(@Context UriInfo allUri) {
        println "searchLdap"
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            String userid = jsonParams.get("userid")

            String ldapDomain = jsonParams.get("ldapDomain")

            if (userid != null) {

                Map results

                try {
                    if (LdapDomain.BRANCH.equals(LdapDomain.valueOf(ldapDomain))) {
                        System.out.println("is branch");
                        results = ldapServiceBranch.getUserAttributesTest(userid);

                    } else if (LdapDomain.UCPB8.equals(LdapDomain.valueOf(ldapDomain))) {
                        System.out.println("is main");
                        results = ldapService.getUserAttributesTest(userid);
                    }
                } catch (Exception e1) {
                    // do nothing
                    println "searchLdap"
                    e1.printStackTrace()
                }
//                Map results = ldapService.getUserAttributes(userid);

                if (results.size() == 0) {
                    returnMap.put("status", "ok");
                    returnMap.put("details", "not found")
                } else {

                    // get the groups from the corresponding TFS roles
                    List<String> adGroups = results.get("groups");

                    if(!adGroups.isEmpty()) {
                        List roles = roleRepository.getRolesMatchingADGroups(adGroups);
                        results.put("roles", roles);
                    }

                    returnMap.put("status", "ok");

                    Employee employee = employeeRepository.getEmployee(new UserId(userid))

                    if (employee) {
                        returnMap.put("isExisting", true)
                    } else {
                        returnMap.put("isExisting", false)
                    }

                    returnMap.put("details", results)
                }

            } else {
                returnMap.put("status", "error");
                returnMap.put("details", "missing parameter")
            }


        } catch(Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/removeLogIn")
    public Response removeLogIn(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        try {

            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            String username = formDetails.get("u");
            User user = userRepository.getUser(new UserId(username));

            String referrer = formDetails.get("referrer");
            println "removeLogin() referrer: ${referrer}"

            securityService.removeLogIn(user, referrer);

            returnMap.put("details", "")
            returnMap.put("status", "ok")

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();
    }
}
