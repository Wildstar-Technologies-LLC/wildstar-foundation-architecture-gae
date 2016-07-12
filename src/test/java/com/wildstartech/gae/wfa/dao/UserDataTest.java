package com.wildstartech.gae.wfa.dao;

import org.testng.annotations.Test;

import com.wildstartech.wfa.dao.UserData;

public class UserDataTest {
	@Test
	public void getAdminUser() {
		String userName="";
		String password="";
		UserData userData=null;
		
		userData=UserData.getInstance();
		userName=userData.getAdminUserName();
	}
}