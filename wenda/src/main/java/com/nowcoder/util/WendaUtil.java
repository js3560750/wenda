package com.nowcoder.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import java.security.MessageDigest;
import java.util.Map;

/**
 * 工具包
 * @author 18894
 *
 */
public class WendaUtil {
    private static final Logger logger = LoggerFactory.getLogger(WendaUtil.class);
    
    //用户未登录发表问题时给一个默认的用户ID
    public static int ANONYMOUS_USERID = 3;
    
    //事件处理器LikeHandler要用到的管理员默认ID
    public static int SYSTEM_USERID = 4;
    
    
    /**
     *有关JSON的工具
     */
    public static String getJSONString(int code){
    	//在pom.xml中导入的阿里巴巴JSON开发包
    	JSONObject json = new JSONObject();	//在pom.xml中导入的阿里巴巴JSON开发包！！！！！！！！！！！！！！！！！！！！！
    	//存入键值对
    	json.put("code", code);
    	//返回成JSON格式的字符串
    	return json.toJSONString();
	
    }
    
    
    public static String getJSONString(int code,String msg){
    	//在pom.xml中导入的阿里巴巴JSON开发包
    	JSONObject json = new JSONObject();
    	//存入键值对
    	json.put("code", code);
    	json.put("msg", msg);
    	//返回成JSON格式的字符串
    	return json.toJSONString();
	
    }
    
    public static String getJSONString(int code,Map<String, Object> map){
    	//在pom.xml中导入的阿里巴巴JSON开发包
    	JSONObject json = new JSONObject();
    	//存入键值对
    	json.put("code", code);
    	//遍历MAP对象存入JSON
    	for(Map.Entry<String, Object> entry:map.entrySet()){
    		json.put(entry.getKey(), entry.getValue());
    	}
    	//返回成JSON格式的字符串
    	return json.toJSONString();
	
    }

    /*
     * 
     * 这是一个MD5的算法，可以把传入的字符串进行MD5加密
     * 
     */
    public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            logger.error("生成MD5失败", e);
            return null;
        }
    }
}
