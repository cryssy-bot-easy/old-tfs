package com.ipc.rbac.application.commandhandler.authorization;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ipc.rbac.application.command.authorization.ExtendAuthorityCommand;
import com.ipc.rbac.application.command.authorization.enumTypes.AuthorityParameterEnum;
import com.ipc.rbac.application.command.authorization.enumTypes.AuthorizationParameterEnum;
import com.ipc.rbac.domain.Authority;
import com.ipc.rbac.domain.Authorization;
import com.ipc.rbac.domain.AuthorizationRepository;
import com.ipc.rbac.domain.infrastructure.repositories.hibernate.HibernateAuthorityRepository;
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
public class ExtendAuthorityCommandHandler implements CommandHandler<ExtendAuthorityCommand> {
	
	@Inject
    private HibernateAuthorityRepository authorityRepository;
   
	@Inject
    private AuthorizationRepository authorizationRepository;
	
	private DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");	
   
	@Override
	public void handle(ExtendAuthorityCommand command) {

		Long authorizationId = Long.valueOf(command.getParameterValue(AuthorizationParameterEnum.AUTHORIZATION_ID));
		
		Date effectiveTo = null;
		
		Authorization authorization = authorizationRepository.getAuthorization(authorizationId);
		
		// get authority list from parameters
		List<Map<AuthorityParameterEnum, String>> authorityTypeList = command.getAuthorityList();

		for(Map<AuthorityParameterEnum, String> authorityTypeMap: authorityTypeList) {

			Long authorityId = Long.valueOf(authorityTypeMap.get(AuthorityParameterEnum.AUTHORITY_ID));

			// get authority from parameter id
			Authority authority = authorityRepository.getAuthority(authorityId);
			
			try{

				effectiveTo = formatter.parse(authorityTypeMap.get(AuthorityParameterEnum.EFFECTIVE_TO));
				authority.setEffectiveTo(effectiveTo);

			} catch(Exception e) {
                e.printStackTrace();
			}
			
			authorityRepository.persist(authority);
		}		
		
		// saveOrUpdate changes to authorization
		authorizationRepository.persistChanges(authorization);
	}
}
