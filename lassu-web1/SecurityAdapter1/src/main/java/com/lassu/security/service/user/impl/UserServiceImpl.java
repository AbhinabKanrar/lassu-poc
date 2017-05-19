package com.lassu.security.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lassu.security.common.model.ReqChkAuthType;
import com.lassu.security.common.model.RespChkAuthType;
import com.lassu.security.common.model.User;
import com.lassu.security.common.util.CommonConstant;
import com.lassu.security.dao.user.UserDao;
import com.lassu.security.service.user.UserService;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private EventBus eventBus;
	
	@Autowired
	private UserDao userDao;

	@Override
	public User findUserByUsername(String username) {
		return userDao.findUserByUsername(username);
	}

	@Override
	public void initiateRespChkAuthAudit(RespChkAuthType respChkAuthType) {
		eventBus.notify(CommonConstant.COMPONENT_AUTHCHK_RESP_AUDIT_DISPATCHER, Event.wrap(respChkAuthType));
	}

	@Override
	public User update(User user, String reportingSite) {
		return userDao.update(user, reportingSite);
	}

}
