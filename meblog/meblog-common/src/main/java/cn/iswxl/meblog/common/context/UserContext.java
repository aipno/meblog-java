package cn.iswxl.meblog.common.context;

/**
 * 用户上下文，使用 ThreadLocal 存储当前线程的用户ID
 */
public class UserContext {
    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> TOKEN_HOLDER = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    public static Long getUserId() {
        return USER_ID_HOLDER.get();
    }
    
    public static void setUsername(String username) {
        USERNAME_HOLDER.set(username);
    }
    
    public static String getUsername() {
        return USERNAME_HOLDER.get();
    }
    
    public static void setToken(String token) {
        TOKEN_HOLDER.set(token);
    }
    
    public static String getToken() {
        return TOKEN_HOLDER.get();
    }


    public static void remove() {
        USER_ID_HOLDER.remove();
        USERNAME_HOLDER.remove();
        TOKEN_HOLDER.remove();
    }
    
    public static boolean isAuthenticated() {
        return getUserId() != null && getUsername() != null;
    }
    
    public static void clearAll() {
        remove();
    }
}