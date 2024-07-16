package com.ipc.rbac.application.commandhandler.authorization;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ipc.rbac.application.command.authorization.GrantAuthorityCommand;
import com.ipc.rbac.application.command.authorization.enumTypes.AuthorityParameterEnum;
import com.ipc.rbac.application.command.authorization.enumTypes.AuthorizationParameterEnum;
import com.ipc.rbac.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class GrantAuthorityCommandHandler implements CommandHandler<GrantAuthorityCommand>{

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private AuthorizationRepository authorizationRepository;
    
	private DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

	@Override
	public void handle(GrantAuthorityCommand command) {
		
		Long authorizationId = Long.valueOf(command.getParameterValue(AuthorizationParameterEnum.AUTHORIZATION_ID));
		
		Authorization authorization = authorizationRepository.getAuthorization(authorizationId);

		Date effectiveFrom = null;
		Date effectiveTo=  null;

		// get authority list from parameters
		List<Map<AuthorityParameterEnum, String>> authorityTypeList = command.getAuthorityList();
		
		for(Map<AuthorityParameterEnum, String> authorityTypeMap: authorityTypeList) {

			String authorityTypeId = authorityTypeMap.get(AuthorityParameterEnum.AUTHORITY_TYPE);

			try{

				effectiveFrom = (Date)formatter.parse(authorityTypeMap.get(AuthorityParameterEnum.EFFECTIVE_FROM));
				effectiveTo = (Date)formatter.parse(authorityTypeMap.get(AuthorityParameterEnum.EFFECTIVE_TO));

			} catch(Exception e) {
                e.printStackTrace();
			}

        	Authority authority = new Authority();    		
        	authority.setAuthorityTypeId(new AuthorityTypeId(authorityTypeId));
        	authority.setEffectiveFrom(effectiveFrom);
        	authority.setEffectiveTo(effectiveTo);

        	authorityRepository.persist(authority);

        	authorization.addAuthority(authority);
		}		

		// adds authorities to authorization
		authorizationRepository.persistChanges(authorization);
	}
}
