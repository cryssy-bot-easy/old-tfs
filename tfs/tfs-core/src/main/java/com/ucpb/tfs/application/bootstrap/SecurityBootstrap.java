package com.ucpb.tfs.application.bootstrap;

import com.ucpb.tfs.domain.security.*;
import com.ucpb.tfs.domain.security.Object;
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
    private ObjectRepository objectRepository;

    @Inject
    private PermissionRepository permissionRepository;

    @Inject
    private RoleRepository roleRepository;

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
                    //initialize();
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

//        if (userRepository.getCount() == 0) {
//            User user = new User(new UserId("branch"));
//            userRepository.save(user);
//        }

        if(objectRepository.getCount() == 0) {
//            com.ucpb.tfs.domain.security.Object siObject = new Object("SI", "Service Instruction");
//            objectRepository.save(siObject);

//            com.ucpb.tfs.domain.security.Object etsApproveBtnObject = new Object("SIAPPBTN", "SI Approval Button");
//            objectRepository.save(etsApproveBtnObject);

//            Permission createSIPermission = new Permission(Operation.CREATE, siObject);
//            permissionRepository.save(createSIPermission);

//            Permission viewSIPermission = new Permission(Operation.VIEW, siObject);
//            permissionRepository.save(viewSIPermission);

//            Permission approveSIPermission = new Permission(Operation.EXECUTE, siObject);
//            permissionRepository.save(approveSIPermission);


//            com.ucpb.tfs.domain.security.Object di = new Object("SI", "Service Instruction");
//            objectRepository.save(siObject);


            Role branchMakerRole = new Role("BRM", "Branch Maker");
//            branchMakerRole.addPermission(createSIPermission);
//            branchMakerRole.addPermission(viewSIPermission);
            roleRepository.save(branchMakerRole);

            Role branchCheckerRole = new Role("BRC", "Branch Checker");
//            branchCheckerRole.addPermission(viewSIPermission);
            roleRepository.save(branchCheckerRole);

            Role branchApproverRole = new Role("BRA", "Branch Approver");
//            branchApproverRole.addPermission(viewSIPermission);
            roleRepository.save(branchApproverRole);

            User user = new User("branchmaker");

            user.addRole(branchMakerRole);
            user.addRole(branchCheckerRole);
            userRepository.save(user);

        }
    }

}
