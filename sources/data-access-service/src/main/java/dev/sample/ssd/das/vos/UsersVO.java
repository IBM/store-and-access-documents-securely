package dev.sample.ssd.das.vos;

import java.util.ArrayList;

import org.json.simple.JSONArray;

public class UsersVO {
	
	ArrayList<UserVO> usersList = new ArrayList<UserVO>();
	
	public UsersVO() {
		
	}
	public UsersVO(ArrayList<UserVO> users) {
		this.usersList = users;
	}
	
	public void addUser(UserVO userVO){
		this.usersList.add(userVO);
	}
	
	public JSONArray toJSONArray() {
		JSONArray usersJSONArray = new JSONArray();
		for(int i = 0; i < this.usersList.size(); i++ ) {
			UserVO userVO = this.usersList.get(i);
			usersJSONArray.add(userVO.toJSON());
		}
		return usersJSONArray;
	}

}
