package com.plateno.booking.internal.job.order.abnormalSweepJob.service;


import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plateno.booking.internal.base.constant.PayStatusEnum;
import com.plateno.booking.internal.base.mapper.OrderMapper;
import com.plateno.booking.internal.base.mapper.OrderPayLogMapper;
import com.plateno.booking.internal.base.mapper.OrderProductMapper;
import com.plateno.booking.internal.base.mapper.SmsLogMapper;
import com.plateno.booking.internal.base.model.SelectOrderParam;
import com.plateno.booking.internal.base.pojo.Order;
import com.plateno.booking.internal.base.pojo.OrderPayLog;
import com.plateno.booking.internal.base.pojo.OrderPayLogExample;
import com.plateno.booking.internal.base.pojo.OrderProduct;
import com.plateno.booking.internal.base.pojo.OrderProductExample;
import com.plateno.booking.internal.bean.config.Config;
import com.plateno.booking.internal.bean.contants.BookingConstants;
import com.plateno.booking.internal.bean.contants.BookingResultCodeContants;
import com.plateno.booking.internal.bean.contants.PayGateCode;
import com.plateno.booking.internal.bean.contants.ViewStatusEnum;
import com.plateno.booking.internal.bean.exception.OrderException;
import com.plateno.booking.internal.bean.request.point.ValueBean;
import com.plateno.booking.internal.bean.response.gateway.pay.PayQueryResponse;
import com.plateno.booking.internal.bean.response.gateway.refund.RefundQueryResponse;
import com.plateno.booking.internal.common.util.LogUtils;
import com.plateno.booking.internal.common.util.json.JsonUtils;
import com.plateno.booking.internal.email.model.RefundSuccessContent;
import com.plateno.booking.internal.email.service.PhoneMsgService;
import com.plateno.booking.internal.gateway.PaymentService;
import com.plateno.booking.internal.goods.MallGoodsService;
import com.plateno.booking.internal.member.PointService;
import com.plateno.booking.internal.service.log.OrderLogService;
import com.plateno.booking.internal.service.order.MOrderService;
import com.plateno.booking.internal.sms.SMSSendService;

/**
 * 支付网关同步
 * @author mogt
 * @date 2016年11月14日
 */
@Service
public class PayGatewaySyncService {
	protected final static Logger logger = LoggerFactory.getLogger(PayGatewaySyncService.class);
	

	@Autowired
	private OrderLogService orderLogService;

	@Autowired
	private PaymentService paymentService;
	
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired 
	private MOrderService  orderService;
	
	@Autowired 
	private PointService  pointService;
	
	@Autowired
	private OrderPayLogMapper orderPayLogMapper;
	
	@Autowired
	private MallGoodsService mallGoodsService;
	
	@Autowired
	private OrderProductMapper orderProductMapper;
	
	@Autowired
	private SMSSendService sendService;
	
	@Autowired
	private SmsLogMapper smsLogMapper;
	
	@Autowired
	private PhoneMsgService phoneMsgService;

	/**
	 * 同步支付中和退款中的订单状态
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void sync() throws Exception {
		
		logger.info("处理退款中订单开始...");
		
		SelectOrderParam selectOrderParam = new SelectOrderParam();
		selectOrderParam.setPageNo(0);
		selectOrderParam.setPageNumber(3000);
		selectOrderParam.setPayStatus(BookingResultCodeContants.PAY_STATUS_10);
		//退款中的订单
		List<Order> orderTList=orderMapper.getPageOrders(selectOrderParam );
		handleEach(orderTList);
		
		logger.info("处理退款中订单结束");
		
		logger.info("处理支付中订单开始...");
		
		//支付中的订单

		List<Order> orderPayingList=orderMapper.getPayingAndPayLogPre5Min(BookingResultCodeContants.PAY_STATUS_11);
		handleEach(orderPayingList);
		
		logger.info("处理支付中订单结束");
		
		logger.info("处理退款中订单开始...");
	}
	
	private void handleEach(List<Order> listOrder)throws Exception{
		for(Iterator<Order> iter=listOrder.iterator();iter.hasNext();){
			Order order = (Order)iter.next();
			Integer status=order.getPayStatus();
			switch(status){
			
			//处理账单退款中状态：10,验证网关退款查询接口 ==> 7/13
			case 10:
				orderService.handleGateWayefund(order);
				break;
			
			//处理支付中账单状态：11,验证网关支付查询接口==>3/12
			case 11:
				orderService.handlePaying(order);
				break;
			
			}
		}
	}
}
