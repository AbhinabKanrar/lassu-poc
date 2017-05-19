package com.lassu.security.service.user;

import com.lassu.security.common.model.RespChkAuthType;
import com.lassu.security.common.model.User;

public interface UserService {

	User findUserByUsername(String username);
	
	void initiateRespChkAuthAudit(RespChkAuthType respChkAuthType);
	
	User update(User user, String reportingSite);
	
}
