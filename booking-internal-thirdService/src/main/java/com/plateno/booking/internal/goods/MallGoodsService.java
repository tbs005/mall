package com.plateno.booking.internal.goods;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

import com.plateno.booking.internal.bean.config.Config;
import com.plateno.booking.internal.bean.contants.BookingConstants;
import com.plateno.booking.internal.bean.exception.OrderException;
import com.plateno.booking.internal.common.util.LogUtils;
import com.plateno.booking.internal.common.util.http.HttpUtils;
import com.plateno.booking.internal.common.util.json.JsonUtils;
import com.plateno.booking.internal.wechat.model.ProductSkuBean;
import com.plateno.booking.internal.wechat.model.ProductSkuBean.SkuPropertyInfos;
import com.plateno.booking.internal.wechat.model.SkuBean;
import com.plateno.booking.internal.wechat.model.SkuStock;

@Service
public class MallGoodsService {
	
	public SkuBean getSkuProperty(String goodId) throws OrderException {
		SkuBean skuBean = new SkuBean();
		String response = "";
		try {
			ObjectNode node = JsonUtils.createObjectNode();
			node.put("productId", goodId);
			String url = Config.MALL_GOODS_URL+ "/productService/skuProperty/"+goodId; // 商品服务接口
			LogUtils.httpLoggerInfo("发起商品查询请求的参数:" + JsonUtils.toJsonString(node));
			response = HttpUtils.httpGetRequest(url);
			LogUtils.httpLoggerInfo("底层商品服务返回数据结构:" + response);
			if (response != null && !StringUtils.isBlank(response)) {
				JsonNode responseNode = JsonUtils.parseJson(response);
				Integer resultCode = responseNode.path("resultCode").asInt();
				if (!resultCode.equals(BookingConstants.TRIP_GOOD_SERVER_SUCCESS_CODE)) {
					String resultMsg = responseNode.path("resultMsg").asText();
					LogUtils.sysErrorLoggerError("底层商品服务出错，接口返回错误信息：" + resultMsg,null);
					return null;
				} else {
					if (!responseNode.has("data"))
						return null;
					if (responseNode.path("data").isNull())
						return null;
					//BeanUtils.copyProperties(responseNode.path("data"),skuBean);
					skuBean=JsonUtils.jsonToObject(responseNode.path("data"),SkuBean.class);
					return skuBean;
				}
			} else {
				LogUtils.sysErrorLoggerError("商品服务返回数据异常",null);
				return null;
			}
		} catch (Exception e) {
			LogUtils.sysErrorLoggerError(String.format("底层商品服务请求失败,%s", response), e);
			return null;
		}
	}
	
	
	public SkuStock getSkuStock(String goodId,String skuProperties) throws OrderException {
		SkuStock skuStock = new SkuStock();
		String response = "";
		try {
			ObjectNode node = JsonUtils.createObjectNode();
			node.put("productId", goodId);
			node.put("sku_properties", skuProperties);
			String url = Config.MALL_GOODS_URL+ "/productService/sku/"; // 商品服务接口
			LogUtils.httpLoggerInfo("商品sku库存价格查询请求的参数:" + JsonUtils.toJsonString(node));
			response = HttpUtils.httpPostRequest(url,JsonUtils.toJsonString(node),Config.CONNECT_SOKET_TIME_OUT_LONG,Config.CONNECT_TIME_OUT_LONG);
			LogUtils.httpLoggerInfo("底层商品sku库存接口返回数据结构:" + response);
			if (response != null && !StringUtils.isBlank(response)) {
				JsonNode responseNode = JsonUtils.parseJson(response);
				Integer resultCode = responseNode.path("resultCode").asInt();
				if (!resultCode.equals(BookingConstants.TRIP_GOOD_SERVER_SUCCESS_CODE)) {
					String resultMsg = responseNode.path("resultMsg").asText();
					LogUtils.sysErrorLoggerError("底层商品sku库存接口出错，接口返回错误信息：" + resultMsg,null);
					return null;
				} else {
					if (!responseNode.has("data"))
						return null;
					if (responseNode.path("data").isNull())
						return null;
					skuStock=JsonUtils.jsonToObject(responseNode.path("data"),SkuStock.class);
					return skuStock;
				}
			} else {
				LogUtils.sysErrorLoggerError("底层商品sku库存接口返回数据异常",null);
				return null;
			}
		} catch (Exception e) {
			LogUtils.sysErrorLoggerError(String.format("底层商品sku库存接口请求失败,%s", response), e);
			return null;
		}
	}
	
	
	
	public ProductSkuBean getProductAndskuStock(String goodId) throws OrderException {
		ProductSkuBean skuStock = new ProductSkuBean();
		String response = "";
		try {
			String url = Config.MALL_GOODS_URL+ "/productService/goods/"+goodId; // 商品服务接口
			LogUtils.httpLoggerInfo("查询请求的参数:" + JsonUtils.toJsonString(goodId));
			response = HttpUtils.httpGetRequest(url);
			LogUtils.httpLoggerInfo("商品库存接口返回数据结构:" + response);
			if (response != null && !StringUtils.isBlank(response)) {
				JsonNode responseNode = JsonUtils.parseJson(response);
				Integer resultCode = responseNode.path("resultCode").asInt();
				if (!resultCode.equals(BookingConstants.TRIP_GOOD_SERVER_SUCCESS_CODE)) {
					String resultMsg = responseNode.path("resultMsg").asText();
					LogUtils.sysErrorLoggerError("商品sku接口出错，接口返回错误信息：" + resultMsg,null);
					return null;
				} else {
					if (!responseNode.has("data"))
						return null;
					if (responseNode.path("data").isNull())
						return null;
					//BeanUtils.copyProperties(responseNode.path("data").path("goodsInfo"),skuStock);
					skuStock=JsonUtils.jsonToObject(responseNode.path("data").path("goodsInfo"), ProductSkuBean.class);
					List<SkuPropertyInfos> list = JsonUtils.jsonToObject(responseNode.path("data").path("skuPropertyInfos"), List.class, SkuPropertyInfos.class);
					if(skuStock != null) {
						skuStock.setSkuPropertyInfos(list);
					}
					
					return skuStock;
				}
			} else {
				LogUtils.sysErrorLoggerError("底层商品库存接口返回数据异常",null);
				return null;
			}
		} catch (Exception e) {
			LogUtils.sysErrorLoggerError(String.format("底层商品库存接口请求失败,%s", response), e);
			return null;
		}
	}
	
	

	public boolean modifyStock(String goodId,Integer quantity) throws OrderException {
		String response = "";
		try {
			ObjectNode node = JsonUtils.createObjectNode();
			node.put("skuId", goodId);
			node.put("quantity", quantity);
			String url = Config.MALL_GOODS_URL+ "/productService/goods/modifyStock?skuId="+goodId+"&quantity="+quantity; // 商品服务接口
			LogUtils.httpLoggerInfo("查询请求的参数:" + JsonUtils.toJsonString(node));
			response = HttpUtils.httpGetRequest(url);
			LogUtils.httpLoggerInfo("商品sku库存接口返回数据结构:" + response);
			if (response != null && !StringUtils.isBlank(response)) {
				JsonNode responseNode = JsonUtils.parseJson(response);
				Integer resultCode = responseNode.path("resultCode").asInt();
				if (!resultCode.equals(BookingConstants.TRIP_GOOD_SERVER_SUCCESS_CODE)) {
					String resultMsg = responseNode.path("resultMsg").asText();
					LogUtils.sysErrorLoggerError("修改商品sku库存接口出错，接口返回错误信息：" + resultMsg,null);
					return false;
				} else {
					return true;
				}
			} else {
				LogUtils.sysErrorLoggerError("修改商品sku库存接口返回数据异常",null);
				return false;
			}
		} catch (Exception e) {
			LogUtils.sysErrorLoggerError(String.format("修改商品sku库存接口请求失败,%s", response), e);
			return false;
		}
	}

}