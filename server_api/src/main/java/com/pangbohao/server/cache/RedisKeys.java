/*
 * *
 *
 *     Created by OuYangX.
 *     Copyright (c) 2016, ouyangxian@gmail.com All Rights Reserved.
 *
 * /
 */

package com.pangbohao.server.cache;

public interface RedisKeys {
	
	
 	/**********************************1.String --字符串对象**************************/
	/**
	 * 订单免运费需要的额度
	 */
	String ORDER_INCREASE_LIMIT_AMOUNT = "order_limit_amount";
	/**
	 * 订单会员宝提示增长额度
	 */
	String ORDER_INCREASE_AMOUNT = "huiyuanbao_increase_amount";
	/**
	 * 	card_certification_limit:userId:{userId} //认证身份次数*

	 */
	String CARD_CERTIFICATION_LIMIT = "card_certification_limit:userId:%s";//认证身份次数*
	/**
	 *  	idcard_validate_limit:userId:{userId} //验证身份次数*
	 */
	String IDCARD_VALIDATE_LIMIT = "idcard_validate_limit:userId:%s";//验证身份次数*
	/**
	 *  	bind_bankCard_limit:userId:{userId} ///绑定银行卡次数*
	 */
	String BIND_BANKCARD_LIMIT = "bind_bankCard_limit:userId:%s";//绑定银行卡次数*
	/**
	 * change_bankCard_limit:userId:{userId} //更换绑定银行卡次数*
	 */
	String CHANGE_BANKCARD_LIMIT = "change_bankCard_limit:userId:%s";//更换绑定银行卡次数*
	/**
	 * 	etag:uri:{uri}-{regionId}-{token}     //缓存HTTP请求的etag信息
	 */
	String ETAG_URI = "etag:uri:%s";
	/**
	 * 	cart:token:{token}:openid            //缓存微信用户openid 
	 */
	String CART_TOKEN_OPENID="cart:token:{0}:openid";
	/**
	 * 	smscode:{phone}用户验证码
	 */
	String SMSCODE_PHONE = "smscode:{0}";
	/**
	 * 	paysetting:{token} 缓存用户付款设置
	 */
	String PAYSETTING_TOKEN = "paysetting:{0}";
	/**
	 * {appId}:{cachedSmscodeType}:{phone} 根据类型缓存验证码
	 */
	String CACHED_SMSCODE_TYPE_PHONE = "{0}:{1}:{2}";
	
	/**
	 * {appId}:sendtimes:{cachedSmscodeType}:{phone}  验证码发送次数
	 */
	String SENDTIMES_SMSCODE_BYTYPE="{0}:sendtimes:{1}:{2}";

	/**
	 * session:{token} 用户登录TOKEN
	 */
	String SESSION_TOKEN = "session:{0}";
	
	/**********************************2.List --列表对象**********************************/
	/**
	 * o2o:redis:sync:message:queue  基于redis的消息队列
	 */
	String O2O_REDIS_SYNC_MESSAGE_QUEUE="o2o:redis:sync:message:queue";
	/**********************************3.Hash --哈希对象**********************************/
	/**
	 * key= user:{userId}; hashkey=balance //我的余额
	 */
	String USER_USERID_KEY="user:{0}"; String  USER_USERID_BALANCE_HASHKEY="balance";

	
	/**********************************4.Set --集合对象***********************************/
	/**
	 * 	etag:member:set //缓存ETagCache的key集合
	 */
	String ETAG_MEMBER_SET="etag:member:set";
	/**********************************5.Zset --有序集合对象**********************************/

	/**
	 * session:{token} 用户登录TOKEN
	 */
	String TOKEN_STR = "session:{0}";

}

