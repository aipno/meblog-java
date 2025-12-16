package cn.iswxl.meblog.admin.service.impl;

import cn.iswxl.meblog.admin.model.vo.user.FindUserInfoListRspVO;
import cn.iswxl.meblog.admin.model.vo.user.FindUserInfoRspVO;
import cn.iswxl.meblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import cn.iswxl.meblog.admin.service.AdminUserService;
import cn.iswxl.meblog.common.context.UserContext;
import cn.iswxl.meblog.common.domain.dos.UserDO;
import cn.iswxl.meblog.common.domain.mapper.BlogSettingsMapper;
import cn.iswxl.meblog.common.domain.mapper.RoleMapper;
import cn.iswxl.meblog.common.domain.mapper.UserMapper;
import cn.iswxl.meblog.common.domain.mapper.UserRoleMapper;
import cn.iswxl.meblog.common.enums.ResponseCodeEnum;
import cn.iswxl.meblog.common.exception.BizException;
import cn.iswxl.meblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import cn.iswxl.meblog.jwt.config.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BlogSettingsMapper blogSettingsMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;

    /**
     * 修改密码（用户修改自己密码）
     *
     * @param updateAdminUserPasswordReqVO
     * @return
     */
    @Override
    public Response updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) {
        // 获取当前登录用户
        String currentUsername = UserContext.getUsername();
        
        // 验证旧密码
        String oldPassword = updateAdminUserPasswordReqVO.getOldPassword();
        if (!StringUtils.hasText(oldPassword)) {
            throw new BizException(ResponseCodeEnum.OLD_PASSWORD_REQUIRED);
        }
        
        // 查询当前用户信息
        UserDO currentUser = userMapper.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new BizException(ResponseCodeEnum.USERNAME_NOT_FOUND);
        }
        
        // 验证旧密码是否正确
        if (!passwordEncoder.passwordEncode().matches(oldPassword, currentUser.getPassword())) {
            throw new BizException(ResponseCodeEnum.OLD_PASSWORD_ERROR);
        }
        
        // 加密新密码
        String encodePassword = passwordEncoder.passwordEncode().encode(updateAdminUserPasswordReqVO.getPassword());
        
        // 更新当前用户的密码
        int count = userMapper.updatePasswordByUsername(currentUsername, encodePassword);
        
        return count == 1 ? Response.success() : Response.fail(ResponseCodeEnum.PASSWORD_UPDATE_FAILED);
    }
    
    /**
     * 重置密码（管理员重置其他用户密码）
     *
     * @param updateAdminUserPasswordReqVO
     * @return
     */
    @Override
    public Response resetPassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) {
        // 拿到用户名、密码
        String username = updateAdminUserPasswordReqVO.getUsername();
        String password = updateAdminUserPasswordReqVO.getPassword();
        
        // 检查用户是否存在
        UserDO userDO = userMapper.findByUsername(username);
        if (userDO == null) {
            throw new BizException(ResponseCodeEnum.USERNAME_NOT_FOUND);
        }
        
        // 加密密码
        String encodePassword = passwordEncoder.passwordEncode().encode(password);
        
        // 更新到数据库
        int count = userMapper.updatePasswordByUsername(username, encodePassword);
        
        return count == 1 ? Response.success() : Response.fail(ResponseCodeEnum.PASSWORD_UPDATE_FAILED);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    @Override
    public Response findUserInfo() {
        // 获取存储在 ThreadLocal 中的用户信息
        String username = UserContext.getUsername();
        // 拿到用户头像
        String avatar = blogSettingsMapper.selectById(1).getAvatar();

        return Response.success(FindUserInfoRspVO.builder()
                .username(username)
                .avatar(avatar)
                .build());
    }

    /**
     * 获取所有用户信息
     *
     * @return
     */
    @Override
    public Response findAllUsers() {
        List<Map<String, Object>> list = userMapper.selectAllUsers();
        List<FindUserInfoListRspVO> userInfoList = list.stream()
                .map(item -> {
                    Long userId = (Long) item.get("id");
                    Long roleId = userRoleMapper.selectUserRole(userId);
                    String role = roleMapper.selectRoleNamesByRoleId(roleId);
                    return FindUserInfoListRspVO.builder()
                            .id(userId)
                            .username((String) item.get("username"))
                            .nickname((String) item.get("nickname"))
                            .role(role)
                            .status((Integer) item.get("status"))
                            .createTime((LocalDateTime) item.get("create_time"))
                            .build();
                })
                .toList();
        return Response.success(userInfoList);
    }

    @Override
    public Response changeUserStatus(Long userId, Integer status) {
        return userMapper.updateUserStatus(userId, status);
    }

}

