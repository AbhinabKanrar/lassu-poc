package com.lassu.service.user;

import com.lassu.common.model.User;

public interface UserService {

	User findUserByUsername(String username);
	void update(User user);
	
}
