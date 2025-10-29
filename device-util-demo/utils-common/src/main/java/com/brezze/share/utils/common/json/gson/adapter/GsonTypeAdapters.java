package com.brezze.share.utils.common.json.gson.adapter;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;

public class GsonTypeAdapters {

    /**
     * 保留小数位
     */
    private static final int DEFAULT_NUMBER_SCALE = 2;

    /**
     * String类型适配器
     */
    public static final TypeAdapter<String> STRING = new TypeAdapter<String>() {
        @Override
        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }

        @Override
        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null) {
                writer.value("");
                return;
            }
            writer.value(value);
        }
    };

    /**
     * StringBuffer类型适配器
     */
    public static final TypeAdapter<StringBuffer> STRING_BUFFER = new TypeAdapter<StringBuffer>() {
        @Override
        public StringBuffer read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return new StringBuffer("");
            }
            return new StringBuffer(reader.nextString());
        }

        @Override
        public void write(JsonWriter writer, StringBuffer value) throws IOException {
            if (value == null) {
                writer.value("");
                return;
            }
            writer.value(value.toString());
        }
    };

    /**
     * StringBuilder类型适配器
     */
    public static final TypeAdapter<StringBuilder> STRING_BUILDER = new TypeAdapter<StringBuilder>() {
        @Override
        public StringBuilder read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return new StringBuilder("");
            }
            return new StringBuilder(reader.nextString());
        }

        @Override
        public void write(JsonWriter writer, StringBuilder value) throws IOException {
            if (value == null) {
                writer.value("");
                return;
            }
            writer.value(value.toString());
        }
    };

    /**
     * Integer类型适配器
     */
    public static final TypeAdapter<Integer> INTEGER = new TypeAdapter<Integer>() {
        @Override
        public Integer read(JsonReader in) throws IOException {
            JsonToken jsonToken = in.peek();
            switch (jsonToken) {
                case NULL:
                    in.nextNull();
                    return null;
                case NUMBER:
                    return in.nextInt();
                default:
                    throw new JsonSyntaxException("Expecting Integer, got: " + jsonToken);
            }
        }

        @Override
        public void write(JsonWriter out, Integer value) throws IOException {
            if (value != null) {
                out.value(value);
            } else {
                out.value(0);
            }
        }
    };

    /**
     * Float类型适配器
     */
    public static final TypeAdapter<Float> FLOAT = new TypeAdapter<Float>() {
        @Override
        public Float read(JsonReader in) throws IOException {
            JsonToken jsonToken = in.peek();
            switch (jsonToken) {
                case NULL:
                    in.nextNull();
                    return 0f;
                case NUMBER:
                    return (float) in.nextDouble();
                default:
                    throw new JsonSyntaxException("Expecting Float, got: " + jsonToken);
            }
        }

        @Override
        public void write(JsonWriter out, Float value) throws IOException {
            if (value != null) {
                out.value(
                        new BigDecimal(value.floatValue())
                                .setScale(DEFAULT_NUMBER_SCALE,BigDecimal.ROUND_HALF_UP)
                                .floatValue()
                );
            } else {
                out.value(0);
            }
        }
    };

    /**
     * Double类型适配器
     */
    public static final TypeAdapter<Double> DOUBLE = new TypeAdapter<Double>() {
        @Override
        public Double read(JsonReader in) throws IOException {
            JsonToken jsonToken = in.peek();
            switch (jsonToken) {
                case NULL:
                    in.nextNull();
                    return 0d;
                case NUMBER:
                    return in.nextDouble();
                default:
                    throw new JsonSyntaxException("Expecting Double, got: " + jsonToken);
            }
        }

        @Override
        public void write(JsonWriter out, Double value) throws IOException {
            if (value != null) {
                out.value(
                        new BigDecimal(value.doubleValue())
                                .setScale(DEFAULT_NUMBER_SCALE,BigDecimal.ROUND_HALF_UP)
                                .doubleValue()
                );
            } else {
                out.value(0);
            }
        }
    };

    /**
     * BigDecimal类型适配器
     */
    public static final TypeAdapter<BigDecimal> BIG_DECIMAL = new TypeAdapter<BigDecimal>() {
        @Override
        public BigDecimal read(JsonReader in) throws IOException {
            JsonToken jsonToken = in.peek();
            switch (jsonToken) {
                case NULL:
                    in.nextNull();
                    return BigDecimal.ZERO;
                case NUMBER:
                    return new BigDecimal(in.nextString());
                default:
                    throw new JsonSyntaxException("Expecting BigDecimal, got: " + jsonToken);
            }
        }

        @Override
        public void write(JsonWriter out, BigDecimal value) throws IOException {
            if (value != null) {
                out.value(value.setScale(DEFAULT_NUMBER_SCALE,BigDecimal.ROUND_HALF_UP));
            } else {
                out.value(0);
            }
        }
    };


    private GsonTypeAdapters() {}
}
