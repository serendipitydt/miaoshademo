##2020/11/16 2.6
- 新增分布式session实现，生成token，cookie并且将用户的信息，即tock等存进redis缓存中
- 开始写商品页面，goods_list.html ，GoodsController等，并且在MiaoshaUserService里面增加了redis存取token和cookie的部分

##2020/11/17 2.7
- 优化GoodsController,将其逻辑结构转移到UerArgumentResolver里面，新增config包。
- 以后获取session的方法改变了，只需要在UerArgumentResolver这个方法里面做调整即可，不需要动GoodsController这些业务逻辑
- 延长了token的有效期,修改了不再每次生成新的token，一次生成，然后作为参数传入，不断更新。

##第二章已完成
