package com.plateno.testservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.plateno.booking.internal.bean.contants.LogisticsEnum;
import com.plateno.booking.internal.bean.exception.OrderException;
import com.plateno.booking.internal.bean.request.custom.MOrderParam;
import com.plateno.booking.internal.bean.request.custom.ReceiptParam;
import com.plateno.booking.internal.interceptor.adam.common.bean.ResultVo;
import com.plateno.booking.internal.service.logistics.LogisticsService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class LogtisticsServiceTest {

    @Autowired
    private LogisticsService logisticsService;
    
    
    @Test
    public void testModifyReceiptInfo() throws OrderException, Exception{
        
        ReceiptParam receiptParam = new ReceiptParam();
        receiptParam.setOrderNo("O1487756635130785187");
        receiptParam.setReceiptName("张思");
        receiptParam.setReceiptMobile("13888888888");
        receiptParam.setProvince("广东省");
        receiptParam.setCity("广州市");
        receiptParam.setArea("番禺区");
        receiptParam.setReceiptAddress("大学城");
        receiptParam.setOperateUserid("123456");
        receiptParam.setOperateUsername("管理员");
        receiptParam.setPlateForm(1);
        
        ResultVo<Object> modifyReceiptInfo = logisticsService.modifyReceiptInfo(receiptParam );
        System.out.println("结果：" + modifyReceiptInfo);
    }
    
    @Test
    public void testDeliverOrder() throws OrderException, Exception{
        
        MOrderParam orderParam = new MOrderParam();
        orderParam.setOrderNo("O1487756635130785187");
        orderParam.setOrderSubNo("O1487756635130785187");
        orderParam.setLogisticsType(1);
        orderParam.setLogisticsNo("2222222222");
        
        logisticsService.deliverOrder(orderParam);
    }
    
    @Test
    public void testModifydeliverOrder() throws OrderException, Exception{
        
        
        MOrderParam orderParam = new MOrderParam();
        orderParam.setOrderNo("O1479265160204863145");
        orderParam.setOrderSubNo("O1487756635130785187");
        orderParam.setLogisticsType(LogisticsEnum.ZT.getType());
        orderParam.setLogisticsNo(null);
        orderParam.setOperateUserid("12345");
        orderParam.setOperateUsername("23456");
        
        ResultVo<Object> modifydeliverOrder = logisticsService.modifydeliverOrder(orderParam );
        System.out.println(modifydeliverOrder);
    }
    
    @Test
    public void testEnterReceipt() throws OrderException, Exception{
        
        MOrderParam orderParam = new MOrderParam();
        orderParam.setOrderNo("O1487756635130785187");
        
        logisticsService.enterReceipt(orderParam);
    }
    
}
