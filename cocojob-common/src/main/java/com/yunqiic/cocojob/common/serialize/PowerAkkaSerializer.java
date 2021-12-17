package com.yunqiic.cocojob.common.serialize;

import akka.serialization.JSerializer;

/**
 * Using custom serializers for akka-remote
 * https://doc.akka.io/docs/akka/current/serialization.html
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class PowerAkkaSerializer extends JSerializer {

    @Override
    public Object fromBinaryJava(byte[] bytes, Class<?> manifest) {
        return SerializerUtils.deSerialized(bytes);
    }

    @Override
    public int identifier() {
        return 277777;
    }

    @Override
    public byte[] toBinary(Object o) {
        return SerializerUtils.serialize(o);
    }

    @Override
    public boolean includeManifest() {
        return false;
    }
}
