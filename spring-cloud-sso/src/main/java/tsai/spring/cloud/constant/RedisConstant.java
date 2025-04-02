package tsai.spring.cloud.constant;
/**
 * Redis 键常量
 * @author tsai
 */
public class RedisConstant {

    /**
     * 登录验证码键名前缀
     */
    public static final String CAPTCHA_KEY_PREFIX = "access_captcha:";

    /**
     * token 前缀
     */
    public static final String OAUTH2_TOKEN_PREFIX = "oauth2_token:";
    public static final String SPRING_SESSION_PREFIX = "spring:session:sso";
}
