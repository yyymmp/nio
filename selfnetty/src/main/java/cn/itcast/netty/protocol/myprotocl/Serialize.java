package cn.itcast.netty.protocol.myprotocl;

import cn.itcast.netty.protocol.myprotocl.message.Message;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
        }
    }
}
