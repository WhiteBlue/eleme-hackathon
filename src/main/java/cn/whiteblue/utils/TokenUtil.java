package cn.whiteblue.utils;

import java.util.UUID;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/9
 */
public class TokenUtil {

    /**
     * 随机UUID=>token
     *
     * @return String
     */
    public static String generate() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }

    /**
     * 根据UserId出UUID=>token
     *
     * @param userId 用户id
     * @return String
     */
    public static String generate(String userId) {
        UUID uuid = UUID.nameUUIDFromBytes(userId.getBytes());
        return uuid.toString().replace("-", "");
    }

}
