package cn.edu.zju.gislab.SCSServices.service.impl;

//import cn.edu.zju.gislab.SZTDService.mapper.UserinfoMapper;
//import cn.edu.zju.gislab.SZTDService.po.Userinfo;
//import cn.edu.zju.gislab.SZTDService.po.UserinfoExample;
//import cn.edu.zju.gislab.SZTDService.service.UserService;
import cn.edu.zju.gislab.SCSServices.mapper.ScsUsersMapper;
import cn.edu.zju.gislab.SCSServices.po.ScsUsers;
import cn.edu.zju.gislab.SCSServices.po.ScsUsersExample;
import cn.edu.zju.gislab.SCSServices.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImp implements UserService {

	@Autowired
	private ScsUsersMapper usersMapper;

	//登录验证
	@Override
	public int checkLogin(String username, String password) {
		ScsUsersExample usersExample = new ScsUsersExample();
		// 通过criteria构造查询条件
		ScsUsersExample.Criteria criteria = usersExample.createCriteria();
		criteria.andUsernameEqualTo(username);
		criteria.andPasswordEqualTo(password);

		List<ScsUsers> userList = usersMapper.selectByExample(usersExample);
		if(userList != null && userList.size() > 0) {
			return userList.get(0).getGroupId().intValue();
		}
		return -1;
	}

	@Override
	public List<ScsUsers> getAllUsers() {
		ScsUsersExample usersExample = new ScsUsersExample();
		List<ScsUsers> userList = usersMapper.selectByExample(usersExample);
		return userList;
	}

	@Override
	public int updateUser(String username, String password, int groupId) {
		ScsUsers user = new ScsUsers();
		user.setUsername(username);
		user.setPassword(password);
		user.setGroupId(groupId);
		usersMapper.updateByPrimaryKey(user);

		return 1;
	}

	@Override
	public int addUser(String username, String password, int groupId) {
		ScsUsersExample usersExample = new ScsUsersExample();
		// 通过criteria构造查询条件
		ScsUsersExample.Criteria criteria = usersExample.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<ScsUsers> userList = usersMapper.selectByExample(usersExample);
		if(userList != null && userList.size() > 0) {
			return 0;
		}
		else{
			ScsUsers user = new ScsUsers();
			user.setUsername(username);
			user.setPassword(password);
			user.setGroupId(groupId);
			usersMapper.insert(user);
			return 1;
		}
	}

	@Override
	public int deleteUser(String username) {
		usersMapper.deleteByPrimaryKey(username);
		return 1;
	}

}
