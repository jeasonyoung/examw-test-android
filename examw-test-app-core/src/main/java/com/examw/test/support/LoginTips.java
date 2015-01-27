package com.examw.test.support;

/**
 * 
 * @author fengwei.
 * @since 2015年1月9日 上午11:18:29.
 */
public class LoginTips {
	/**
	 * 1、提交数据错误！ （MD5校验失败）
	 * 2、登录错误次数已达上限，请过段时间再试或找回密码（上限5次） 
	 * 3、用户名长度只能在4~20个之间!
	 * 4、密码长度只能在6~20个之间! 
	 * 5、用户名不存在，请先注册
	 * 
	 * +1、密码错误次数+1；
	 * -1、用户名不存在，或被删除！ 
	 * -2、登录密码错误！ 
	 * -3、登录失败、未知错误！ 
	 * -4、用户名重复，请联系客服
	 */
	public static String getLoginTip(int code,String other)
	{
		switch(code)
		{
		case 1:
			return "提交数据错误";
		case 2:
			return "登录错误次数已达上限，请过段时间再试或找回密码";
		case 3:
			return "用户名长度只能在4~20个之间";
		case 4:
			return "密码长度只能在6~20个之间";
		case 5:
			return "用户名不存在，请先注册";
		case -1:
			return "用户名不存在，或被删除";
		case -2:
			return "登录密码错误";
		case -3:
			return "登录失败、未知错误";
		case -4:
			return "用户名重复，请联系客服";
		default:
			return "登陆成功";
		}
	}
	/*
	 * 	1、未知提交方式client！
		2、提交数据错误！  （实际上是MD5验证不一致）
		3、用户名长度只能在4~20个之间!
		4、密码长度只能在4~20个之间!
		5、两次输入密码不相同！
		6、姓名太长或太短了！
		7、请输入中文姓名！
		8、手机号码不正确！（超过15位就会报错）
		9、注册异常请联系管理员！
		10、用户名已经注册！

		-1、用户名不合法！
		-2、用户名不允许注册的词语！
		-3、用户名已经存在！
		-4、Email 格式有误！
		-5、Email 不允许注册！
		-6、该 Email 已经被注册！
		-7、注册信息不全！
	 */
	public static String getRegisterTip(int code)
	{
		switch(code)
		{
		case 1:
			return "未知提交方式client";
		case 2:
			return "提交数据错误";
		case 3:
			return "用户名长度只能在4~20个之间";
		case 4:
			return "密码长度只能在6~20个之间";
		case 5:
			return "两次输入密码不相同";
		case 6:
			return "姓名太长或太短了";
		case 7:
			return "请输入中文姓名";
		case 8:
			return "手机号码不正确";
		case 9:
			return "注册异常请联系管理员";
		case 10:
			return "用户名已经注册";
		case -1:
			return "用户名不合法";
		case -2:
			return "用户名不允许注册的词语";
		case -3:
			return "用户名已经存在";
		case -4:
			return "Email 格式有误";
		case -5:
			return "Email 不允许注册";
		case -6:
			return "该 Email 已经被注册";
		case -7:
			return "注册信息不全";
		default:
			return "注册成功";
		}
	}
}
