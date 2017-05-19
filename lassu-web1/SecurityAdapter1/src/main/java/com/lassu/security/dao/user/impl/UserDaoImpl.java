package com.lassu.security.dao.user.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.lassu.security.common.model.User;
import com.lassu.security.common.model.UserStatus;
import com.lassu.security.dao.user.UserDao;

@Repository
@EnableRetry
public class UserDaoImpl implements UserDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate jdbcNTemplate;

	private static String RETRIEVE_SQL_BY_USERNAME = "SELECT * FROM lassu.web_x_user_detail where username =:username";
	private static String INSERT_SQL = "INSERT INTO lassu.web_x_user_detail_audit ("
			+ " audit_id, user_id, reporting_site, crtn_ts" + ") VALUES ("
			+ " :audit_id, :user_id, :reporting_site, now()" + ")";
	private static String UPDATE_SQL = "SELECT * FROM lassu.web_x_user_detail where user_id = ? for update";

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
	public User findUserByUsername(String username) {
		Map<String, Object> param = new HashMap<>(1);
		param.put("username", username);

		if (System.getProperty("site.code").equalsIgnoreCase("2")) {
			RETRIEVE_SQL_BY_USERNAME = "SELECT * FROM lassu.web_y_user_detail where username =:username";
		}
		
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
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public User update(User user, String reportingSite) {
		Map<String, Object> params = new HashMap<>(3);
		params.put("audit_id", UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
		params.put("user_id", user.getUserId());
		params.put("reporting_site", reportingSite);

		if (System.getProperty("site.code").equalsIgnoreCase("2")) {
			INSERT_SQL = "INSERT INTO lassu.web_y_user_detail_audit ("
					+ " audit_id, user_id, reporting_site, crtn_ts" + ") VALUES ("
					+ " :audit_id, :user_id, :reporting_site, now()" + ")";
		}
		
		jdbcNTemplate.update(INSERT_SQL, params);

		user.setUserStatus(UserStatus.INSECURE);
		update(user);

		return user;
	}

	private void update(User user) {
		
		if (System.getProperty("site.code").equalsIgnoreCase("2")) {
			UPDATE_SQL = "SELECT * FROM lassu.web_y_user_detail where user_id = ? for update";
		}
		
		PreparedStatementCreatorFactory queryFactory = new PreparedStatementCreatorFactory(UPDATE_SQL,
				Arrays.asList(new SqlParameter(Types.BIGINT)));
		queryFactory.setUpdatableResults(true);
		queryFactory.setResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE);
		PreparedStatementCreator psc = queryFactory.newPreparedStatementCreator(new Object[] { user.getUserId() });

		final User tmpUser = new User();

		RowCallbackHandler rowHandler = new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				tmpUser.setUserId(rs.getLong("user_id"));
				tmpUser.setUsername(rs.getString("username"));
				tmpUser.setPassword(rs.getString("password"));
				tmpUser.setRole(rs.getString("role"));
				tmpUser.setMail(rs.getString("mail"));
				tmpUser.setPhoneNumber(rs.getString("phone_number"));
				tmpUser.setUserStatus(UserStatus.valueOf(rs.getString("user_status")));

				tmpUser.setUserStatus(user.getUserStatus());

				rs.updateString("user_status", tmpUser.getUserStatus().name());

				rs.updateRow();
			}
		};

		jdbcTemplate.query(psc, rowHandler);

	}

}
