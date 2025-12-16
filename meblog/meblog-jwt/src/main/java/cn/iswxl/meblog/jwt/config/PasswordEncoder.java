package cn.iswxl.meblog.jwt.config;

import cn.hutool.crypto.digest.BCrypt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PasswordEncoder {

    @Bean
    public PasswordEncoderService passwordEncode() {
        // BCrypt 是一种安全且适合密码存储的哈希算法，它在进行哈希时会自动加入"盐"，增加密码的安全性。
        return new PasswordEncoderService() {
            @Override
            public String encode(CharSequence rawPassword) {
                return BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                try {
                    return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
                } catch (Exception e) {
                    return false;
                }
            }
        };
    }


    /***
     * 原始密码加密
     */
    public static void main(String[] args) {
        System.out.println(BCrypt.hashpw("tK$eF,mDWUpCEmtLHRcHeD"));
    }
    
    public interface PasswordEncoderService {
        String encode(CharSequence rawPassword);
        boolean matches(CharSequence rawPassword, String encodedPassword);
    }
}
