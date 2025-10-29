package com.brezze.share.utils.common.json.gson.adapter.factory;

import com.brezze.share.utils.common.json.gson.adapter.GsonTypeAdapters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;

/**
 * NullIntegerToZeroAdapterFactory
 *
 * @Author penf
 * @Description 处理null数字类型(Integer/Float/Double/BigDecimal)适配器工厂类
 * @Date 2018/09/02 15:35
 */
public class NullNumberToZeroAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class rawType = (Class) type.getRawType();
        if (rawType == Integer.class) {
            return (TypeAdapter) GsonTypeAdapters.INTEGER;
        }
        if (rawType == Float.class) {
            return (TypeAdapter) GsonTypeAdapters.FLOAT;
        }
        if (rawType == Double.class) {
            return (TypeAdapter) GsonTypeAdapters.DOUBLE;
        }
        if (rawType == BigDecimal.class) {
            return (TypeAdapter) GsonTypeAdapters.BIG_DECIMAL;
        }

        return null;
    }

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().serializeNulls()
                //.registerTypeAdapterFactory( new NullStringToEmptyAdapterFactory())
                //.registerTypeAdapterFactory( new NullNumberToZeroAdapterFactory())
                //.registerTypeAdapter(Integer.class, GsonTypeAdapters.INTEGER)
//                .registerTypeAdapter(Float.class, GsonTypeAdapters.FLOAT)
//                .registerTypeAdapter(Double.class, GsonTypeAdapters.DOUBLE)
//                .registerTypeAdapter(BigDecimal.class, GsonTypeAdapters.BIG_DECIMAL)
                        .create();


        System.out.println("end");
    }


}
