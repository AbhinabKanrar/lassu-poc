package com.lassu.security.dao.user;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import com.lassu.security.common.model.User;
import com.lassu.security.common.util.CommonConstant;

public interface UserDao {

	@Retryable(maxAttempts=CommonConstant.DB_RETRY_COUNT,value=DataAccessResourceFailureException.class,backoff=@Backoff(delay = CommonConstant.DB_RETRY_DELAY))
	User findUserByUsername(String username);
	
	@Retryable(maxAttempts=CommonConstant.DB_RETRY_COUNT,value=DataAccessResourceFailureException.class,backoff=@Backoff(delay = CommonConstant.DB_RETRY_DELAY))
	User update(User user, String reportingSite);
	
}
