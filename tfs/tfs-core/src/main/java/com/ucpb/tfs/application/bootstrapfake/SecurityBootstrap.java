package com.ucpb.tfs.application.bootstrapfake;


import com.ipc.rbac.domain.*;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component
public class SecurityBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Inject
    private TransactionTemplate tt;

    @Inject
    private UserRepository userRepository;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private PermissionRepository permissionRepository;

    public SecurityBootstrap() {
    }

    public SecurityBootstrap(PlatformTransactionManager tm) {
        tt.setTransactionManager(tm);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try{
                    initialize();
//                    System.out.println("test");
                }
                catch (Exception ex){
                    ex.printStackTrace();
                    status.setRollbackOnly();
                }
            }
        });

    }


    public void initialize() {

        if (userRepository.getCount() == 0) {
            User user = new User(new UserActiveDirectoryId("branch"), "Branch", "User");
            userRepository.persist(user);
        }

        if(permissionRepository.getCount() == 0) {

            List<Permission> permissions = new ArrayList<Permission>();

            permissions.add(new Permission("SAVE-INSTRUCTION", "Save an eTS"));
            permissions.add(new Permission("APPROVE-INSTRUCTION", "Approve an eTS"));
            permissions.add(new Permission("CHECK-INSTRUCTION", "Check an eTS"));

            permissions.add(new Permission("SAVE-SERVICE", "Save a service request"));
            permissions.add(new Permission("APPROVE-SERVICE", "Approve a service request"));
            permissions.add(new Permission("CHECK-SERVICE", "Check a service request"));

            for(Permission permission : permissions ) {
                permissionRepository.persist(permission);
            }
        }

        if(roleRepository.getCount() == 0) {
            List<Role> roles = new ArrayList<Role>();

            roles.add(new Role("Branch Maker", "Branch Maker"));
            roles.add(new Role("Branch Checker", "Branch Checker"));
            roles.add(new Role("Branch Approver", "Branch Approver"));
            roles.add(new Role("TSD Maker", "TSD Maker"));
            roles.add(new Role("TSD Checker", "TSD Checker"));
            roles.add(new Role("TSD Approver", "TSD Approver"));

            for(Role role : roles ) {
                roleRepository.persist(role);
            }

        }




    }

}
