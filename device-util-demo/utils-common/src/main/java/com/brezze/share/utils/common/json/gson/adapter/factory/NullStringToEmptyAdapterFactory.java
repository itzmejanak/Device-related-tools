package com.brezze.share.utils.common.json.gson.adapter.factory;

import com.brezze.share.utils.common.json.gson.adapter.GsonTypeAdapters;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * NullStringToEmptyAdapterFactory
 *
 * @Author penf
 * @Description 处理null字符串(String/StringBuffer/StringBuilder)适配器工厂类
 * @Date 2018/09/01 17:32
 */
public class NullStringToEmptyAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class rawType = (Class) type.getRawType();
        if (rawType == String.class) {
            return (TypeAdapter) GsonTypeAdapters.STRING;
        }
        if (rawType == StringBuffer.class) {
            return (TypeAdapter) GsonTypeAdapters.STRING_BUFFER;
        }
        if (rawType == StringBuilder.class) {
            return (TypeAdapter) GsonTypeAdapters.STRING_BUILDER;
        }
        return null;
    }
}
