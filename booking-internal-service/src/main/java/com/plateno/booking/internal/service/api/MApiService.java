package com.plateno.booking.internal.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plateno.booking.internal.bean.request.custom.MAddBookingParam;
import com.plateno.booking.internal.interceptor.adam.common.bean.ResultCode;
import com.plateno.booking.internal.interceptor.adam.common.bean.ResultVo;
import com.plateno.booking.internal.validator.order.CouponValidateService;
import com.plateno.booking.internal.validator.order.ProductValidateService;


/**
 * @author user
 * 
 *         策略模式实现,根据渠道获取对象实例
 *
 */
@Service
public class MApiService {

	


	
	
	
	@Autowired
	private ProductValidateService productValidateService;
	
	@Autowired
	private CouponValidateService couponValidateService;
	
	/**
	 * 校验订单
	 * 
	 * @param convertBookingParam
	 * @return
	 * @throws Exception 
	 */
	public ResultVo Validate(MAddBookingParam addBookingParam,ResultVo output){
		productValidateService.checkProductAndCal(addBookingParam, output);
		//校验不通过则直接返回
		if(!output.getResultCode().equals(ResultCode.SUCCESS)){
		    output.setData(null);
		    return output;
		}
		//判断优惠券规则
        if(addBookingParam.getCouponId() != null && addBookingParam.getCouponId() > 0) {
            couponValidateService.checkCoupon(addBookingParam, output);
            //校验不通过则直接返回
            if(!output.getResultCode().equals(ResultCode.SUCCESS)){
                output.setData(null);
                return output;
            }
        }
        //判断实付金额是否准确
        productValidateService.checkPayMoney(addBookingParam, output);
        if(!output.getResultCode().equals(ResultCode.SUCCESS)){
            output.setData(null);
            return output;
        }
        
		return output;
	}
	
	
	
	
	
	
	
	

}
