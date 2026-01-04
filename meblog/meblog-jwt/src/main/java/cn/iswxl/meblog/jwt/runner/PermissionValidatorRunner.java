package cn.iswxl.meblog.jwt.runner;

import cn.iswxl.meblog.common.domain.dos.PermissionDO;
import cn.iswxl.meblog.common.domain.mapper.PermissionMapper;
import cn.iswxl.meblog.jwt.annotation.RequiresPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 启动时校验权限配置完整性
 */
@Component
@Slf4j
public class PermissionValidatorRunner implements CommandLineRunner {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始校验权限配置完整性...");

        // 1. 扫描代码中定义的所有权限
        Set<String> codePermissions = new HashSet<>();
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();

        map.forEach((info, handlerMethod) -> {
            // 获取类上的注解
            RequiresPermission classAnno = handlerMethod.getBeanType().getAnnotation(RequiresPermission.class);
            String basePerm = (classAnno != null && classAnno.value().length > 0) ? classAnno.value()[0] : "";

            // 获取方法上的注解
            RequiresPermission methodAnno = handlerMethod.getMethodAnnotation(RequiresPermission.class);

            if (methodAnno != null) {
                for (String p : methodAnno.value()) {
                    String fullPerm = (p.contains(":") || basePerm.isEmpty()) ? p : basePerm + ":" + p;
                    codePermissions.add(fullPerm);
                }
            }
        });

        // 2. 查询数据库中存在的所有权限
        List<PermissionDO> dbPermissionList = permissionMapper.selectList(null);
        Set<String> dbPermissions = dbPermissionList.stream()
                .map(PermissionDO::getPermCode)
                .collect(Collectors.toSet());

        // 3. 对比差异
        List<String> missingPermissions = new ArrayList<>();
        for (String codePerm : codePermissions) {
            if (!dbPermissions.contains(codePerm)) {
                missingPermissions.add(codePerm);
            }
        }

        // 4. 自动创建缺失的权限
        if (!missingPermissions.isEmpty()) {
            log.warn("======================================================");
            log.warn("检测到代码中定义了数据库中不存在的权限码，开始自动创建：");
            missingPermissions.forEach(p -> log.warn("- {}", p));
            log.warn("======================================================");

            // 批量创建缺失的权限
            List<PermissionDO> permissionsToCreate = missingPermissions.stream()
                    .map(permCode -> {
                        return PermissionDO.builder()
                                .permCode(permCode)
                                .permName(generatePermName(permCode)) // 生成权限名称
                                .module("blog")
                                .description("自动创建的权限: " + permCode) // 权限描述
                                .build();
                    })
                    .toList();

            // 批量插入到数据库
            for (PermissionDO permission : permissionsToCreate) {
                permissionMapper.insert(permission);
            }

            log.info("成功创建了 {} 个缺失的权限", missingPermissions.size());
        } else {
            log.info("权限校验通过，数据库与代码一致。");
        }
    }

    /**
     * 根据权限码生成权限名称
     */
    private String generatePermName(String permCode) {
        // 将权限码转换为可读的权限名称
        // 例如: user:add -> 用户添加, article:view -> 文章查看
        String[] parts = permCode.split(":");
        if (parts.length >= 2) {
            String resource = parts[0];
            String action = parts[1];

            // 可以根据实际业务需求调整转换逻辑
            String resourceCN = getResourceName(resource);
            String actionCN = getActionName(action);

            return resourceCN + actionCN;
        }
        return permCode;
    }

    /**
     * 获取资源名称的中文描述
     */
    private String getResourceName(String resource) {
        switch (resource) {
            case "user":
                return "用户";
            case "article":
                return "文章";
            case "comment":
                return "评论";
            case "permission":
                return "权限";
            case "role":
                return "角色";
            default:
                return resource;
        }
    }

    /**
     * 获取操作名称的中文描述
     */
    private String getActionName(String action) {
        switch (action) {
            case "add":
                return "添加";
            case "edit":
                return "编辑";
            case "delete":
                return "删除";
            case "view":
                return "查看";
            case "list":
                return "列表";
            default:
                return action;
        }
    }

}