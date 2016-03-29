package truecolor.mdwb.model;

/**
 * Created by xiaowu on 15/8/16.
 */
public class Oauth2AccessToken {
    private String accessToken = "";
    private String refreshToken = "";
    private long expiresTime = 0L;
    private String uid = "";

    public Oauth2AccessToken(){}

    public Oauth2AccessToken(String accessToken, long expiresTime, String uid){
        this.accessToken = accessToken;
        this.expiresTime = expiresTime;
        this.uid = uid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(long expiresTime) {
        this.expiresTime = expiresTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
