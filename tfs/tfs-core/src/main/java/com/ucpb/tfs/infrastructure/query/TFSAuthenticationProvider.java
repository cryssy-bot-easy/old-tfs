package com.ucpb.tfs.infrastructure.query;

import com.ucpb.tfs.application.query.AuthenticationProvider;
import com.ucpb.tfs.application.service.UserAuthenticationProvider;
import com.ucpb.tfs.domain.security.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.lang.Object;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Transactional
public class TFSAuthenticationProvider implements AuthenticationProvider {


    @Inject
    private UserRepository userRepository;

    @Inject
    private EmployeeRepository employeeRepository;

    @Inject
    UserAuthenticationProvider userAuthenticationProvider;

    @Override
    public Map<String, Object> authenticate(String userid, String password) {

        // todo: remove this
        System.out.println("authenticating " + userid + " " + password);

        Map<String, Object> returnValues = new HashMap<String, Object>();

//        User loggedInUser = null;
        User loggedInUser = userRepository.getUser(new UserId(userid));

        if(loggedInUser != null) {
            // if user is a TFS User
//            password = password + "hello";
            // call the authentication provider to check if this login is valid
            if(userAuthenticationProvider.authenticate(userid, password)) {

                System.out.println("AUTHORIZED");
                returnValues.put("status", "AUTHORIZED");

                /* todo: copy this to the ADAuthenticationProvider */

                Map<String, Object> userProperties = new HashMap<String, Object>();

                Set<Object> roleSet = new HashSet<Object>();

                Set<Role> roles = loggedInUser.getRoles();

                // get the role(s) and add to our set, this will be sent to the front-end for further action
                for(Role role : roles) {
                    HashMap<String, String> roleDetail = new HashMap<String, String>();

                    roleDetail.put("id", role.getId());
                    roleDetail.put("description", role.getDescription());

                    System.out.println(role.getDescription());

                    roleSet.add(roleDetail);
                }


                userProperties.put("roles", roleSet);

                Employee employee = employeeRepository.getEmployee(new UserId(userid));

                if(employee != null) {

                    userProperties.put("firstname", employee.getFirstName());
                    userProperties.put("lastname", employee.getLastName());
                    userProperties.put("unitcode", employee.getUnitCode());

                    userProperties.put("postAuthority", employee.getPostingAuthority());
                    userProperties.put("level", employee.getLevel());
                    userProperties.put("postingLimit", employee.getPostingLimit());

                    // added authenticated property
                    userProperties.put("authenticated", "true");

                    returnValues.put("userdetails", userProperties);
                }

                /* end copy to AD authentication provider */

            }
            else {
                System.out.println("UNAUTHORIZED");
                returnValues.put("status", "UNAUTHORIZED");
            }

        } else {
            // user is not a TFS user
            System.out.println("INVALID");
            returnValues.put("status", "INVALID");
        }

        return returnValues;
    }
}
