package org.damein.miaosha.Controller;

import org.damein.miaosha.domain.MiaoshaUser;
import org.damein.miaosha.redis.RedisService;
import org.damein.miaosha.service.GoodsService;
import org.damein.miaosha.service.MiaoshaUserService;
import org.damein.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/goods")
public class GoodsController {

    private static Logger log = LoggerFactory.getLogger(GoodsController.class);


    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/to_list")
    //返回的页面的名称，即.html
    public String list(Model model, MiaoshaUser user) {
        model.addAttribute("user", user);
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        //将参数传到页面上
        model.addAttribute("goodsList", goodsList);
        return "goods_list";
    }

    @RequestMapping("/to_detail/{goodsId}")
    //返回的页面的名称，即.html
    public String detail(Model model, MiaoshaUser user,
                         @PathVariable("goodsId") long goodsId) {
        //自增id不会用普通的自增，要用snowflake算法
        model.addAttribute("user", user);

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);
        //
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds=0;
        if (now < startAt) {//秒杀没开始，倒计时
            miaoshaStatus=0;
            remainSeconds=(int)((startAt-now)/1000);
        } else if (now > endAt) {//秒杀已经结束
            miaoshaStatus=2;
            remainSeconds=-1;
        } else {//秒杀进行中
            miaoshaStatus=1;
            remainSeconds=0;
        }
        //讲这些参数传到页面上
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        return "goods_detail";
    }
}
