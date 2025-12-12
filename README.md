# Meblog - 前后端分离博客系统

## 项目概述
Meblog 是一个基于 Spring Boot 开发的前后端分离博客系统，提供了完整的博客文章管理、用户认证、全文搜索、文件存储等功能。

## 技术栈

### 后端技术
| 技术                     | 版本          | 说明                     |
|--------------------------|---------------|--------------------------|
| Spring Boot              | 2.6.3         | 后端框架                 |
| Java                     | 17            | 开发语言                 |
| MyBatis Plus             | 3.5.2         | ORM框架                  |
| MySQL                    | 8.x           | 关系型数据库             |
| Redis                    | -             | 缓存数据库               |
| Minio                    | 8.2.1         | 对象存储服务             |
| Lucene                   | 8.11.1        | 全文搜索引擎             |
| JWT                      | 0.11.2        | 认证授权                 |
| SpringDoc OpenAPI        | 1.6.14        | API文档生成              |
| Lombok                   | 1.18.28       | Java代码简化工具         |
| MapStruct                | 1.5.5.Final   | 对象映射工具             |
| CommonMark               | 0.20.0        | Markdown解析库           |

## 项目结构

```
meblog/
├── meblog-web/          # 入口模块（启动模块）
├── meblog-admin/        # 管理后台模块
├── meblog-common/       # 通用模块（工具类、常量等）
├── meblog-jwt/          # JWT认证模块
├── meblog-search/       # 搜索模块（基于Lucene）
└── pom.xml              # 父项目配置文件
```

## 核心功能

### 1. 博客文章管理
- 文章发布、编辑、删除
- Markdown格式支持
- 分类与标签管理
- 文章状态管理（草稿、发布、归档）

### 2. 用户认证与授权
- JWT Token认证
- 权限管理
- 注册与登录
- 密码重置（邮件验证码）

### 3. 全文搜索
- 基于Lucene的高效搜索
- 中文分词支持
- 关键词高亮
- 搜索结果排序

### 4. 文件管理
- 基于Minio的对象存储
- 图片上传与预览
- 文件大小限制
- 支持多种文件类型

### 5. 其他功能
- 邮件发送服务
- 定时任务支持
- 数据统计与分析
- API文档自动生成

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.x
- Redis 5.x+
- Minio 8.x

## 安装与配置

### 1. 克隆项目

```bash
git clone https://github.com/aipno/meblog-java.git
cd meblog-java
```

### 2. 配置数据库

编辑 `meblog/meblog-web/src/main/resources/application-dev.yml` 文件，修改数据库连接信息：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/meblog?useUnicode=true&characterEncoding=UTF-8
    username: your-username
    password: your-password
```

### 3. 配置Redis

在同一配置文件中修改Redis连接信息：

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your-redis-password
```

### 4. 配置Minio

```yaml
minio:
  endpoint: http://localhost:9000
  accessKey: your-minio-access-key
  secretKey: your-minio-secret-key
  bucketName: meblog
```

### 5. 配置邮件服务

```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 465
    username: your-email@example.com
    password: your-email-password
    protocol: smtps
```

### 6. 构建项目

```bash
mvn clean install
```

### 7. 启动应用

```bash
java -jar meblog/meblog-web/target/meblog-web-0.7.0.jar
```

## 使用指南

### 访问API文档

启动应用后，可通过以下地址访问API文档：

```
http://localhost:8080/swagger-ui.html
```

### 管理后台

待补充

### 前端访问

待补充

## API文档

项目使用 SpringDoc OpenAPI 自动生成API文档，包含所有接口的详细信息，包括请求参数、响应格式、错误码等。

### 主要API端点

- 用户管理：`/api/user/*`
- 文章管理：`/api/article/*`
- 分类管理：`/api/category/*`
- 标签管理：`/api/tag/*`
- 搜索功能：`/api/search/*`
- 文件上传：`/api/upload/*`

## 贡献规范

1. 提交代码前请确保通过所有测试
2. 遵循项目现有的代码风格（使用Lombok、MapStruct等）
3. 提交PR时请详细描述更改内容和目的
4. 确保代码注释清晰，便于理解和维护
5. 新增功能请添加相应的测试用例

## 许可证信息

项目采用 **GNU Affero General Public License v3.0** 许可证，详情请查看 [LICENSE](LICENSE) 文件。

## 联系方式

- 项目作者：艾普诺
- 项目地址：https://github.com/aipno/meblog-java
- 问题反馈：https://github.com/aipno/meblog-java/issues

## 版本历史

- v0.7.0 (当前版本)：初始版本，包含基本博客功能

## 致谢

感谢所有为项目做出贡献的开发者和支持者！

## 免责声明

本项目仅供学习和参考使用，请勿用于商业用途。使用本项目产生的任何后果，项目作者不承担任何责任。