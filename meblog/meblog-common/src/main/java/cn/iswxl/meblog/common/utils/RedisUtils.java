package cn.iswxl.meblog.common.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 */
@Component
@AllArgsConstructor
@Slf4j
public class RedisUtils {

    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 普通获取键对应值
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        try {
            return key == null ? null : redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis get error, key: {}", key, e);
            return null;
        }
    }

    /**
     * 普通设置键值
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            if (key == null) {
                return false;
            }
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("Redis set error, key: {}", key, e);
            return false;
        }
    }

    /**
     * 普通设置键值并设置过期时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (key == null) {
                return false;
            }
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("Redis set error, key: {}, time: {}", key, time, e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 键
     */
    public void del(String key) {
        try {
            if (key == null) {
                return;
            }
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis delete error, key: {}", key, e);
        }
    }

    /**
     * 指定缓存的失效时间
     *
     * @param key  键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0 && key != null) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Redis expire error, key: {}, time: {}", key, time, e);
            return false;
        }
    }

}
