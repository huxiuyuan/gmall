package com.atguigu.gmall.index.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.annotation.GmallCache;
import com.atguigu.gmall.index.feignclient.GmallPmsClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.index.utils.DistributedLock;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author huXiuYuan
 * @Description：谷粒商城首页服务实现层
 * @date 2024/7/7 19:24
 */
@Slf4j
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 分类缓存前缀
     */
    private static final String KEY_PREFIX = "index:cates:";

    /**
     * 分类分布式锁前缀
     */
    private static final String LOCK_PREFIX = "index:cates:lock:";

    /**
     * 查询所有一级分类
     *
     * @return 一级分类
     */
    @Override
    public List<CategoryEntity> queryLv1Categories() {
        ResponseVo<List<CategoryEntity>> categoryResponseVo = this.gmallPmsClient.queryCategoryByParentId(0L);
        return categoryResponseVo.getData();
    }

    /**
     * 根据一级分类id查询二、三级分类
     *
     * @param pid 一级分类id
     * @return 二、三级分类
     */
    @Override
    @GmallCache(prefix = KEY_PREFIX, timeout = 129600, random = 14400, lock = LOCK_PREFIX)
    public List<CategoryEntity> queryLv23CategoriesByPid(Long pid) {
        ResponseVo<List<CategoryEntity>> listResponseVo = this.gmallPmsClient.queryCategoriesWithSubsByPid(pid);
        return listResponseVo.getData();
    }

    /**
     * 根据一级分类id查询二、三级分类
     *
     * @param pid 一级分类id
     * @return 二、三级分类
     */
    public List<CategoryEntity> queryLv23CategoriesByPidV2(Long pid) {
        // 1.先查询缓存，如果缓存命中则直接返回
        String json = this.redisTemplate.opsForValue().get(KEY_PREFIX + pid);
        if (StringUtils.isNotBlank(json)) {
            return JSON.parseArray(json, CategoryEntity.class);
        }

        // 为了防止缓存击穿，添加分布式锁
        RLock fairLock = this.redissonClient.getFairLock(LOCK_PREFIX + pid);
        fairLock.lock();

        try {
            // 在获取锁的过程中，可能有其他请求将数据放入缓存中，需要再次确认缓冲中有没有
            json = this.redisTemplate.opsForValue().get(KEY_PREFIX + pid);
            if (StringUtils.isNotBlank(json)) {
                return JSON.parseArray(json, CategoryEntity.class);
            }

            // 2.如果缓存没有命中，调用远程接口获取数据，放入缓存
            ResponseVo<List<CategoryEntity>> listResponseVo = this.gmallPmsClient.queryCategoriesWithSubsByPid(pid);
            List<CategoryEntity> categoriesEntityList = listResponseVo.getData();
            if (!CollectionUtils.isEmpty(categoriesEntityList)) {
                // 为了防止缓存雪崩，给缓存时间添加随机值
                this.redisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoriesEntityList), 90 + new Random().nextInt(10), TimeUnit.DAYS);
            } else {
                // 为了防止缓存穿透，即使数据为null也进行缓存
                this.redisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoriesEntityList), 5, TimeUnit.MINUTES);
            }
            return categoriesEntityList;
        } finally {
            // 解锁
            fairLock.unlock();
        }
    }

    /**
     * 本地锁测试
     * 使用ab压力测试工具：ab -n 5000 -c 100 http://192.168.247.1:8888/index/test_local_lock(请求总次数5000，请求并发数100)
     *      一个实例时：redis中num的值为5000
     *      三个实例时：redis中num的值为2835
     *                注：多个实例时，每次结果可能都不一致，因为加了synchronized后可以保证每个实例中的请求不会产生并发问题，但是不能保证每个实例间不会产生并发问题
     *                   所以redis中的值应该为 请求总次数/实例数量 - 请求总次数，因为最多所有实例同时并发，极端清况下所有请求都没有产生并发问题
     *
     * @return
     */
    @Override
    public synchronized void testLocalLock() {
        // 获取redis中num的值
        String num = this.redisTemplate.opsForValue().get("num");
        if (StringUtils.isBlank(num)) {
            this.redisTemplate.opsForValue().set("num", "1");
            return;
        }
        int value = Integer.parseInt(num);
        // num值++，并重新设置给redis中的num
        this.redisTemplate.opsForValue().set("num", String.valueOf(++value));
    }

    /**
     * 分布式锁(基于redis) 版本1
     *      使用redis的[setnx key value]指令实现锁的独占排他
     *      setnx lock 1：如果redis中不存在key为lock的数据，将lock的值设置为1返回成功，如果存在则返回失败
     *
     * 使用ab压力测试工具：ab -n 5000 -c 100 http://192.168.247.1:8888/index/test_distributed_lock1
     *      三个实例时：redis中num的值为5000
     *
     * @return
     */
    @Override
    public void testDistributedLock1() {
        // 获取锁，其实就是往redis中设置一个key，设置成功就是获取锁成功，设置失败则反之
        Boolean lock = this.redisTemplate.opsForValue().setIfAbsent("lock", "1");

        // 如果没有获取到锁
        if (Boolean.FALSE.equals(lock)) {
            // 递归进行重试
            try {
                // 等待50毫秒
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.testDistributedLock1();
        } else {
            String num = this.redisTemplate.opsForValue().get("num");
            if (StringUtils.isBlank(num)) {
                this.redisTemplate.opsForValue().set("num", "1");
                return;
            }
            int value = Integer.parseInt(num);
            this.redisTemplate.opsForValue().set("num", String.valueOf(++value));

            // 释放锁，就是将redis中key为lock的数据删掉
            this.redisTemplate.delete("lock");
        }
    }

    /**
     * 分布式锁(基于redis) 版本2
     *      防止死锁：使用redis的[set key value ex 过期时间(秒) nx]指令给锁设置过期时间，保证加锁和设置过期时间的原子性
     *      set lock 1 ex 3 nx：如果redis中不存在key为lock的数据，将lock的值设置为1、lock的过期时间设置为3秒返回成功，如果存在则返回失败
     *
     *      原因1：如果获取锁之后、释放锁之前，服务宕机，则锁没有被释放造成死锁
     *
     *      在RedisTemplate的底层中：
     *          1.this.redisTemplate.opsForValue().setIfAbsent("lock", "1"); 是通过[setnx key value]指令实现
     *          2.this.redisTemplate.opsForValue().setIfAbsent("lock", "1", 3, TimeUnit.SECONDS); 是通过[set key value ex 过期时间(秒) nx]指令实现
     *
     *      注：为什么不通过[expire key 过期时间(秒)]在加锁成功后给锁添加过期时间
     *              因为在redis中只能保证一条指令的原子性，通过java的setIfAbsent方法和expire方法是依次在redis中执行了两条指令
     *              并且服务在获取锁之后和设置过期时间之前的可能性也不是不存在
     * @return
     */
    @Override
    public void testDistributedLock2() {
        // 获取锁，其实就是往redis中设置一个key并给这个key设置3秒的过期时间，设置成功就是获取锁成功，设置失败则反之
        Boolean lock = this.redisTemplate.opsForValue().setIfAbsent("lock", "1", 3, TimeUnit.SECONDS);

        // 如果没有获取到锁
        if (Boolean.FALSE.equals(lock)) {
            // 递归进行重试
            try {
                // 等待50毫秒
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.testDistributedLock1();
        } else {
            // 不可取
            // this.redisTemplate.expire("lock", 3, TimeUnit.SECONDS);

            String num = this.redisTemplate.opsForValue().get("num");
            if (StringUtils.isBlank(num)) {
                this.redisTemplate.opsForValue().set("num", "1");
                return;
            }
            int value = Integer.parseInt(num);
            this.redisTemplate.opsForValue().set("num", String.valueOf(++value));

            // 释放锁，就是将redis中key为lock的数据删掉
            this.redisTemplate.delete("lock");
        }
    }

    /**
     * 分布式锁(基于redis) 版本3
     *      防误删：为每一个请求生成一个UUID作为redis中key的值，来保证每一个锁的key-value唯一，然后在删除时判断这个key的值是否等于这个UUID
     *
     *      原因：由于key的过期时间难以把握，可能因为服务器抖动造成请求时间过长或者资源被占用等等，造成请求的执行时间超过了锁的过期时间
     *           所以请求还没执行完锁就过期被删除了
     *
     *           假设现在有a、b、c三个请求，a执行之间为7秒，b为3秒，c为4秒
     *           a获取锁  1---------3------------7 a释放锁
     *                             b获取锁 1---------3---4 b释放锁
     *                                              c获取锁 1----------3 c释放锁
     *           显而易见：当a的锁过期后，b获取了锁，当a执行完删除锁的时候其实释放的是b的锁
     *                   当b的锁过期后，c获取了锁，当b执行完删除锁的时候其实释放的是c的锁
     * @return
     */
    @Override
    public void testDistributedLock3() {
        // 生成UUID
        String uuid = UUID.randomUUID().toString();
        // 获取锁，其实就是往redis中设置一个key，uuid作为key的值，并给这个key设置3秒的过期时间，设置成功就是获取锁成功，设置失败则反之
        Boolean lock = this.redisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS);

        // 如果没有获取到锁
        if (Boolean.FALSE.equals(lock)) {
            // 递归进行重试
            try {
                // 等待50毫秒
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.testDistributedLock1();
        } else {
            String num = this.redisTemplate.opsForValue().get("num");
            if (StringUtils.isBlank(num)) {
                this.redisTemplate.opsForValue().set("num", "1");
                return;
            }
            int value = Integer.parseInt(num);
            this.redisTemplate.opsForValue().set("num", String.valueOf(++value));

            // 如果是自己的锁才能进行释放
            if (StringUtils.equals(uuid, this.redisTemplate.opsForValue().get("lock"))) {
                // 假如在判断是自己的锁后，锁就过期了，其他的请求获取到锁，下面释放的依然是别人的锁，所以要保证判断锁是自己的和释放锁之间的原子性
                // 释放锁，就是将redis中key为lock的数据删掉
                this.redisTemplate.delete("lock");
            }
        }
    }

    /**
     * 分布式锁(基于redis) 版本4
     *      使用lua脚本解决 判断锁是自己的锁 和 释放锁 之间的原子性
     *      脚本：if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return
     *
     * 使用ab压力测试工具：ab -n 5000 -c 100 http://192.168.247.1:8888/index/test_distributed_lock4
     *      三个实例时：redis中num的值为5000
     *
     * @return
     */
    @Override
    public void testDistributedLock4() {
        // 生成UUID
        String uuid = UUID.randomUUID().toString();
        // 获取锁，其实就是往redis中设置一个key，uuid作为key的值，并给这个key设置3秒的过期时间，设置成功就是获取锁成功，设置失败则反之
        Boolean lock = this.redisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS);

        // 如果没有获取到锁
        if (Boolean.FALSE.equals(lock)) {
            // 递归进行重试
            try {
                // 等待50毫秒
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.testDistributedLock1();
        } else {
            String num = this.redisTemplate.opsForValue().get("num");
            if (StringUtils.isBlank(num)) {
                this.redisTemplate.opsForValue().set("num", "1");
                return;
            }
            int value = Integer.parseInt(num);
            this.redisTemplate.opsForValue().set("num", String.valueOf(++value));

            String script = "if redis.call('get', KEYS[1]) == ARGV[1] " +
                    "then " +
                    "   return redis.call('del', KEYS[1]) " +
                    "else " +
                    "   return 0 end";
            this.redisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Collections.singletonList("lock"), uuid);
        }
    }

    /**
     * 分布式锁(基于redis) 版本5
     *      参考最下面的本地锁ReentrantLock的可重入解决方案
     *      我们可以用redis的hash数据类型来存放锁相关数据，lock: { UUID: 锁的进入次数 }，使用UUID来判断获取锁的线程是否为持有锁的线程，使用UUID的值来记录锁的进入次数
     *
     *      一.可重入锁加锁步骤：
     *          1.判断锁是否被占用[exists lock]，如果没有被占用(返回的是0)直接获取锁[hset lock uuid 1]并设置过期时间[expire lock 3]，并返回1代表获取锁成功
     *          2.如果锁被占用(返回的是1)，判断是否当前线程占用了锁[hexists lock uuid]
     *              如果是(返回的是1)则重入[hincrby lock uuid 1]也就是将uuid对应的值加1，并重置过期时间[expire lock 3]，并返回1代表获取锁成功
     *          3.如果锁已经被占用，并且不是当前线程占用了锁，则返回0代表获取锁失败
     *
     *          lua脚本：if redis.call('exists', 'lock') == 0               ----->如果锁没有被占用
     *                      then
     *                          redis.call('hset', 'lock', 'uuid', 1)           ----->获取锁
     *                          redis.call('expire', 'lock', 3)                 ----->给锁设置过期时间
     *                      return 1                                            ----->获取锁成功
     *                  elseif redis.call('hexists', 'lock', 'uuid') == 1  ----->如果是当前线程的锁
     *                      then
     *                          redis.call('hincrby', 'lock', 'uuid', 1)        ----->重入锁，也就是将uuid对应的值加1
     *                          redis.call('expire', 'lock', 3)                 ----->重置过期时间
     *                      return 1                                            ----->获取锁成功
     *                  else                                                ----->如果锁已经被占用，并且不是当前线程占用了锁
     *                      return 0                                            ----->获取锁失败
     *                  end
     *
     *          优化：由于hincrby lock uuid 1指令在lock不存在的时候会先去创建这个key再递增，并且跟hset lock uuid 1的效果一样，所以可以将if和elseif放在一起判断
     *
     *          lua脚本：if redis.call('exists', 'lock') == 0 or redis.call('hexists', 'lock', 'uuid') == 1  ----->如果锁没有被占用，或者是当前线程的锁
     *                  then
     *                      redis.call('hincrby', 'lock', 'uuid', 1)                                            ----->获取锁/重入锁
     *                      redis.call('expire', 'lock', 3)                                                     ----->给锁设置过期时间/重置过期时间
     *                  return 1                                                                                ----->获取锁成功
     *              else                                                                                    ----->如果锁已经被占用，并且不是当前线程占用了锁
     *                  return 0                                                                                ----->获取锁失败
     *              end
     *
     *          lua脚本(参数替换)：
     *               if redis.call('exists', KEYS[1]) == 0 or redis.call('hexists', KEYS[1], ARGV[1]) == 1
     *                  then
     *                      redis.call('hincrby', KEYS[1], ARGV[1], 1)
     *                      redis.call('expire', KEYS[1], ARGV[2])
     *                  return 1
     *               else
     *                  return 0
     *               end
     *
     *               KEYS: lock
     *               ARGV: uuid 3
     *
     *          最终脚本：EVAL "if redis.call('exists', KEYS[1]) == 0 or redis.call('hexists', KEYS[1], ARGV[1]) == 1 then redis.call('hincrby', KEYS[1], ARGV[1], 1) redis.call('expire', KEYS[1], ARGV[2]) return 1 else return 0 end" 1 lock uuid 3
     *
     *      二.可重入锁解锁步骤：
     *          1.判断锁是否存在或者是否是当前线程的锁[hexists lock uuid]，如果不是(返回的是0)则返回nil
     *          2.判断uuid对应的值减1后是否为0，如果为0则释放锁并返回1
     *          3.如果不为0则返回0
     *
     *          lua脚本：
     *              if redis.call('hexists', 'lock', 'uuid') == 0           ----->判断锁是否存在或者是否是当前线程的锁
     *                  then
     *                  return nil                                              ----->如果不存在或者不是当前线程的锁，返回null代表释放锁失败
     *              elseif redis.call('hincrby', 'lock', 'uuid', -1) == 0   ----->判断uuid对应的值减1后是否为0
     *                  then
     *                  return redis.call('del', 'lock')                        ----->如果是0，删除锁，返回删除锁的结果(0/1)
     *              else                                                    ----->如果是当前县城的锁并且uuid的值减1后不为0
     *                  return 0                                                ----->返回0，代表重入次数减1成功
     *              end
     *
     *          lua脚本(参数替换)：
     *              if redis.call('hexists', KEYS[1], ARGV[1]) == 0
     *                  then
     *                  return nil
     *              elseif redis.call('hincrby', KEYS[1], ARGV[1], -1) == 0
     *                  then
     *                  return redis.call('del', KEYS[1])
     *              else
     *                  return 0
     *              end
     *
     *              KEYS: lock
     *              ARGV: uuid
     *
     *          最终脚本：EVAL "if redis.call('hexists', KEYS[1], ARGV[1]) == 0 then return nil elseif redis.call('hincrby', KEYS[1], ARGV[1], -1) == 0 then return redis.call('del', KEYS[1]) else return 0 end" 1 lock uuid
     *
     * @return
     */
    @Override
    public void testDistributedLock5() {
        String lockName = "lock";
        String uuid = UUID.randomUUID().toString();
        Boolean lock = this.distributedLock.tryLock(lockName, uuid, 3);

        // 如果加锁成功
        if (Boolean.TRUE.equals(lock)) {
            try {
                String num = this.redisTemplate.opsForValue().get("num");
                if (StringUtils.isBlank(num)) {
                    this.redisTemplate.opsForValue().set("num", "1");
                    return;
                }
                int value = Integer.parseInt(num);
                this.redisTemplate.opsForValue().set("num", String.valueOf(++value));

                // 测试可重入锁
                this.testSubDistributedLock5(lockName, uuid);
            } catch (NumberFormatException e) {
                log.error("业务逻辑执行失败", e);
                throw e;
            } finally {
                // 释放锁
                this.distributedLock.unLock(lockName, uuid);
            }
        }
    }

    /**
     * 测试可重入锁
     *
     * @param lockName  锁的名称
     * @param uuid      UUID
     */
    public void testSubDistributedLock5(String lockName, String uuid) {
        // 因为锁已经被testDistributedLock5()方法获取，这里就是重入锁
        Boolean lock = this.distributedLock.tryLock(lockName, uuid, 3);

        // 如果加锁成功
        if (Boolean.TRUE.equals(lock)) {
            try {
                // TODO:业务逻辑
            } catch (Exception e) {
                log.error("业务逻辑执行失败", e);
                throw e;
            } finally {
                // 释放锁
                this.distributedLock.unLock(lockName, uuid);
            }
        }
    }

    /**
     * 分布式锁(基于redis) 版本6
     *      锁的自动续期
     *          使用lua脚本判断锁是否存在，存在则重置过期时间 和 定时任务(java.util.concurrent.ScheduledFuture)
     *
     *      原因：版本3中提到过，锁的过期时间难以把握，无论设置多长时间都会有实际执行时间比过期时间长的清况
     *          我们加锁的目的就是为了同一时刻只能有一个请求进入此方法，一旦锁过期但是方法还没执行完，下一个请求就可以获取到锁进入此方法，就会产生并发问题
     *
     *      lua脚本：
     *          if redis.call('hexists', 'lock', 'uuid') == 1   ----->如果锁存在
     *              then
     *              return redis.call('expire', 'lock', 3)          ----->重置过期时间，并返回重置结果(0/1)
     *          else                                            ----->如果锁不存在
     *              return 0                                        ----->返回0代表自动续期失败
     *          end
     *
     *      lua脚本(参数替换)：
     *          if redis.call('hexists', KEYS[1], ARGV[1]) == 1
     *              then
     *              return redis.call('expire', KEYS[1], ARGV[2])
     *          else
     *              return 0
     *          end
     *
     *          KEYS: lock
     *          ARGV: uuid 3
     *
     *      最终脚本：EVAL "if redis.call('hexists', KEYS[1], ARGV[1]) == 1 then return redis.call('expire', KEYS[1], ARGV[2]) else return 0 end" 1 lock uuid 30
     *
     * @return
     */
    @Override
    public void testDistributedLock6() {
        String lockName = "lock";
        String uuid = UUID.randomUUID().toString();
        Boolean lock = this.distributedLock.tryLock(lockName, uuid, 3);

        // 如果加锁成功
        if (Boolean.TRUE.equals(lock)) {
            try {
                String num = this.redisTemplate.opsForValue().get("num");
                if (StringUtils.isBlank(num)) {
                    this.redisTemplate.opsForValue().set("num", "1");
                    return;
                }
                int value = Integer.parseInt(num);
                this.redisTemplate.opsForValue().set("num", String.valueOf(++value));

                // 休眠60秒测试自动续期
                Thread.sleep(20000);
            } catch (NumberFormatException e) {
                log.error("业务逻辑执行失败", e);
                throw e;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 释放锁
                this.distributedLock.unLock(lockName, uuid);
            }
        }
    }

    /**
     * Redisson分布式锁
     *      redisson的加锁、解锁、自动续期几乎跟DistributedLocK中的实现思路一致
     *
     * @return
     */
    @Override
    public void testRedissonLock() {
        // 加锁
        RLock lock = this.redissonClient.getLock("lock");
        lock.lock();

        try {
            String num = this.redisTemplate.opsForValue().get("num");
            if (StringUtils.isBlank(num)) {
                this.redisTemplate.opsForValue().set("num", "1");
                return;
            }
            int value = Integer.parseInt(num);
            this.redisTemplate.opsForValue().set("num", String.valueOf(++value));
        } finally {
            // 解锁
            lock.unlock();
        }
    }

    /** ----------------------------------------------------synchronized是可重入锁-----------------------------------------------------------*/

    /**
     * synchronized是可重入锁
     *      synchronized加在方法上时，锁的对象就是方法所在类的实例对象，那下面我的a方法和b方法锁的对象就是相同的，都是当前类的实例对象，也就是this关键字引用的对象
     *      那么当我a方法获取到锁之后，调用b方法，按理来说b方法的锁的对象被a持有，就会等待a方法释放锁才能执行业务操作
     *      但是由于synchronized是可重入锁，意味着一个已经拥有某个对象锁的线程可以再次获取同一对象的锁而不会导致死锁
     *      当一个线程进入一个synchronized块或方法时，它会自动增加锁的持有计数；当线程离开时，它会减少计数。只有当计数降为零时，锁才会被释放，允许其他线程获取该锁
     *
     *      由于synchronized是jvm进行实现的，所以源码难以阅读
     *      可以参考本地锁ReentrantLock的加锁方法nonfairTryAcquire()和解锁方法tryRelease()
     *              ReentrantLock使用state记录锁的进入次数，使用exclusiveOwnerThread记录持有锁的线程
     *                  获取锁时：
     *                      state为0：加锁，state++，并将当前线程设置给exclusiveOwnerThread属性
     *                      state不为0：判断当前线程是否等于exclusiveOwnerThread，如果是state++，如果不是获取锁失败
     *                  释放锁时：
     *                      1.判断当前线程是否等于exclusiveOwnerThread
     *                          不等于：抛异常
     *                          相等：state为0时，设置exclusiveOwnerThread为null
     *                      2.state--
     *
     *
     * @param args
     */
    public static void main(String[] args) {
        a();
    }

    public synchronized static void a() {
        System.out.println("进入a方法");
        b();
    }

    public synchronized static void b() {
        System.out.println("进入b方法");
    }
}
