package cn.iswxl.meblog.jwt.service;

import cn.iswxl.meblog.jwt.model.certification.LoginReqVO;
import cn.iswxl.meblog.jwt.model.certification.LoginRspVO;
import cn.iswxl.meblog.jwt.model.certification.RegisterReqVO;

/**
 * 登录授权服务接口
 */
public interface AuthService {

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

