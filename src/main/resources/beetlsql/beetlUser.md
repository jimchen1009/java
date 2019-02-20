sample
===
* 注释

	select #use("cols")# from beetl_user where #use("condition")#

cols
===

	id,name,age,userName,roleId,create_date

updateSample
===

	`id`=#id#,`name`=#name#,`age`=#age#,`userName`=#username#,`roleId`=#roleid#,`create_date`=#createDate#

condition
===

	1 = 1  
	@if(!isEmpty(name)){
	 and `name`=#name#
	@}
	@if(!isEmpty(age)){
	 and `age`=#age#
	@}
	@if(!isEmpty(username)){
	 and `userName`=#username#
	@}
	@if(!isEmpty(roleid)){
	 and `roleId`=#roleid#
	@}
	@if(!isEmpty(createDate)){
	 and `create_date`=#createDate#
	@}
	
queryNewUser
===
    select * from beetl_user order by id desc

queryNewUser$count
===
    select count(1) from beetl_user
    
selectByName
===

	select * from beetl_user where name = #name#
	
