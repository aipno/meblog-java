package cn.iswxl.meblog.jwt.service;

import cn.iswxl.meblog.jwt.model.certification.LoginReqVO;
import cn.iswxl.meblog.jwt.model.certification.LoginRspVO;
import cn.iswxl.meblog.jwt.model.certification.RegisterReqVO;

/**
 * 登录授权服务接口
 */
public interface AuthService {

    /**
     * 注册
     *
     * @param registerUserReqVO 认证用户请求信息
     * @return 是否成功
     */
    boolean register(RegisterReqVO registerUserReqVO);


    /**
     * 向指定邮箱发送验证码
     *
     * @param email 邮箱号
     */
    void sendMailCode(String email);

    /**
     * 登录授权
     *
     * @param loginReqVO 认证用户请求信息
     * @return 认证用户返回信息
     */
    LoginRspVO login(LoginReqVO loginReqVO);

    /**
     * 退出登录
     */
    void logout();
}

