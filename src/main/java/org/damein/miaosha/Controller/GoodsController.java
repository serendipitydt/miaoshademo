package org.damein.miaosha.Controller;

import org.damein.miaosha.domain.MiaoshaUser;
import org.damein.miaosha.redis.RedisService;
import org.damein.miaosha.result.CodeMsg;
import org.damein.miaosha.result.Result;
import org.damein.miaosha.service.MiaoshaUserService;
import org.damein.miaosha.util.ValidatorUtil;
import org.damein.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Controller
@RequestMapping("/goods")
public class GoodsController {

    private static Logger log = LoggerFactory.getLogger(GoodsController.class);


    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    //@ResponseBody
    @RequestMapping("/to_list")
    //返回的页面的名称，即.html
    public String list(Model model, MiaoshaUser user) {
        model.addAttribute("user", user);
        return "goods_list";
    }

//    @RequestMapping("/to_detail")
//    //返回的页面的名称，即.html
//    public String detail(HttpServletResponse response,Model model,
//                          @CookieValue(value = MiaoshaUserService.COOKIE_NAME_TOKEN,required = false) String cookieToken,
//                          @RequestParam(value = MiaoshaUserService.COOKIE_NAME_TOKEN,required = false) String paraToken) {
//        //model.addAllAttributes("user",new MiaoshaUser());
//        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paraToken)){
//            return "login";
//        }
//        String token=StringUtils.isEmpty(paraToken)?cookieToken:paraToken;//设定优先级
//        MiaoshaUser user=userService.getByToken(response,token);
//        model.addAttribute("user",user);
//        return "goods_list";
//    }
}
