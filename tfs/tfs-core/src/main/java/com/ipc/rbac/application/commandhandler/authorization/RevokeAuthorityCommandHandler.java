package com.ipc.rbac.application.commandhandler.authorization;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ipc.rbac.application.command.authorization.RevokeAuthorityCommand;
import com.ipc.rbac.application.command.authorization.enumTypes.AuthorityParameterEnum;
import com.ipc.rbac.application.command.authorization.enumTypes.AuthorizationParameterEnum;
import com.ipc.rbac.domain.Authority;
import com.ipc.rbac.domain.Authorization;
import com.ipc.rbac.domain.infrastructure.repositories.hibernate.HibernateAuthorityRepository;
import com.ipc.rbac.domain.infrastructure.repositories.hibernate.HibernateAuthorizationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class RevokeAuthorityCommandHandler implements CommandHandler<RevokeAuthorityCommand> {

	@Inject
    private HibernateAuthorityRepository authorityRepository;
    
    @Inject
    private HibernateAuthorizationRepository authorizationRepository;
    
	@Override
	public void handle(RevokeAuthorityCommand command) {

		Long authorizationId = Long.valueOf(command.getParameterValue(AuthorizationParameterEnum.AUTHORIZATION_ID));
		
		Authorization authorization = authorizationRepository.getAuthorization(authorizationId);
		
		// get authority list from parameters
		List<Map<AuthorityParameterEnum, String>> authorityTypeList = command.getAuthorityList();

		for(Map<AuthorityParameterEnum, String> authorityTypeMap: authorityTypeList) {
			Long authorityId = Long.valueOf(authorityTypeMap.get(AuthorityParameterEnum.AUTHORITY_ID));

			// get authority from parameter id
			Authority authority = authorityRepository.getAuthority(authorityId);

			// remove authorities from authorization
			authorization.removeAuthority(authority);
		}		
		
		// saveOrUpdate changes to authorization
		authorizationRepository.persistChanges(authorization);
	}
}
