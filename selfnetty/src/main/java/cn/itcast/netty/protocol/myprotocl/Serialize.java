package cn.itcast.netty.protocol.myprotocl;

import cn.itcast.netty.protocol.myprotocl.message.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
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
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
                String json = new String(bytes, StandardCharsets.UTF_8);
                return gson.fromJson(json, clazz);
            }

            @Override
            public <T> byte[] serialize(T obj) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
                String json = gson.toJson(obj);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        };

        class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

            @Override
            public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                try {
                    String str = json.getAsString();
                    return Class.forName(str);
                } catch (ClassNotFoundException e) {
                    throw new JsonParseException(e);
                }
            }

            @Override             //   String.class
            public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
                // class -> json
                return new JsonPrimitive(src.getName());
            }
        }
    }

}
