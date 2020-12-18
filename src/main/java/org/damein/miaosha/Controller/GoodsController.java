package org.damein.miaosha.Controller;

import org.apache.catalina.core.ApplicationContext;
import org.damein.miaosha.domain.MiaoshaUser;
import org.damein.miaosha.redis.GoodsKey;
import org.damein.miaosha.redis.RedisService;
import org.damein.miaosha.result.Result;
import org.damein.miaosha.service.GoodsService;
import org.damein.miaosha.service.MiaoshaUserService;
import org.damein.miaosha.vo.GoodsDetailVo;
import org.damein.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired(required = false)
    ApplicationContext applicationContext;

    @RequestMapping(value="/to_list",produces = "text/html")
    @ResponseBody
    //返回的页面的名称，即.html
    public String list(Model model, MiaoshaUser user, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("user", user);
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        //将参数传到页面上
        model.addAttribute("goodsList", goodsList);
        //return "goods_list";
        //取缓存
        String html=redisService.get(GoodsKey.getGoodsList,"",String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        IWebContext ctx=new WebContext(request,response,
                request.getServletContext(),request.getLocale(),model.asMap());
        //手动渲染
        //如果没有缓存，就手动渲染 然后向缓存存入
       html= thymeleafViewResolver.getTemplateEngine().process("goods_list",ctx);
       if(!StringUtils.isEmpty(html)){
           redisService.set(GoodsKey.getGoodsList,"",html);
       }
       return html;
    }

    @RequestMapping(value = "/to_detail2/{goodsId}",produces = "text/html")
    @ResponseBody
    //返回的页面的名称，即.html
    public String detail2(HttpServletResponse response,HttpServletRequest request,
                         Model model, MiaoshaUser user,
                         @PathVariable("goodsId") long goodsId) {
        //自增id不会用普通的自增，要用snowflake算法
        model.addAttribute("user", user);

        //取缓存
        String html=redisService.get(GoodsKey.getGoodsDetail,""+goodsId,String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }

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

        //return "goods_detail";
        IWebContext ctx=new WebContext(request,response,
                request.getServletContext(),request.getLocale(),model.asMap());
        //手动渲染
        //如果没有缓存，就手动渲染 然后向缓存存入
        html= thymeleafViewResolver.getTemplateEngine().process("goods_detail",ctx);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);
        }
        return html;
    }

    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    //返回的页面的名称，即.html
    public Result<GoodsDetailVo> detail(HttpServletResponse response, HttpServletRequest request,
                                        Model model, MiaoshaUser user,
                                        @PathVariable("goodsId") long goodsId) {
        //自增id不会用普通的自增，要用snowflake算法

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
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
        GoodsDetailVo vo=new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setMiaoshaStatus(miaoshaStatus);
        vo.setRemainSeconds(remainSeconds);
        vo.setUser(user);

        return Result.success(vo);
    }
}
