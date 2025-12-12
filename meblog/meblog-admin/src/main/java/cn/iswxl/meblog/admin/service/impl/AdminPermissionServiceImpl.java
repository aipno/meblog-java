package cn.iswxl.meblog.admin.service.impl;

import cn.iswxl.meblog.admin.model.vo.permission.FindPermissionListRspVO;
import cn.iswxl.meblog.admin.service.AdminPermissionService;
import cn.iswxl.meblog.common.domain.dos.PermissionDO;
import cn.iswxl.meblog.common.domain.dos.PermissionParentDO;
import cn.iswxl.meblog.common.domain.dos.RolePermissionDO;
import cn.iswxl.meblog.common.domain.mapper.PermissionMapper;
import cn.iswxl.meblog.common.domain.mapper.PermissionParentMapper;
import cn.iswxl.meblog.common.domain.mapper.RolePermissionMapper;
import cn.iswxl.meblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AdminPermissionServiceImpl implements AdminPermissionService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private PermissionParentMapper permissionParentMapper;


    /**
     * 获取角色权限列表
     * <p>
     * 该方法用于查询指定角色拥有的权限及其父级分类信息。首先获取所有权限父级，
     * 再查询角色拥有的权限，然后遍历每个权限父级，查询其下的权限并判断是否属于该角色，最终构造成树状结构返回。
     * @param roleId
     * @return
     */
    @Override
    public Response findPermissionWithParentList(Long roleId) {
        // 查询所有权限父级
        List<PermissionParentDO> permissionParentDOS = permissionParentMapper.selectList(null);

        // 查询角色拥有的权限
        List<RolePermissionDO> rolePermissionDOS = rolePermissionMapper.selectRolePermissions(roleId);
        System.out.println("rolePermissionDOS = " + rolePermissionDOS);
        // 构建返回结果
        List<FindPermissionListRspVO> vos = new ArrayList<>();

        if (!CollectionUtils.isEmpty(permissionParentDOS)) {
            for (PermissionParentDO permissionParentDO : permissionParentDOS) {
                // 查询该父级下的所有权限
                List<PermissionDO> permissionDOS = permissionMapper.selectByParentId(Math.toIntExact(permissionParentDO.getId()));

                // 构建权限列表
                List<FindPermissionListRspVO.PermissionListRspVO> permissionList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(permissionDOS)) {
                    for (PermissionDO permissionDO : permissionDOS) {
                        // 判断该权限是否属于当前角色
                        boolean hasPermission = rolePermissionMapper.isStatus(roleId, permissionDO.getId());
                        System.out.println("hasPermission = " + hasPermission);

                        permissionList.add(FindPermissionListRspVO.PermissionListRspVO.builder()
                                .permissionId(permissionDO.getId())
                                .permissionName(permissionDO.getPermName())
                                .status(hasPermission)
                                .permissionDesc(permissionDO.getDescription())
                                .build());
                    }
                }

                // 构建返回VO
                FindPermissionListRspVO vo = FindPermissionListRspVO.builder()
                        .permissionParentName(permissionParentDO.getPermParentName())
                        .permissionList(permissionList)
                        .build();

                vos.add(vo);
            }
        }

        return Response.success(vos);
    }
}

