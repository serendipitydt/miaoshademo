package org.damein.miaosha.service;

import org.damein.miaosha.dao.MiaoshaUserDao;
import org.damein.miaosha.domain.MiaoshaUser;
import org.damein.miaosha.exception.GlobalException;
import org.damein.miaosha.redis.MiaoshaUserKey;
import org.damein.miaosha.redis.RedisService;
import org.damein.miaosha.result.CodeMsg;
import org.damein.miaosha.util.MD5Util;
import org.damein.miaosha.util.UUIDUtil;
import org.damein.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class MiaoshaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoshaUser getById(long id) {
        //取缓存
        MiaoshaUser user= redisService.get(MiaoshaUserKey.getById,""+id,MiaoshaUser.class);
        if(user!=null){
            return user;
        }
        //缓存没有的话 查数据库
        user=miaoshaUserDao.getById(id);
        //数据库有的话，存入缓存
        if(user!=null){
            redisService.set(MiaoshaUserKey.getById,""+id,user);
        }
        return user;
    }

    public boolean updatePassword(String token,long id,String formpass){
        //取user
        MiaoshaUser user=getById(id);
        if(user==null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //为什么要新产生对象去更新而不直接更新user呢？因为更新会产生很多sql，deailog，会影响效率，所以生成一个新的对象
        //所以修改哪个字段就更新哪个字段

        //更新数据库
        MiaoshaUser toBeUpdate=new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.fromPassToDBPass(formpass,user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        //处理缓存，因为user变了
        redisService.delete(MiaoshaUserKey.getById,""+id);
        //token不能删，删了就不能登陆了，更新即可
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token,token,user);
        return true;
    }

    //登录时出现了异常的话直接往外抛
    //全局异常处理器 GlobalExceptionHanler会拦截捕捉异常然后输出
    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERSSION_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        //从数据库去一个user
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        System.out.println("数据库密码是： " + dbPass);
        String saltDB = user.getSalt();
        System.out.println("数据库验证块是： " + saltDB);
        String calcPass = MD5Util.fromPassToDBPass(formPass, saltDB);
        System.out.println("formPass: " + formPass);
        System.out.println("计算的密码是： " + calcPass);
        //String calcDBPass= MD5Util.fromPassToDBPass(dbPass,saltDB);
        //System.out.println("计算的数据库密码是： "+calcDBPass);

        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_WRONG);
        }

        String token = UUIDUtil.uuid();
        addCookie(response,token, user);
        return true;
    }

    /**
     * 分布式seddion的部分，相当难，可以多看几遍视频
     * @param response
     * @param user
     */
    private void addCookie(HttpServletResponse response,String token, MiaoshaUser user) {
        //生产cookie

        //吧token写到数据库中，传递给客户端
        //在此之前需要标记token是哪一个用户的，所以需要将用户信息写到redis里面
        redisService.set(MiaoshaUserKey.token, token, user);//正好前面从数据库取了一个user 用作value
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        //把cookie写到客户端去了
        response.addCookie(cookie);

    }

    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        //延长有效期
        // 将缓存里的重新生成一个cookie 再写出去即可
        if(user!=null){
            addCookie(response,token, user);//为空的话，不是合法的token，不需要做处理
        }

        return user;
    }
}
