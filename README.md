![](https://raw.githubusercontent.com/WonderMing13/MarkDownImage/master/RPC.png)

#关于WonderRPC框架

**基于Zookeeper&Netty完成简单的RPC框架并且整合TCC分布式事务以及分布式锁**
![](https://img.shields.io/aur/license/android-studio)
![](https://img.shields.io/badge/language-java-orange.svg)
![](https://img.shields.io/appveyor/ci/gruntjs/grunt)
![](https://img.shields.io/maven-central/v/org.apache.maven/apache-maven)



**[tcc-transaction](https://github.com/changmingxie/tcc-transaction)感谢开源供学习思想**
**[BootNettyRpc](https://github.com/forezp/BootNettyRpc)感谢开源供学习思想**

**基于此进行了扩展,使得RPC框架可以基于SpringBoot使用**

![](https://raw.githubusercontent.com/WonderMing13/MarkDownImage/master/WonderRPC.jpg)

#基础特性

1. 同步与异步调用
2. 强一致性TCC三段式分布式事务
3. 基于ZK的分布式锁
4. 基于SpringBoot使用
5. Zk的负载均衡算法



#How to Use

#### 1.远程调用

##### 1.1消费者Consumer

**消费者添加注解@EnableWonderRpc扫描消费者接口**

```java
@EnableWonderRpc(basePackages = "org.wonderming.service")
@SpringBootApplication
public class CommonApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommonApplication.class,args);
    }
}
```

**添加消费者接口 proxyClass是要调用的提供者的类就是@Service注解的类以及是否同步调用的标识**

```java
@WonderRpcClient(name = "testWonder",proxyClass = "org.wonderming.service.WonderServiceImpl",isSync = true)
public interface ITestService {
     String getTest(TransactionContext transactionContext,String str);
}
```

**添加相关属性,routeStrategy是Zk负载均衡默认（1.随机算法Random,ConsistentHash是哈希一致性) **

```java
wonder:
  zk:
    address: 127.0.0.1:2181
  netty:
    client:
      name: wonder
      routeStrategy: Random
```



##### 1.2 提供者Provider

**提供者启动类添加注解@EnableWonderRpc扫描带有@Service的提供者实现**

```java
@EnableWonderRpc(basePackages = "org.wonderming.service")
@SpringBootApplication
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class,args);
    }
}
```

**创建一个提供者供消费使用**

```java
@Service
public class WonderServiceImpl implements WonderService {
		******
}
```

**添加相关属性**

```java
wonder:
  zk:
    address: 127.0.0.1:2181
  netty:
    server:
      host: 127.0.0.1
      port: 8000
```



#### 2.TCC分布式事务

##### 2.1 消费者方

**一个根事务发起调用诸多分支事务,根事务必须要添加@TccTransaction以完成Try-Confirm-Cancel,并且同一类中添加确认方法和取消方法调用的消费者接口也需要添加MethodUtil.getConsumerTransactionContext()来传递事务上下文信息**

```java
@Service
public class MyTestServiceImpl implements MyTestService {
    
  	//消费者1
    @Resource
    private ITestService iTestService;
		
  	//消费者2
    @Resource
    private IWonderService iWonderService;

    @Override
    @TccTransaction(confirmMethod = "test1",cancelMethod = "test2")
    public String test(){
        System.out.println("开始处理逻辑");
        //传递事务上下文
        final String hjp = iTestService.getTest(MethodUtil.getConsumerTransactionContext(), "HJP");
        final String xjx = iWonderService.getWonder(MethodUtil.getConsumerTransactionContext(), "XJX");
        System.out.println("处理完成逻辑");
        return hjp + xjx;
    }

    public String test1(){
        System.out.println("confirm方法");
        return "ok";
    }

    public String test2(){
        System.out.println("cancel方法");
        return "ok";
    }
}
```

**添加相应的属性告知RPC框架此为根事务**

```java
wonder:
    tcc:
      type: root
```

##### 2.2 提供者方

**在各自相对应的提供者实现类中实现逻辑并且标注此为提供者并且添加Try-Confirm-Cancel**

```java
@Service
public class WonderServiceImpl implements WonderService {

    @Override
    @TccTransaction(confirmMethod = "wonder1",cancelMethod = "wonder2",type = MethodType.PROVIDER)
    public String getTest(TransactionContext transactionContext,String str) {
        System.out.println("开始分支TRY事务");
        return "hi" + str;
    }

    public String wonder1(TransactionContext transactionContext,String str){
        System.out.println("transactionContext："+transactionContext);
        System.out.println("str："+str);
        return "okConfirm";
    }

    public String wonder2(TransactionContext transactionContext,String str){
        System.out.println("transactionContext："+transactionContext);
        System.out.println("str："+str);
        return "okCancel";
    }
 }
```

**添加相应的属性为分支事务**

```java
wonder:
    tcc:
      type: branch
```

