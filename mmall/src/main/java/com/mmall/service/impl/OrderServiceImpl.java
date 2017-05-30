package com.mmall.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.OrderItemMapper;
import com.mmall.dao.OrderMapper;
import com.mmall.dao.PayInfoMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import com.mmall.pojo.PayInfo;
import com.mmall.pojo.Product;
import com.mmall.pojo.Shipping;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FTPUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;

/**
 * 这是订单的Service的实现
 * @author Administrator
 *
 */
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private OrderItemMapper orderItemMapper;
	
	@Autowired
	private PayInfoMapper payInfoMapper;
	
	@Autowired
	private CartMapper cartMapper;
	
	@Autowired
	private ProductMapper productMapper;
	
	@Autowired
	private ShippingMapper shippingMapper;
	
	private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	
	/**
	 * 这是我们支付宝的支付功能
	 * @param userId  这是哪个用户的订单
	 * @param orderNo 这是商品的订单
	 * @param path  这是要生成二维码传到哪里的路径
	 * @return
	 */
	public ServerResponse pay(Integer userId,Long orderNo,String path){
		//我们与前端的约定是：我们把订单号和二维码的URL返回给它，我们直接用map来承载这个对象
		Map<String,String> resultMap = Maps.newHashMap();
		//首先，我们需要校验通过userId和orderNo来查询这个用户的订单是否存在
		Order order = orderMapper.selectOrderByUserId(userId, orderNo);
		if(order == null){
			return ServerResponse.createByErrorMessage("该用户没有该订单！");
		}
        //若order不为空，则：将这个订单放入到Map中
		resultMap.put("orderNo", String.valueOf(order.getOrderNo()));
		
		//接下来，我们就要组装支付宝订单的各个参数啦，我们就去：Main.java类中，将生成支付二维码的参数复制过来，如下：
		
		// (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo =order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("happymmall扫码支付，订单号：").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共：").append(totalAmount).append("元").toString();
        

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        
        //下面我们要获取订单明细的列表,在OrderItemMapper中去写一个接口
        List<OrderItem> orderItemList = orderItemMapper.getOrderItem(userId, orderNo);
        //遍历一下这个集合
        for(OrderItem orderItem : orderItemList){
        	//下面我们构建GoodsDetail,注意：在构建GoodsDetail时，为什么要将我们的单价乘以100，因为：GoodsDetail中的单价的单位为分，故需要乘以100
        	// 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        	GoodsDetail goods1 = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
        			                BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(), orderItem.getQuantity());
        	
        	goodsDetailList.add(goods1);
        }
      
        
        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
            .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
            .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
            .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
            .setTimeoutExpress(timeoutExpress)
            .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
            .setGoodsDetailList(goodsDetailList);

        
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
        
        
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
            	logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                //然后，下面是比较关键的部分，我们下单成功后，会生成二维码，然后，把二维码传到我们的服务器上，最后组装出来url返回给前端
                File folder = new File(path);
                //如果文件夹不存在的话，我们会为其赋予权限，并创建文件夹
                if(!folder.exists()){
                	folder.setWritable(true);
                	folder.mkdirs();
                }
                
                
                // 需要修改为运行机器上的路径
                String qrPath = String.format(path+"/qr-%s.png",response.getOutTradeNo());
                //下面我们设置一个文件的名称，使用String的format（）方法，传入两个参数，第一个参数是：格式，后面的是文件名，文件名会替换掉前面格式中的“%s”.
                String qrFileName= String.format("qr-%s.png", response.getOutTradeNo());
                //下面是使用google的包生成二维码
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                
                File targetFile = new File(path,qrFileName);
                //这里我们使用ftp服务器来长传文件
			try {
				FTPUtil.uploadFile(Lists.newArrayList(targetFile));
			} catch (IOException e) {
				logger.error("上传二维码异常！",e);
			}
			logger.info("qrPath:"+qrPath);
			//接下来，我们要拼装url地址
			String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
			resultMap.put("qrUrl", qrUrl);
			return ServerResponse.createBySuccess(resultMap);
			
       case FAILED:
            	logger.error("支付宝预下单失败!!!");
               return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
            	logger.error("系统异常，预下单状态未知!!!");
            	return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
            	logger.error("不支持的交易状态，交易返回异常!!!");
            	return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }
	
	 // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
            	logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                    response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }
    
    /**
     * 支付宝的回调方法,下面的方法是判断订单状态和订单是否已经支付啦
     * @param params
     * @return
     */
    public ServerResponse aliCallback(Map<String,String> params){
    	//首先，从map中拿到我们的订单号(即：商户订单号),注意：我们是在：从支付宝开发文档中的异步通知的参数表中拿到的参数：out_trade_no
    	//这个商户订单号，也就是：商户发送请求的一个订单号，即为：内部订单号
    	Long orderNo = Long.parseLong(params.get("out_trade_no"));
    	//把支付宝的交易号拿过来，支付宝交易号，即：支付宝接受商品订单号后，生成并返回给商户的订单号
    	String tradeNo = params.get("trade_no");
    	//支付状态
    	String tradeStatus = params.get("trade_status"); 
    	
    	//接着，我们在查一下，这个内部订单号是否存在，需要在：PayInfoMapper.java中去写一个sql方法
    	Order order = orderMapper.selectByOrderNo(orderNo);
    	if(order == null){
    		return ServerResponse.createByErrorMessage("非快乐慕商城的订单，回调忽略！");
    	}
    	//然后，判断这个订单是否已经支付啦，我们需要去常量类中去声明一个枚举
    	//下面判断一下，当订单状态大于等于支付成功的代码的时候，就不需要回调啦
    	if(order.getStatus() >= Const.orderStatusEnum.PAID.getCode()){
    		//此时，就不需要在进行回调啦
    		return ServerResponse.createBySuccess("支付宝重复调用！");
    	}
    	//判断支付宝的状态，去常量类中写一个接口
    	if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
    		//当支付宝的状态为：success,就将订单的状态置成：已支付
    		order.setStatus(Const.orderStatusEnum.PAID.getCode());
    		//可以更新我们付款时间，可以从回调中获取
    		order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
    		//将订单状态更新
    		order.setId(order.getId());
    		orderMapper.updateByPrimaryKeySelective(order);
    	
    	}
    	
    	//此时，既然已经付款啦，我们需要付款信息,故：我们来创建PayInfo 
    	PayInfo payInfo = new PayInfo();
    	payInfo.setUserId(order.getUserId());
    	payInfo.setOrderNo(order.getOrderNo());
    	//下面我们设置支付平台，需要去常量类中，创建一个枚举来表示：不同的支付平台
    	payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
    	//将支付宝的交易号赋给payInfo
    	payInfo.setPlatformNumber(tradeNo);
    	payInfo.setPlatformStatus(tradeStatus);
    	//接着，我们将payInfo对象，放入到数据库中
    	payInfoMapper.insert(payInfo);
    	
    	return ServerResponse.createBySuccess();
    }
    
    /**
     * 这是获取订单状态的方法
     * @param userId
     * @param orderNo
     * @return
     */
    public ServerResponse<Boolean> queryOrderPayStatus(Integer userId,Long orderNo){
    	//首先，查询一下这个订单号是否存在
    	Order order = orderMapper.selectOrderByUserId(userId, orderNo);
    	if(order == null){
    		return ServerResponse.createByErrorMessage("用户没有该订单！");
    	}
    	if(order.getStatus() >= Const.orderStatusEnum.PAID.getCode()){
    		return ServerResponse.createBySuccess();
    	}
    	return ServerResponse.createByError();
    }
    
    
    /**
     * 创建订单的方法
     * @param userId
     * @param shippingId
     * @return
     */
    public ServerResponse createOrderByUesId(Integer userId,Integer shippingId){
    	//首先，我们从购物车中获取被选中的商品集合
    	List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
    	
    	//接着，我们去计算每个商品的总价，就要使用到：mmall_order_item表
    	ServerResponse serverResponse = this.getCartOrderItem(userId, cartList);
    	if(!serverResponse.isSuccess()){
    		return serverResponse;
    	}
    	
    	List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
    	//计算总价，可以在重新写一个方法来实现计算商品的总价
    	BigDecimal payment = this.getOrderTotalPrice(orderItemList);
    	
    	//生成订单，我们需要一个方法来组装订单
    	Order order = this.assembleOrder(userId, shippingId, payment);
    	if(order == null){
    		return ServerResponse.createByErrorMessage("创建订单失败！");
    	}
    	
    	//接下来，判断OrderItem是否为空
    	if(CollectionUtils.isEmpty(orderItemList)){
    		return ServerResponse.createByErrorMessage("购物车为空！");
    	}
    	//在将我们的订单号放入到我们的orderItemList中去
    	for(OrderItem orderItem :orderItemList){
    		orderItem.setOrderNo(order.getOrderNo());
    	}
    	
    	//mybatis 批量插入到OrderItem表中
    	orderItemMapper.batchInsert(orderItemList);
    	//此时，订单生成成功，我们要减少库存，还是需要在外面创建方法来实现
    	this.reduceProductStock(orderItemList);
    	//清空购物车，需要去重新写一个方法
    	this.cleanCart(cartList);
    	
    	//返回给前端数据，我们需要把收货人的地址，图片的前缀等等，我们重新写一个方法来组装我们的orderVo
    	OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
    	return ServerResponse.createBySuccess(orderVo);
    }
    
    //下面我们来组装我们的orderVo
    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList){
    	OrderVo orderVo = new OrderVo();
    	orderVo.setOrderNo(order.getOrderNo());
    	orderVo.setPayment(order.getPayment());
    	orderVo.setPaymentType(order.getPaymentType());
    	//这里就去常量类中的支付方式的枚举类中创建一个静态获取枚举的方法
    	orderVo.setPaymentTypeDesc(Const.paymentTypeEnum.codeof(order.getPaymentType()).getValue());
    	orderVo.setPostage(order.getPostage());
    	orderVo.setStatus(order.getStatus());
    	orderVo.setStatusDesc(Const.orderStatusEnum.codeOf(order.getStatus()).getValue());
    	
    	orderVo.setShippingId(order.getShippingId());
    	Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
    	//若收货地址不为空的话，我们从shipping对象中获取收货地址的收货人的姓名
    	if(shipping != null){
    		orderVo.setReceiverName(shipping.getReceiverName());
    		//我们下面要给shippingVo赋给orderVo,需要去组装shippingVo
        	orderVo.setShippingVo(this.assembleShippingVo(shipping));
    	}
    	
    	orderVo.setPaymentTime(DateTimeUtil.dateOrStr(order.getPaymentTime()));
    	orderVo.setSendTime(DateTimeUtil.dateOrStr(order.getSendTime()));
    	orderVo.setCloseTime(DateTimeUtil.dateOrStr(order.getCloseTime()));
    	orderVo.setCreateTime(DateTimeUtil.dateOrStr(order.getCreateTime()));
    	
    	orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
    	
    	//这里我们创建一个List，List的是以OrderItemVo类型的元素
    	List<OrderItemVo> orderItemVoList = Lists.newArrayList();
    	//下面我们来组装我们的orderItem
    	for(OrderItem orderItem:orderItemList){
    		//在orderItemList中的所有元素转换成OrderItemVo对象
    		OrderItemVo orderItemVo = this.assembleOrderItem(orderItem);
    		orderItemVoList.add(orderItemVo);
    	}
    	//这里组装了orderItemList
    	orderVo.setOrderItemList(orderItemVoList);
    	
    	return orderVo;
    }
    
    
    //这是我们组装orderItemVo的类
    private OrderItemVo assembleOrderItem(OrderItem orderItem){
    	OrderItemVo orderItemVo = new OrderItemVo();
    	
    	orderItemVo.setOrderNo(orderItem.getOrderNo());
    	orderItemVo.setProductId(orderItem.getProductId());
    	orderItemVo.setProductName(orderItem.getProductName());
    	orderItemVo.setProductImage(orderItem.getProductImage());
    	orderItemVo.setQuantity(orderItem.getQuantity());
    	orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
    	orderItemVo.setTotalPrice(orderItem.getTotalPrice());
    	orderItemVo.setCreateTime(DateTimeUtil.dateOrStr(orderItem.getCreateTime()));
    	
    	return orderItemVo;
    }
    
    //这是我们组装的shippingVo
    private ShippingVo assembleShippingVo(Shipping shipping){
    	ShippingVo shippingVo = new ShippingVo();
    	
    	shippingVo.setReceiverName(shipping.getReceiverName());
    	shippingVo.setReceiverPhone(shipping.getReceiverPhone());
    	shippingVo.setReceiverMoble(shipping.getReceiverMobile());
    	shippingVo.setReceiverProvince(shipping.getReceiverProvince());
    	shippingVo.setReceiverCity(shipping.getReceiverCity());
    	shippingVo.setReceiverAddress(shipping.getReceiverAddress());
    	shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
    	shippingVo.setReceiverZip(shipping.getReceiverZip());
    	
    	return shippingVo;
    }
    
    /*
     * 这是清空购物车的方法
     */
    private void cleanCart(List<Cart> cartList){
    	for(Cart cart:cartList){
    		cartMapper.deleteByPrimaryKey(cart.getId());
    	}
    }
    
    
    /*
     * 这是订单生成以后，我们需要减少库存的方法
     */
    private void reduceProductStock(List<OrderItem> orderItemList){
    	for(OrderItem orderItem:orderItemList){
    		//获取产品，通过orderItem中的属性：ProductId来获取
    		Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
    		product.setStock(product.getStock()-orderItem.getQuantity());
    		//更新product商品的库存
    		productMapper.updateByPrimaryKeySelective(product);
    	}
    }
    
    /**
     * 这是我们组装订单的类
     * @param userId
     * @param shippingId  这是收货地址的id
     * @param payment  	这是商品的总价
     */
    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal payment){
    	
    	//在组装order订单的时候需要一个订单号，也需要重新创建类来生成订单号
    	Order order = new Order();
    	Long orderNo = this.generateOrderNo();
    	order.setOrderNo(orderNo);
    	//下面是组装这个商品的状态，这个状态是：未付款
    	order.setStatus(Const.orderStatusEnum.NO_PAY.getCode());
    	//设置是否包邮
    	order.setPostage(0);
    	//下面我们设置它的支付方式，需要去常量里面定义一个枚举
    	order.setPaymentType(Const.paymentTypeEnum.ON_LINE_PAY.getCode());
    	order.setPayment(payment);
    	
    	order.setUserId(userId);
    	order.setShippingId(shippingId);
    	//发货时间等等
    	//付款时间等等
    	//下面将我们order插入到orderMapper中
    	int count = orderMapper.insert(order);
    	//判断一下，是否为空，来判断我们的订单是否创建成功
    	if(count > 0){
    		return order;
    	}
    	return null;
    }
    
    /*
     * 这是我们生成订单号的方法，这里要说一下这个订单号的生成的规则，我们不能这个一个订单号，让其递增，这样的话，我们的竞争对手就会知道我们这个网站每天卖出的数量
     * 这里，我们使用的是当前的时间加上当前时间取模来生成订单号，但是，这样的话，还是有漏洞，如果两个用户在同一时间购买商品的话，有一个用户生成订单就会失败，故：一般在后面添加上一个随机数
     */
    private Long generateOrderNo(){
    	Long currentTime = System.currentTimeMillis();
    	return currentTime+new Random().nextInt(100);
    }
    
    /**
     * 下面的方法是计算订单中商品的总价
     * @param orderItemList
     * @return
     */
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
    	//首先，我们初始化一下这儿BigDecimal
    	BigDecimal payment = new BigDecimal("0");
    	//遍历这个集合
    	for(OrderItem orderItem : orderItemList){
    		payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());

    	}
    	return payment;
    }
    
    /**
     * 下面就通过购物车对象将子订单的详情获取了
     * @param userId
     * @param cartList
     * @return
     */
    private ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){
    	//首先，初始化一下，返回的List
    	List<OrderItem> orderItemList = Lists.newArrayList();
    	//判断一下，我们从购物车中获取的商品是否为空
    	if(CollectionUtils.isEmpty(cartList)){
    		return ServerResponse.createByErrorMessage("购物车为空！");
    	}
    	
    	//然后，验证购物车的数据，包括商品的数量和状态
    	for(Cart cartItem : cartList){
    		//创建OrderItem的实例
    		OrderItem orderItem = new OrderItem();
    		//通过cartItem中获取商品
    		Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
    		//在校验一下，这个商品是否在线
    		if(Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()){
    			return ServerResponse.createByErrorMessage("商品"+product.getName()+"不是在线状态！");
    		}
    		//接着，校验一下，商品的库存，若购买商品的数量大于该商品的库存的话，我们提示错误
    		if(cartItem.getQuantity() > product.getStock()){
    			return ServerResponse.createByErrorMessage("该商品的库存不足！");
    		}
    		//然后，进行组装：OrderItem
    		orderItem.setUserId(userId);
    		orderItem.setProductId(product.getId());
    		orderItem.setProductName(product.getName());
    		orderItem.setProductImage(product.getMainImage());
    		orderItem.setCurrentUnitPrice(product.getPrice());
    		orderItem.setQuantity(cartItem.getQuantity());
    		orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartItem.getQuantity()));
    		
    		//将orderItem放入到orderItemList
    		orderItemList.add(orderItem);
    		
    	}
    	return ServerResponse.createBySuccess(orderItemList);
    	
    }
    
    /**
     * 这是取消订单的方法
     * @param userId
     * @param orderNo
     * @return
     */
    public ServerResponse<String> cancelOrder(Integer userId,Long orderNo){
    	//首先，获取订单
    	Order order = orderMapper.selectOrderByUserId(userId, orderNo);
    	if(order == null){
    		return ServerResponse.createByErrorMessage("该用户此订单不存在！");
    	}
    	//接着，来判断一下，这个订单的状态
    	if(Const.orderStatusEnum.NO_PAY.getCode() != order.getStatus()){
    		return ServerResponse.createByErrorMessage("该订单已支付，无法取消订单！");
    	}
    	
    	//然后，我们就创建一个Order对象，将订单的状态修改为：取消订单，在更新一下订单即可
    	Order updateOrder = new Order();
    	
    	updateOrder.setId(order.getId());;
    	updateOrder.setStatus(Const.orderStatusEnum.CANCELED.getCode());
    	
    	int count = orderMapper.updateByPrimaryKeySelective(updateOrder);
    	if(count > 0){
    		return ServerResponse.createBySuccessMessage("取消订单成功！");
    	}
    	return ServerResponse.createByErrorMessage("取消订单失败！");
    }
    
    /**
     * 这是我们获取购物车中商品的详情
     * @param userId
     * @return
     */
    public ServerResponse  getCartProduct(Integer userId){
    	//首先，去com.mmall.vo中去写一个orderProductVo
    	OrderProductVo orderProductVo = new OrderProductVo();
    	
    	//获取购物车对象列表
    	List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
    	//调用前面的方法，获取OrderItemVoList
    	ServerResponse serverResponse = this.getCartOrderItem(userId, cartList); 
    	if(!serverResponse.isSuccess()){
    		return serverResponse;
    	}
    	List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
    	//接着，计算商品的总价格
    	BigDecimal payment = new BigDecimal("0");
    	//遍历这个订单详情的列表
    	for(OrderItem orderItem:orderItemList){
    		payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
    	}
    	
    	//接下来，就可以给orderProdcutVo对象来赋值啦
    	orderProductVo.setProductTotalPrice(payment);
    	orderProductVo.setOrderItemList(orderItemList);
    	orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
    	
    	return ServerResponse.createBySuccess(orderProductVo);
    }
    
    /**
     * 查看订单的详情
     * @param userId
     * @param orderNo
     * @return
     */
    public ServerResponse<OrderVo> getOrderDetail(Integer userId,Long orderNo){
    	Order order = orderMapper.selectOrderByUserId(userId, orderNo);
    	if(order != null){
    		List<OrderItem> orderItemList = orderItemMapper.getOrderItem(userId, orderNo);
    		OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
    		return ServerResponse.createBySuccess(orderVo);
    	}
    	return ServerResponse.createByErrorMessage("该用户没有该订单！");
    }
    
    /**
     * 获取订单的列表
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> getOrderList(Integer userId,int pageNum,int pageSize){
    	//开始分页
    	PageHelper.startPage(pageNum, pageSize);
    	
    	//在order中写一个方法来获取订单列表
    	List<Order> orderList = orderMapper.selectOrderListByuserId(userId);
    	//由于：我们传递到前端，我们就使用OrderVo，故：需要重新写一个方法，将order组装成orderVo
    	List<OrderVo> orderVoList = this.assembleOrderVoList(orderList, userId);
    	PageInfo pageResult = new PageInfo(orderList);
    	pageResult.setList(orderVoList);
    	return ServerResponse.createBySuccess(pageResult);
    }
    
    /**
     * 这是我们将order对象组装成OrderVo
     * @param orderList
     * @param userId
     * @return
     */
    private List<OrderVo> assembleOrderVoList(List<Order> orderList,Integer userId){
    	List<OrderVo> orderVoList = Lists.newArrayList();
    	//接着，遍历List<Order>集合
    	for(Order order:orderList){
    		List<OrderItem> orderItemList = Lists.newArrayList();
    		if(userId == null){
    			//即：管理员登录，无法userId，即：后台查看订单列表
    			orderItemList = orderItemMapper.getOrderItemByOrderNo(order.getOrderNo());
    			OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
    			orderVoList.add(orderVo);
    		}
    		else{
    			orderItemList = orderItemMapper.getOrderItem(userId, order.getOrderNo());
    			OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
    			orderVoList.add(orderVo);
    		}	
    	}
    	return orderVoList;	
    }
    
    //backend后台
    /**
     * 后台获取订单的列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> manageOrderList(int pageNum,int pageSize){
    	PageHelper.startPage(pageNum, pageSize);
    	//首先，我们获取order表中获取所有的订单
    	List<Order> orderList = orderMapper.selectAllOrder();
    	List<OrderVo> orderVoList = this.assembleOrderVoList(orderList, null);
    	PageInfo pageResult = new PageInfo(orderList);
    	pageResult.setList(orderVoList);
    	return ServerResponse.createBySuccess(pageResult);
    }
    
    /**
     * 下面是查看订单的详情
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<OrderVo> manageOrderDetail(Long orderNo){
    	//首先，获取订单
    	Order order = orderMapper.selectByOrderNo(orderNo);
    	//接着，还要获取订单的详情的列表
    	if(order != null){
    		List<OrderItem> orderItemList = orderItemMapper.getOrderItemByOrderNo(orderNo);
        	OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
        	return ServerResponse.createBySuccess(orderVo);
    	}
    	return ServerResponse.createByErrorMessage("订单号不存在！");
    	
    }
    
    /**
     * 通过订单号来查询订单，这里我们为了后来，实现其他的查询，我们对其使用mybatis进行分页
     * @param orderNo
     * @return
     */
    public ServerResponse<PageInfo> managesearch(Long orderNo,int pageNum,int pageSize){
    	
    	PageHelper.startPage(pageNum, pageSize);
    	//首先，获取订单
    	Order order = orderMapper.selectOrderByOrderNo(orderNo);
    	if(order != null){
    		List<OrderItem> orderItemList = orderItemMapper.getOrderItemByOrderNo(orderNo);
    		OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
    		
    		//注意：这里我们使用：Lists.newArrayList()方法，将order 和 orderVo看成是一个集合
    		PageInfo pageResult = new PageInfo(Lists.newArrayList(order));
    		pageResult.setList(Lists.newArrayList(orderVo));
    		return ServerResponse.createBySuccess(pageResult);
    	}
    	return ServerResponse.createByErrorMessage("没有该订单!");
    }
    
    /**
     * 下面实现的是发货的功能
     * @param orderNo
     * @return
     */
    public ServerResponse<String> manageSendGoods(Long orderNo){
    	//首先，我们去获取订单 
    	Order order = orderMapper.selectByOrderNo(orderNo);
    	if(order != null){
    		//判断一下，订单的状态是否是已支付状态，若是：我们就发货
    		if(order.getStatus() == Const.orderStatusEnum.PAID.getCode()){
    			//下面是将订单的状态更改为：发货，发送时间更改为：当前时间
    			order.setStatus(Const.orderStatusEnum.SHIPPED.getCode());
    			order.setSendTime(new Date());
    			//更新订单的状态
    			int count = orderMapper.updateByPrimaryKeySelective(order);
    			if(count > 0){
    				return ServerResponse.createBySuccessMessage("发货成功！");
    			}
    			return ServerResponse.createByErrorMessage("发货失败！");
    		}
    		else if(order.getStatus() < Const.orderStatusEnum.NO_PAY.getCode()){
    			return ServerResponse.createByErrorMessage("该订单已取消，无法发货！");
    		}
    		else if(order.getStatus() < Const.orderStatusEnum.PAID.getCode()){
    			return ServerResponse.createByErrorMessage("该订单未支付为未支付状态！");
    		}
    		else if(order.getStatus() == Const.orderStatusEnum.SHIPPED.getCode()){
    			return ServerResponse.createByErrorMessage("该订单以发货，请耐心等待接收！");
    		}
    		
    	}
    	return ServerResponse.createByErrorMessage("没有此订单！");
    }
   

	


	
}