package cn.iswxl.meblog.jwt.service.impl;

import cn.iswxl.meblog.common.domain.dos.UserDO;
import cn.iswxl.meblog.common.domain.mapper.UserMapper;
import cn.iswxl.meblog.common.exception.BizException;
import cn.iswxl.meblog.common.enums.ResponseCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl { // 不再实现 UserDetailsService

    @Autowired
    private UserMapper userMapper;

    /**
     * 普通的根据用户名查询用户方法
     */
    public UserDO findUserByUsername(String username) {
        // 从数据库中查询
        UserDO userDO = userMapper.findByUsername(username);

        // 判断用户是否存在
        if (Objects.isNull(userDO)) {
            throw new BizException(ResponseCodeEnum.USERNAME_NOT_FOUND); // 需确保你有这个枚举，或者用类似语义
        }

        return userDO;
    }
}