package com.lassu.security.service.user;

import com.lassu.security.common.model.ReqChkAuthType;
import com.lassu.security.common.model.RespChkAuthType;
import com.lassu.security.common.model.User;

public interface UserService {

	User findUserByUsername(String username);
	
	void initiateReqChkAuthAudit(ReqChkAuthType reqChkAuthType);
	
	User update(User user, String reportingSite);
	
}
