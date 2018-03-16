package core;

import java.util.ArrayList;

public class Config {
	
	private String admin_name;
	private String admin_mail;
	private ArrayList<User> users;
	private ArrayList<Path> paths;
	
	
	public String getAdmin_name() {
		if(admin_name == null){
			return new String("");
		}
		return admin_name;
	}

	public void setAdmin_name(String admin_name) {
		this.admin_name = admin_name;
	}

	public String getAdmin_mail() {
		if(admin_mail == null){
			return new String("");
		}
		return admin_mail;
	}

	public void setAdmin_mail(String admin_mail) {
		this.admin_mail = admin_mail;
	}

	public ArrayList<User> getUsers() {
		if(users == null){
			this.users = new ArrayList<User>();
		}
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	public ArrayList<Path> getPaths() {
		if(paths == null){
			this.paths =  new ArrayList<Path>();
		}
		return paths;
	}

	public void setPaths(ArrayList<Path> paths) {
		this.paths = paths;
	}
}