package truecolor.mdwb.utils;


/**
 * 用户信息保存为全局以供所有类引用
 */
public final class OauthToken {
	public static  String oauth_token ;
	public static  String oauth_token_secret ;
	public static  String uid ;

	/**
	 * 保存用户信息
	 */
	public OauthToken(String oauth_token,String oauth_token_secret,String uid)
	{
		this.oauth_token=oauth_token;
		this.oauth_token_secret=oauth_token_secret;
		this.uid=uid;
	}

	/**
	 * API地址
	 */
	public static  String getBaseUriAPI(String mod,String act)
	{
		return "http://42.121.113.32/weibo/index.php?app=api&mod="+mod+"&act="+act+"";
	}

}

