package cn.edu.zju.gislab.SCSServices.service;


import cn.edu.zju.gislab.SCSServices.po.ScsUsers;

import java.util.List;
import java.util.Map;

public interface UserService {
	//登录验证
	int checkLogin(String username, String password);
	List<ScsUsers> getAllUsers();
	int updateUser(String username,String password,int groupId);
	int addUser(String username,String password,int groupId);
	int deleteUser(String username);
}
