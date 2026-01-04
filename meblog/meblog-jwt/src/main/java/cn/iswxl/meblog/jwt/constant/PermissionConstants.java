package cn.iswxl.meblog.jwt.constant;

public interface PermissionConstants {

    // 模块：文章
    interface Article {
        String BASE = "admin:article";
        String PUBLISH = "publish";
        String UPDATE = "update";
        String DELETE = "delete";
        // 获取文章分页数据
        String LIST = "list";
        String DETAIL = "detail";
        String IS_PUBLISH_UPDATE = "status:update";
        String IS_PERMISSION_UPDATE = "permission:update";
    }

    // 模块：分类
    interface Category {
        String BASE = "admin:category";
        String LIST = "list";
        String CREATE = "create";
        String UPDATE = "update";
        String DELETE = "delete";
    }

    // 模块：标签
    interface Tag {
        String BASE = "admin:tag";
        String CREATE = "create";
        String UPDATE = "update";
        String DELETE = "delete";
        String LIST = "list";
        String PAGE = "page";
        String SEARCH = "search";
    }

    // 模块：博客设置信息
    interface BlogSettings {
        String BASE = "admin:blog:settings";
        String UPDATE = "update";
        String DETAIL = "detail";
    }

    // 模块：知识库
    interface Wiki {
        String BASE = "admin:wiki";
        String CREATE = "create";
        String UPDATE = "update";
        String DELETE = "delete";
        String LIST = "list";
        String IS_TOP_UPDATE = "istop:update";
        String IS_PUBLISH_UPDATE = "isPublish:update";
        String CATALOG_LIST = "catalog:list";
        String CATALOG_UPDATE = "catalog:update";
    }

    // 模块：文件
    interface File {
        String BASE = "admin:image";
        String UPLOAD = "upload";
        String LIST = "list";
        String IMAGE_LIST = "image:list";
        String PAGE = "page";
        String DELETE = "delete";
    }

    // 模块：用户
    interface User {
        String BASE = "admin:user";
        String LIST = "list";
        String OTHER = "other";
        String UPDATE_PASSWD = "update:password";
    }

    // 模块：权限
    interface Permission {
        String BASE = "admin:permission";
        String LIST = "list";
        String OTHER = "other";
    }

    // 模块：角色
    interface Role {
        String BASE = "admin:role";
        String LIST = "list";
        String LIST_ALL = "list:all";
        String UPDETE_PERMISSION = "updete:permission";
    }

    // 模块：仪表盘
    interface Dashboard {
        String BASE = "admin:dashboard";
        String STATISTICS = "statistics";
        String PUBLISH_ARTICLE_STATISTICS = "publishArticle:statistics";
        String PV_STATISTICS = "pv:statistics";
    }

}
