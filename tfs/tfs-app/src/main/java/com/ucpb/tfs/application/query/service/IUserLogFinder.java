package com.ucpb.tfs.application.query.service;

import java.util.List;
import java.util.Map;

import com.incuventure.cqrs.query.Finder;
import org.apache.ibatis.annotations.Param;

@Finder
public interface IUserLogFinder {

	List<Map<String,?>> findUsersToLogout(@Param("userId") String userId,
			@Param("loginStatus") String loginStatus); 
}
