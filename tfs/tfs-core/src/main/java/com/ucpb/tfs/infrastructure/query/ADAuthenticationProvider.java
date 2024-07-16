package com.ucpb.tfs.infrastructure.query;

import com.ucpb.tfs.application.query.AuthenticationProvider;
import com.ucpb.tfs.domain.security.User;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.security.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class ADAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Map<String, Object> authenticate(String userid, String password) {

        Map<String, Object> returnValues = new HashMap<String, Object>();

        User loggedInUser = userRepository.getUser(new UserId(userid));

        System.out.println("the user is: " + loggedInUser.toString());

        System.out.println("i am here");

        if(loggedInUser != null) {
            // if user is a TFS User

            if(userid.equals(password)) {
                System.out.println("AUTHORIZED");
                returnValues.put("status", "AUTHORIZED");
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
