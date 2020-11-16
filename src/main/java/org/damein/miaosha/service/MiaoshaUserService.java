package org.damein.miaosha.service;

import org.damein.miaosha.dao.MiaoshaUserDao;
import org.damein.miaosha.domain.MiaoshaUser;
import org.damein.miaosha.result.CodeMsg;
import org.damein.miaosha.util.MD5Util;
import org.damein.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoshaUserService {

    @Autowired
    MiaoshaUserDao miaoshaUserDao;
    public MiaoshaUser getById(long id){
        return miaoshaUserDao.getById(id);
    }

    public CodeMsg login(LoginVo loginVo) {
        if (loginVo==null){
            return CodeMsg.SERSSION_ERROR;
        }
        String mobile=loginVo.getMobile();
        String formPass=loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser user=getById(Long.parseLong(mobile));
        if (user==null){
            return CodeMsg.MOBILE_NOT_EXIST;
        }
        //验证密码
        String dbPass=user.getPassword();
        String saltDB=user.getSalt();
        String calcPass= MD5Util.fromPassToDBPass(formPass,saltDB);
        if (!calcPass.equals(dbPass)){
            return CodeMsg.PASSWORD_WRONG;
        }
        return CodeMsg.SUCCESS;
    }
}
