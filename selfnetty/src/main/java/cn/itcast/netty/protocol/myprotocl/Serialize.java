package cn.itcast.netty.protocol.myprotocl;

import cn.itcast.netty.protocol.myprotocl.message.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author jlz
 * @date 2022年02月09日 23:41
 */
public interface Serialize {

    <T> T deserialize(Class<T> clazz, byte[] bytes);

    <T> byte[] serialize(T obj);


    enum Algorithm implements Serialize {
        /**
         * 基于java方式实现的序列化方式
         *
         * @author jlz
         * @date 2022/2/9 23:46
         * @param null
         * @return null
         */
        JAVA {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    return (T) ois.readObject();
                } catch (IOException | ClassNotFoundException ioException) {
                    throw new RuntimeException("反序列化失败");
                }
            }

            @Override
            public <T> byte[] serialize(T obj) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(obj);
                    return bos.toByteArray();
                } catch (IOException ioException) {
                    throw new RuntimeException("序列化失败");
                }
            }
        },
        JSON{
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                //字节转字符串
                String string = new String(bytes, StandardCharsets.UTF_8);

                return new Gson().fromJson(string, clazz);
            }

            @Override
            public <T> byte[] serialize(T obj) {
                //将对象转正json
                String json = new Gson().toJson(obj);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        }
    }
}
