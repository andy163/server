package com.pangbohao.server.utils;

import com.alibaba.fastjson.JSONObject;
import com.pangbohao.server.rest.model.GetListResult;
import com.squareup.okhttp.*;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.logging.Logger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Util {

    private static final float OPEN_HOUR = 8f;

    private static final float CLOSE_HOUR = 22f;

    public static String SERVER_ADDRESS = "";// "api.thy360.com";

    // public static final String SERVER_ADDRESS = "weixin.jh920.com";

    private static DecimalFormat floatFormat = new DecimalFormat("###.00");

    public static final String defaultDeliverTime = "快速达(1小时内)";
    public static final String oldDefaultDeliverTime = "即时送";

    private static final Logger logger = Logger.getLogger(Util.class);
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
            Locale.getDefault());
    static SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.getDefault());
    static SimpleDateFormat sdft = new SimpleDateFormat("HHmmss",
            Locale.getDefault());
    private static SimpleDateFormat sdfDate = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.getDefault());
    private static SimpleDateFormat orderIdDf = new SimpleDateFormat(
            "yyyyMMddHHmmssSSS", Locale.getDefault());
    private static SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
    private static SimpleDateFormat sdfMd = new SimpleDateFormat(
            "MM-dd", Locale.getDefault());
    private static Calendar calendar = Calendar.getInstance();

    public static boolean DEBUG = false;

    private final static SortedMap<Float, String> todayTimeMap = new TreeMap<Float, String>();

    static {
        todayTimeMap.put((float) 10.0, "今天  10:30-11:30");
        todayTimeMap.put((float) 14.5, "今天  15:00-16:00");
        todayTimeMap.put((float) 17.5, "今天  18:00-19:00");
        todayTimeMap.put((float) 20.0, "今天  20:30-21:30");
    }

    private final static SortedMap<Float, String> tomorrowTimeMap = new TreeMap<Float, String>();

    static {
        tomorrowTimeMap.put((float) 10.0, "明天  10:30-11:30");
        tomorrowTimeMap.put((float) 14.5, "明天  15:00-16:00");
        tomorrowTimeMap.put((float) 17.5, "明天  18:00-19:00");
        tomorrowTimeMap.put((float) 20.0, "明天  20:30-21:30");
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return sdf.format(date);
    }

    public static String formatDateSecond(Date date) {
        if (date == null) {
            return "";
        }
        return sdfs.format(date);
    }

    public static String formatTime(Date date) {
        if (date == null) {
            return "";
        }
        return sdft.format(date);
    }

    public static String formatDateDate(Date date) {
        if (date == null) {
            return "";
        }
        return sdfDate.format(date);
    }

    public static String formatDateMd(Date date) {
        if (date == null) {
            return "";
        }
        return sdfMd.format(date);
    }

    public static String formatDate(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat f = new SimpleDateFormat(format, Locale.getDefault());
        return f.format(date);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            // logger.debug("fromJson:{0} of clazz {1}", clazz, json);
            return JSONObject.parseObject(json, clazz);
//			return gson.fromJson(json, clazz);
        } catch (Exception ex) {
            logger.error("Error:" + json, ex);
            return null;
        }
    }

    public static <T> T fromJson(String json, Type type) {
        try {
            return JSONObject.parseObject(json, type);
        } catch (Exception ex) {
            logger.error("Error:" + json, ex);
            return null;
        }
    }

    public static String toJson(Object o) {
        try {
            return JSONObject.toJSONString(o);
        } catch (Exception ex) {
            logger.error("Error:" + o, ex);
            return null;
        }
    }

    public static List<Map<String, String>> getDeliverTime(int regionId) {

        float openHour = OPEN_HOUR;
        float closeHour = CLOSE_HOUR;
        SortedMap<Float, String> todayMap = todayTimeMap;
        SortedMap<Float, String> tomorrowMap = tomorrowTimeMap;

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        float currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        float hour = currentHour;
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        hour = (float) (hour + 0.5);// 从下个半小时开始
        if (minute > 30) {
            hour = (float) (hour + 0.5);
        }

        for (float key : todayMap.keySet()) {
            if (key >= hour && key >= openHour && key <= closeHour) {
                String time = todayMap.get(key);
                if (time != null) {
                    list.add(Collections.singletonMap("name", todayMap.get(key)));
                }
                hour = (float) (hour + 0.5);
            }
        }

        for (float key : tomorrowMap.keySet()) {
            // if (key < hour && key > openHour && key <= closeHour) {
            if (key > openHour && key <= closeHour) {

                String time = tomorrowMap.get(key);
                if (time != null) {
                    list.add(Collections.singletonMap("name", time));
                }
                hour = (float) (hour + 0.5);
            }
        }

        if (currentHour >= openHour && currentHour < closeHour) {
            list.add(Collections.singletonMap("name", defaultDeliverTime));
        }

        return list;
    }

    public static String genSerialNo() {
        return genNumberCode(8);

    }

    public static String createSmsCode() {
        return genNumberCode(4);
    }

    private static String genNumberCode(int length) {

        String code = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            code += random.nextInt(10);
        }
        return code;

    }

    public static String formatFloatStr(double balanceAmount) {
        String str = floatFormat.format(balanceAmount);
        if (str.startsWith(".")) {
            str = "0" + str;
        }
        return str;
    }

    public static float formatFloat(double balanceAmount) {
        String v = floatFormat.format(balanceAmount);
        return Float.parseFloat(v);
    }

    public static SortedMap<String, String> toSortedMap(Object o) {
        try {
            SortedMap<String, String> map = new TreeMap<String, String>();
            Field[] fields = o.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                String fieldName = fields[i].getName();
                Object value = getFieldValueByName(fieldName, o);
                if (value != null) {
                    String v = value.toString();
                    if (!StringUtils.isEmpty(v)) {
                        map.put(fieldName, v);
                    }
                }
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase(
                    Locale.ENGLISH);
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {
            return null;
        }

    }

    public static void fillRtnPageDataRedis(GetListResult<?> rtn, long size,
                                            int pageSize, int currentPageSize, long pageNumber) {
        int mod = (size % pageSize) > 0 ? 1 : 0;
        long totalPages = size / pageSize + mod;
        rtn.setTotalpage(totalPages);
        boolean hasNext = true;
        if (currentPageSize < pageSize) {
            hasNext = false;
        } else if (currentPageSize == pageSize && pageNumber >= totalPages) {
            hasNext = false;
        }
        rtn.setHasNext(hasNext);
        rtn.setCount(currentPageSize);// 当前页条数
        rtn.setTotal(size);// 总条数
        // int currentPage = (int) Math.min(pageNumber, totalPages);
        rtn.setCurrPage(pageNumber);
    }

    public static String genString(Object... as) {
        StringBuffer bf = new StringBuffer();
        for (Object s : as) {
            bf.append(s);
        }
        return bf.toString();
    }

    public static Date toDate(String startTime) {
        try {
            return sdf.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Date toDateDate(String startTime) {
        try {
            return sdfDate.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date toDateSecond(String startTime) {
        try {
            return sdfs.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date toTime(String startTime) {
        try {
            return sdft.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public final static String MD5(byte[] buffer) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
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
            return null;
        }
    }


    public static Map<String, String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            // 设置支持命名空间
            factory.setNamespaceAware(true);
            // 2.生成parser对象
            XmlPullParser parser = factory.newPullParser();
            // 3.设置输入
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:
                        if ("xml".equals(nodeName) == false) {
                            // 实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String httpGet(String url) {
        return httpGet(url, null);
    }

    public static String httpGet(String url, Map<String, Object> headersMap) {
        logger.debug("http get: {0}", url);
        OkHttpClient client = new OkHttpClient();
        Request.Builder requestBuild = new Request.Builder().url(url);
        if (headersMap != null && !headersMap.isEmpty()) {
            headersMap.forEach((k, v) -> requestBuild.addHeader(k, String.valueOf(v)));
        }
        Request request = requestBuild.build();
        try {
            Response response = client.newCall(request).execute();
            logger.debug("response.code() is {0}", response.code());

            if (response.code() == 200) {
                String body = response.body().string();
                logger.debug("response.body is {0}", body);

                return body;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String httpPost(String url, Map<String, Object> paramsMap) {
        return httpPost(url, paramsMap, null);
    }

    public static String httpPost(String url, Map<String, Object> paramsMap, Map<String, Object> headersMap) {
        logger.debug("http form post: {0}, {1}, {2}", url, paramsMap, headersMap);
        try {
            Request.Builder requestBuild = new Request.Builder();

            Map<String, Object> defaultHeader = new HashMap<>();
            defaultHeader.put("Content-Type", "application/json; charset=utf-8;");
            defaultHeader.put("Accept", "application/json; charset=utf-8;");
            if (headersMap != null && !headersMap.isEmpty()) {
                defaultHeader.putAll(headersMap);
            }
            defaultHeader.forEach((k, v) -> requestBuild.addHeader(k, String.valueOf(v)));
            RequestBody requestBody;
            if (String.valueOf(defaultHeader.get("Content-Type")).contains("application/json")) {
                requestBody = RequestBody.create(
                        MediaType.parse("application/json; charset=utf-8;"),
                        Util.toJson(paramsMap));
            } else {
                FormEncodingBuilder bodyBuilder = new FormEncodingBuilder();
                if (paramsMap != null && !paramsMap.isEmpty()) {
                    paramsMap.forEach((k, v) -> bodyBuilder.add(k, String.valueOf(v)));
                }
                requestBody = bodyBuilder.build();
            }

            Request request = requestBuild.url(url).post(requestBody)
                    .build();

            Response response = new OkHttpClient().newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                logger.debug("http form post result: {0}", result);
                return result;
            } else {
                throw new IOException("Unexpected code " + response);
            }

        } catch (Exception e) {
            logger.error("http request url: {0},throw exception: {1}", url, e);
        }
        return null;
    }

    public static String httpPost(String mediaType, String url, String content) {
        return httpPost(mediaType, url, content, null);
    }

    public static String httpPost(String mediaType, String url, String content,
                                  Map<String, String> headers) {
        logger.debug("httpPost: {0}", url);
        logger.debug("httpPost content: {0}", content);
        OkHttpClient client = new OkHttpClient();
        RequestBody bodyContent = RequestBody.create(
                MediaType.parse(mediaType), content);
        com.squareup.okhttp.Request.Builder builder = new Request.Builder()
                .url(url).post(bodyContent);
        if (headers != null && headers.keySet() != null && headers.keySet().size() > 0) {
            logger.debug("httpPost headers: {0}", Util.toJson(headers));
            for (String name : headers.keySet()) {
                builder.header(name, headers.get(name));
            }
        }
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            logger.debug("httpPost: response code {0}", response.code());
            logger.debug("httpPost: response body {0}", body);

            if (response.code() == 200) {
                return body;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String httpPut(String mediaType, String url, String content,
                                  Map<String, String> headers) {
        logger.debug("httpPut: {0}", url);
        logger.debug("httpPut content: {0}", content);
        OkHttpClient client = new OkHttpClient();
        RequestBody bodyContent = RequestBody.create(
                MediaType.parse(mediaType), content);
        com.squareup.okhttp.Request.Builder builder = new Request.Builder()
                .url(url).put(bodyContent);
        if (headers != null && headers.keySet() != null && headers.keySet().size() > 0) {
            logger.debug("httpPut headers: {0}", Util.toJson(headers));
            for (String name : headers.keySet()) {
                builder.header(name, headers.get(name));
            }
        }
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            logger.debug("httpPut: response code {0}", response.code());
            logger.debug("httpPut: response body {0}", body);

            if (response.code() == 200) {
                return body;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
    public static boolean isJhsqApp(String appType) {
        return StringUtils.equals(appType, "jhsq");
    }

    public static boolean isJhpsApp(String appType) {
        return StringUtils.equals(appType, "jhps");
    }

    public static boolean isJhsjApp(String appType) {
        return StringUtils.equals(appType, "jhsj");
    }

    public static String createOutTradeOrderId(long orderId) {
        return orderIdDf.format(new Date()) + orderId;
    }

    public static String parseOrderIdFromOutTradeOrderId(String oOId) {
        // 去掉yyyyMMddHHmmssSSS前缀
        return StringUtils.substring(oOId, 17);
    }

    public static String messageFormat(String var1, Object... var2) {
        if (var2 == null || var2.length == 0) {
            return var1;
        }
        Object[] var3 = new Object[var2.length];
        for (int i = 0; i < var2.length; i++) {
            if (var2[i] == null) {
                var3[i] = "";
                continue;
            }
            var3[i] = var2[i] instanceof String ? var2[i] : String.valueOf(var2[i]);
        }
        return MessageFormat.format(var1, var3);
    }

    /***
     * 或一天内剩余时间
     * @return
     */
    public static long time() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long diff = cal.getTimeInMillis() - System.currentTimeMillis();
        return diff;
    }

    public static Date getBeforeDate(Date date, Integer days) {
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.MILLISECOND, 00);
        calendar.add(Calendar.DATE, days);

        return calendar.getTime();

    }

    public static Date getAfterDate(Date date, Integer days) {
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 59);
        calendar.add(Calendar.DATE, days);

        return calendar.getTime();

    }

    public static String getWeekInfo(Date date) {
        if (date == null) {
            return "";
        }
        return dateFm.format(date);
    }

    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static int getHour(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static String orderJson(String json) {
        String resultBody = "";
        if (!StringUtils.isEmpty(json)) {
            JSONObject jsonObject = JSONObject.parseObject(json);
            if (jsonObject!=null){
                LinkedHashMap m = decodeJSONObject(jsonObject);
                if (m != null & m.size() > 0) {
                    List<Map.Entry<String, String>> entries = new ArrayList<>(m.entrySet());
                    Collections.sort(entries, new Comparator<Map.Entry<String, String>>() {
                        public int compare(Map.Entry<String, String> a, Map.Entry<String, String> b) {
                            return a.getKey().compareTo(b.getKey());
                        }
                    });
                    Map<String, String> sortedMap = new LinkedHashMap<>();
                    for (Map.Entry<String, String> entry : entries) {
                        sortedMap.put(entry.getKey(), entry.getValue());
                    }
                    resultBody = Util.toJson(sortedMap);
                }
            }
        } else {
            resultBody = json;
        }
        return resultBody;
    }

    private static LinkedHashMap decodeJSONObject(JSONObject jsonObject) {
        Set<String> set = jsonObject.keySet();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (String key : set) {
            if (jsonObject.get(key) instanceof JSONObject) {
                JSONObject jo = (JSONObject) jsonObject.get(key);
                if (jo.keySet().size() > 0) {
                    LinkedHashMap<String, String> map2 = decodeJSONObject(jo);
                    map.putAll(map2);
                }
            } else if (!StringUtils.isEmpty(jsonObject.get(key).toString())) {
                map.put(key, jsonObject.get(key).toString());
            }
        }
        return map;
    }

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

}
