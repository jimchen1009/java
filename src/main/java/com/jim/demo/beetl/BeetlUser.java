package com.jim.demo.beetl;
import java.util.Date;

/* 
* 
* gen by beetlsql 2018-11-06
*/
public class BeetlUser  {
	
	private Integer id ;
	private Integer age ;
	//用户角色
	private Integer roleid ;
	private String name ;
	//用户名称
	private String username ;
	private Date createDate ;
	
	public BeetlUser() {
	}
	
	public Integer getId(){
		return  id;
	}
	public void setId(Integer id ){
		this.id = id;
	}
	
	public Integer getAge(){
		return  age;
	}
	public void setAge(Integer age ){
		this.age = age;
	}
	
	/**用户角色
	*@return 
	*/
	public Integer getRoleid(){
		return  roleid;
	}
	/**用户角色
	*@param  roleid
	*/
	public void setRoleid(Integer roleid ){
		this.roleid = roleid;
	}
	
	public String getName(){
		return  name;
	}
	public void setName(String name ){
		this.name = name;
	}
	
	/**用户名称
	*@return 
	*/
	public String getUsername(){
		return  username;
	}
	/**用户名称
	*@param  username
	*/
	public void setUsername(String username ){
		this.username = username;
	}
	
	public Date getCreateDate(){
		return  createDate;
	}
	public void setCreateDate(Date createDate ){
		this.createDate = createDate;
	}
	

}
