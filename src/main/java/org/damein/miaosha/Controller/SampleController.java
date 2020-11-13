package org.damein.miaosha.Controller;

import org.damein.miaosha.domain.User;
import org.damein.miaosha.redis.RedisService;
import org.damein.miaosha.redis.UserKey;
import org.damein.miaosha.result.CodeMsg;
import org.damein.miaosha.result.Result;
import org.damein.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> home() {
        return Result.success("Hello,World");
    }

    @RequestMapping("/error")
    @ResponseBody
    public Result<String> error() {
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    //进入页面模板的方法
    @RequestMapping("/thymeleaf")
    @ResponseBody
    public String thymeleaf(Model model) {
        model.addAttribute("name", "tian");
        return "hello";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet() {

        User user = userService.getById(2);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx() {

        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {

        User user= redisService.get(UserKey.getById,""+1, User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
        User user =new User();
        user.setId(1);
        user.setName("555111");
        redisService.set(UserKey.getById,""+1,user);//"UserKey:id1"
        return Result.success(true);
    }

}
