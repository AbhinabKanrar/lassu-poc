package com.lassu.dao.user.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.lassu.common.model.User;
import com.lassu.common.model.UserStatus;
import com.lassu.dao.user.UserDao;

@Repository
@EnableRetry
public class UserDaoImpl implements UserDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate jdbcNTemplate;
	
	private static final String RETRIEVE_SQL_BY_USERNAME = "SELECT * FROM lassu.web_y_user_detail where username =:username";

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
	public User findUserByUsername(String username) {
		Map<String, Object> param = new HashMap<>(1);
		param.put("username", username);

		User user = jdbcNTemplate.query(RETRIEVE_SQL_BY_USERNAME, param, new ResultSetExtractor<User>() {
			@Override
			public User extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					User user = new User();
					user.setUserId(rs.getLong("user_id"));
					user.setUsername(rs.getString("username"));
					user.setPassword(rs.getString("password"));
					user.setRole(rs.getString("role"));
					user.setMail(rs.getString("mail"));
					user.setPhoneNumber(rs.getString("phone_number"));
					user.setUserStatus(UserStatus.valueOf(rs.getString("user_status")));
					return user;
				} else {
					return null;
				}
			}
		});

		return user;
	}

	@Override
	public void update(User user) {
		// TODO Auto-generated method stub

	}

}
