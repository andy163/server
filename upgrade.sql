drop procedure if exists add_auth;  
create procedure add_auth()
BEGIN
		DECLARE done INT;
		DECLARE cur_id,cur_is_active,cur_is_staff,cur_is_superuser,cur_sex INT;
		DECLARE cur_birth,cur_date_joined,cur_email,cur_imgurl,cur_last_login,cur_name,cur_password,cur_token,cur_username VARCHAR(128);
		#声明游标cursor_name（cursor_name是个多行结果集） 
		DECLARE cur CURSOR FOR 
						SELECT `user`.id,birth,date_joined,email,imgurl,is_active,is_staff,is_superuser,last_login,name,password,sex,token,username 
							FROM o2.auth_user user 
								LEFT JOIN o2.so2o_userprofile  profile 
									ON `user`.id=`profile`.user_id ;
	  #将结束标志绑定到游标
		DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 59000;
		#清空auth_user 数据
		TRUNCATE TABLE auth_user;
		#遍历数据结束标志
		#打开游标  
		OPEN cur; 
			#开始循环 
			read_loop: LOOP
				#提取游标里的数据
				FETCH cur INTO cur_id,cur_birth,cur_date_joined,cur_email,cur_imgurl,cur_is_active,cur_is_staff,cur_is_superuser,cur_last_login,cur_name,cur_password,cur_sex,cur_token,cur_username ;
				
				#声明结束的时候
				IF done THEN
				  LEAVE read_loop;
				END IF;
				#插入数据
				INSERT INTO auth_user (id,birth,date_joined,email,headimgurl,is_active,is_staff,is_superuser,last_login,nickname,password,sex,token,username) 
						values (cur_id,cur_birth,cur_date_joined,cur_email,cur_imgurl,cur_is_active,cur_is_staff,cur_is_superuser,cur_last_login,cur_name,cur_password,cur_sex,cur_token,cur_username);
			END LOOP;
		#关闭游标
		CLOSE cur ;  		
END;  
CALL add_auth()  ;