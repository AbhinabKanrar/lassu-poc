package com.lassu.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lassu.common.model.User;
import com.lassu.dao.user.UserDao;
import com.lassu.service.user.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;
	
	@Override
	public User findUserByUsername(String username) {
		return userDao.findUserByUsername(username);
	}

	@Override
	public void update(User user) {
		// TODO Auto-generated method stub

	}

}
