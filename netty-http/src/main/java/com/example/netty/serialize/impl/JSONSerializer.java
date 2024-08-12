package com.example.netty.serialize.impl;

import com.alibaba.fastjson.JSON;
import com.example.netty.serialize.Serializer;

/**
 * @author 彭涛
 */
public class JSONSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes,clazz);
    }
}
