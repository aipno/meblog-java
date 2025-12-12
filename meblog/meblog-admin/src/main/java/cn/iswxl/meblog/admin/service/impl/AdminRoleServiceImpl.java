package cn.iswxl.meblog.admin.service.impl;

import cn.iswxl.meblog.admin.model.vo.role.ChangeRolePermissionReqVO;
import cn.iswxl.meblog.admin.model.vo.user.ChangeUserRoleReqVO;
import cn.iswxl.meblog.admin.model.vo.role.FindRoleUserInfoListReqVO;
import cn.iswxl.meblog.admin.model.vo.role.FindRoleUserInfoListRspVO;
import cn.iswxl.meblog.admin.service.AdminRoleService;
import cn.iswxl.meblog.common.domain.mapper.RolePermissionMapper;
import cn.iswxl.meblog.common.domain.mapper.UserMapper;
import cn.iswxl.meblog.common.domain.mapper.UserRoleMapper;
import cn.iswxl.meblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AdminRoleServiceImpl implements AdminRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public List<FindRoleUserInfoListRspVO> findRoleUserInfoList(FindRoleUserInfoListReqVO reqVO) {
        Long roleId = reqVO.getRoleId();
        List<Long> UserIdList = userRoleMapper.selectUserIdsByRoleId(roleId);
        return List.of(UserIdList.stream().map(userId ->
                FindRoleUserInfoListRspVO.builder()
                        .userId(userId)
                        .username(userMapper.selectById(userId).getUsername())
                        .nickname(userMapper.selectById(userId).getNickname())
                        .build()
        ).toArray(FindRoleUserInfoListRspVO[]::new));
    }

    @Override
    public Response changeUserRole(ChangeUserRoleReqVO reqVO) {
        String username = reqVO.getUsername();
        Long userId = userMapper.findByUsername(username).getId();
        Long roleId = reqVO.getRoleId();

        userRoleMapper.changeUserRole(userId,roleId);
        return Response.success();
    }

    @Override
    public Response changeRolePermission(ChangeRolePermissionReqVO reqVO) {
        rolePermissionMapper.updatePermissions(reqVO.getRoleId(),reqVO.getPermissionIds());
        return Response.success();
    }
}
