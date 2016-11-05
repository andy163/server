package com.pangbohao.server.utils;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by ps_an on 2016/8/29.
 */
public class Oauth {

    private static final Logger logger = Logger
            .getLogger(Oauth.class);

    /**
     * 产生一个随机的字符串
     * @param length 随机的字符串长度
     * @return
     */
    public static String randomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(str.length());
            buf.append(str.charAt(num));
        }
        return buf.toString();
    }

    /**
     * 获取签名秘钥
     * @param appid 接入的子系统名称
     * @return
     */
    public static String getSecret(String appid) {
        return SystemConfig.getProperty(appid);
    }


    /**
     * 验证请求是否符合加密规则
     * @param headers
     * @param body
     * @return
     */
    public static boolean verifyRequest(String appid,String authorization,MultivaluedMap<String, String> headers,String body){
        logger.debug("request signature="+authorization);
        String signature = getRequestSign(appid,headers,body);
        if (StringUtils.isEmpty(body) ||
                (!StringUtils.isEmpty(signature)
                        && StringUtils.equals(signature,authorization))){
            return true;
        }
        return false;
    }

    public static String getRequestSign(String appid,MultivaluedMap<String, String> headers, String body) {
        return getSign(appid,headers,body);
    }

    public static String getSign(String appid,MultivaluedMap<String, String> signMap, String body) {
        logger.debug("appid="+appid);
        logger.debug("signMap="+signMap.toString());
        logger.debug("body="+body);

        // 过滤空值、sign与sign_type参数
        Map<String, String> sParaNew = paraFilter(getSignMap(signMap));

        StringBuilder sb =new StringBuilder();
//        // 签名步骤一：按字典序排序参数
//        sb.append(createLinkString(sParaNew));
        //由于Python后台的加密规则，这里先这样做
        sb.append(body);
        //签名步骤二：在string后加入KEY
        sb.append("&secret="+getSecret(appid));
        //签名步骤三：MD5加密'
        logger.debug("sb.toString()="+sb.toString());
        String result = Util.MD5(sb.toString().getBytes());
        //签名步骤四：所有字符转为大写
        result = result.toUpperCase();

        logger.debug("signature="+result);
        return result;
    }

    private static Map<String,String> getSignMap(MultivaluedMap<String, String> signMap){
        Map<String,String> map = new TreeMap<>();
        signMap.remove("Authorization");
        for (Map.Entry<String,List<String>> entry :signMap.entrySet()) {
            StringBuilder sb =new StringBuilder();
            if (entry.getValue()!=null && entry.getValue().size()>0) {
                for (int i = 0; i < entry.getValue().size(); i++) {
                    if (i == entry.getValue().size() - 1) {// 拼接时，不包括最后一个&字符
                        try {
                            sb.append(URLEncoder.encode(entry.getValue().get(i), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            sb.append(URLEncoder.encode(entry.getValue().get(i), "UTF-8")).append(",");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            try {
                map.put(URLEncoder.encode(entry.getKey(),"UTF-8"),sb.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray
     *            签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("")
                    || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }

    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params
     *            需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }

    public static void main(String[] args) {
        System.out.println(randomString(32));
    }
}
