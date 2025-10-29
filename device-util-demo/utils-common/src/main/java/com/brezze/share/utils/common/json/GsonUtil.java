package com.brezze.share.utils.common.json;

import com.brezze.share.utils.common.json.gson.adapter.factory.NullStringToEmptyAdapterFactory;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.util.List;
import java.util.Map;

@Slf4j
public class GsonUtil {
    //序列化
    final static JsonSerializer<LocalDateTime> jsonSerializerDateTime =
            (localDateTime, type, jsonSerializationContext) ->
                    new JsonPrimitive(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

    final static JsonSerializer<LocalDate> jsonSerializerDate =
            (localDate, type, jsonSerializationContext) ->
                    new JsonPrimitive(localDate.atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
    //反序列化
    final static JsonDeserializer<LocalDateTime> jsonDeserializerDateTime =
            (jsonElement, type, jsonDeserializationContext) ->
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(jsonElement.getAsJsonPrimitive().getAsLong()), ZoneId.systemDefault());

    final static JsonDeserializer<LocalDate> jsonDeserializerDate =
            (jsonElement, type, jsonDeserializationContext) -> {
                LocalDateTime utc = LocalDateTime.ofInstant(Instant.ofEpochMilli(jsonElement.getAsJsonPrimitive().getAsLong()), ZoneId.systemDefault());
                return LocalDate.of(utc.getYear(), utc.getMonth(), utc.getDayOfMonth());
            };

    /**
     * gson对象（不过滤空值），线程安全的
     */
    private static final Gson GSON;
    /**
     * gson对象（不过滤空值）
     */
    private static final Gson GSON_NULL;
    /**
     * gson对象（null值转字符串）
     */
    private static final Gson GSON_NULL_TO_EMPTY;
    /**
     * 过滤空值
     */
    public static final int FILTER_NULL = 1;
    /**
     * 不过滤空值
     */
    public static final int FILTER_NOT = 0;
    /**
     * 不过滤空值
     */
    public static final int NULL_CONVERT_EMPTY = 2;

    static {
        GSON = new GsonBuilder()
                // 当Map的key为复杂对象时,需要开启该方法
                //.enableComplexMapKeySerialization()
                // 当字段值为空或null时，依然对该字段进行转换
                .serializeNulls()
                // 序列化日期格式  "yyyy-MM-dd HH:mm"
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(LocalDateTime.class, jsonSerializerDateTime)
                .registerTypeAdapter(LocalDate.class, jsonSerializerDate)
                .registerTypeAdapter(LocalDateTime.class, jsonDeserializerDateTime)
                .registerTypeAdapter(LocalDate.class, jsonDeserializerDate)
                //反序列化整形数字转Double问题
//                .registerTypeAdapter(TreeMap.class, treeMapDeserialize)
                // 自动格式化换行
                //.setPrettyPrinting()
                // 防止特殊字符出现乱码
                .disableHtmlEscaping()
                .create();

        GSON_NULL = new GsonBuilder()
                // 当Map的key为复杂对象时,需要开启该方法
                .enableComplexMapKeySerialization()
                // 当字段值为空或null时，依然对该字段进行转换
                //.serializeNulls()
                // 序列化日期格式  "yyyy-MM-dd HH:mm"
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(LocalDateTime.class, jsonSerializerDateTime)
                .registerTypeAdapter(LocalDate.class, jsonSerializerDate)
                .registerTypeAdapter(LocalDateTime.class, jsonDeserializerDateTime)
                .registerTypeAdapter(LocalDate.class, jsonDeserializerDate)
                //反序列化整形数字转Double问题
//                .registerTypeAdapter(TreeMap.class, treeMapDeserialize)
                // 自动格式化换行
                //.setPrettyPrinting()
                // 防止特殊字符出现乱码
                .disableHtmlEscaping()
                .create();

        GSON_NULL_TO_EMPTY = new GsonBuilder()
                // 当Map的key为复杂对象时,需要开启该方法
                .enableComplexMapKeySerialization()
                // 当字段值为空或null时，依然对该字段进行转换
                .serializeNulls()
                .registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory())
                .registerTypeAdapter(LocalDateTime.class, jsonSerializerDateTime)
                .registerTypeAdapter(LocalDate.class, jsonSerializerDate)
                .registerTypeAdapter(LocalDateTime.class, jsonDeserializerDateTime)
                .registerTypeAdapter(LocalDate.class, jsonDeserializerDate)
                //反序列化整形数字转Double问题
//                .registerTypeAdapter(TreeMap.class, treeMapDeserialize)
                // 序列化日期格式  "yyyy-MM-dd HH:mm"
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                // 自动格式化换行
                //.setPrettyPrinting()
                // 防止特殊字符出现乱码
                .disableHtmlEscaping()
                .create();
    }

    /**
     * 获取gson解析器（不过滤空值）
     *
     * @return
     */
    public static Gson getGson() {
        return GSON;
    }

    /**
     * 获取gson解析器（过滤空值）
     *
     * @return
     */
    public static Gson getWriteNullGson() {
        return GSON_NULL;
    }

    /**
     * 对象转json字符串[默认不过滤空值字段]
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        return toJson(object, FILTER_NOT);
    }

    /**
     * 对象转json字符串
     *
     * @param object
     * @param type   格式类型：0-不过滤空值字段 1-过滤空值字段 2-null值转空字符串
     * @return
     */
    public static String toJson(Object object, int type) {
        if (FILTER_NULL == type) {
            return GSON_NULL.toJson(object);
        } else if (FILTER_NOT == type) {
            return GSON.toJson(object);
        } else if (NULL_CONVERT_EMPTY == type) {
            return GSON_NULL_TO_EMPTY.toJson(object);
        } else {
            return GSON.toJson(object);
        }
    }

    /**
     * json字符串转对象
     *
     * @param json     源字符串
     * @param classOfT 目标对象类型
     * @param <T>
     * @return
     */
    public static <T> T jsonToBean(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    /**
     * json字符串转对象
     *
     * @param json      源字符串
     * @param typeToken 目标对象类型
     * @param <T>
     * @return
     */
    public static <T> T jsonToBean(String json, TypeToken<T> typeToken) {
        return GSON.fromJson(json, typeToken.getType());
    }

    /**
     * json字符串转List
     *
     * @param json
     * @param typeToken
     * @return
     */
    public static <T> List<T> jsonToList(String json, TypeToken<List<T>> typeToken) {
        return GSON.fromJson(json, typeToken.getType());
    }

    /**
     * json字符串转List<Map<String, T>>
     *
     * @param json
     * @return
     */
    public static <T> List<Map<String, T>> jsonToListMap(String json) {
        return GSON.fromJson(json, new TypeToken<List<Map<String, T>>>() {
        }.getType());
    }

    /**
     * json字符串转Map
     *
     * @param json
     * @return
     */
    public static <T> Map<String, T> jsonToMap(String json) {
        return GSON.fromJson(json, new TypeToken<Map<String, T>>() {
        }.getType());
    }

    /**
     * 对象转Map
     *
     * @param object
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> beanToMap(Object object) {
        String json = toJson(object);
        return jsonToMap(json);
    }

    /**
     * Map转对象
     *
     * @param map
     * @param target
     * @return
     */
    public static <T> T mapToBean(Map<String, String> map, Class<T> target) {
        if (map == null) {
            return null;
        }
        String json = toJson(map);
        return jsonToBean(json, target);
    }

    /**
     * 对象转换
     *
     * @param data   数据对象
     * @param target 目标对象
     * @return
     */
    public static <T> T cast(Object data, Class<T> target) {
        try {
            if (data != null) {
                Gson gson = getGson();
                if (data instanceof String) {
                    return gson.fromJson(((String) data), target);
                }

                String toJson = gson.toJson(data);
                return gson.fromJson(toJson, target);
            }
        } catch (Exception e) {
            log.error("data cast exception: {}", e);
        }

        return null;
    }
}
