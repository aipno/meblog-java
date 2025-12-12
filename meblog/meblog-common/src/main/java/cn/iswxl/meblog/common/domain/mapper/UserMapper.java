package cn.iswxl.meblog.common.domain.mapper;

import cn.iswxl.meblog.common.domain.dos.UserDO;
import cn.iswxl.meblog.common.utils.Response;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<UserDO> {


    /**
     * 根据用户名查找用户信息
     *
     * @param username 用户名
     * @return 用户信息对象，如果未找到则返回null
     */
    default UserDO findByUsername(String username) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, username);
        return selectOne(wrapper);
    }


    /**
     * 根据用户名更新用户密码
     *
     * @param username 用户名
     * @param password 新密码
     * @return 更新记录数
     */
    default int updatePasswordByUsername(String username, String password) {
        LambdaUpdateWrapper<UserDO> wrapper = new LambdaUpdateWrapper<>();
        // 设置要更新的字段
        wrapper.set(UserDO::getPassword, password);
        wrapper.set(UserDO::getUpdateTime, LocalDateTime.now());
        // 更新条件
        wrapper.eq(UserDO::getUsername, username);

        return update(null, wrapper);
    }


    /**
     * 根据用户名更新用户邮箱
     *
     * @param username 用户名
     * @param email    新邮箱
     * @return 更新记录数
     */
    default int updateEmailByUsername(String username, String email) {
        LambdaUpdateWrapper<UserDO> wrapper = new LambdaUpdateWrapper<>();
        // 设置要更新的字段
        wrapper.set(UserDO::getEmail, email);
        wrapper.set(UserDO::getUpdateTime, LocalDateTime.now());
        // 更新条件
        wrapper.eq(UserDO::getUsername, username);

        return update(null, wrapper);
    }


    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱地址
     * @return 是否存在
     */
    default boolean existsByEmail(String email) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getEmail, email);
        return selectCount(wrapper) > 0;
    }

    /**
     * 获取所有用户的 用户ID、用户名、用户昵称和创建时间
     *
     * @return 用户信息列表
     */
    default List<Map<String, Object>> selectAllUsers() {

        return selectMaps(new LambdaQueryWrapper<UserDO>()
                .select(UserDO::getId,
                        UserDO::getUsername,
                        UserDO::getNickname,
                        UserDO::getStatus,
                        UserDO::getCreateTime));
    }


    default Response updateUserStatus(Long userId, Integer status) {
        LambdaUpdateWrapper<UserDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserDO::getId, userId);
        wrapper.set(UserDO::getStatus, status);
        update(null, wrapper);
        return Response.success();
    }
}
