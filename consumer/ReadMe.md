wget https://alibaba.github.io/arthas/arthas-boot.jar
java -jar arthas-boot.jar -h   打印帮助信息
java -jar arthas-boot.jar 选择 com.onejane.demo.consumer.ConsumerApplication所在id
java -jar arthas-boot.jar pid  指定java进程pid
http://127.0.0.1:8563/  访问web console

dashboard -i 5000 -n 2  每5毫秒执行一次dashboard共执行2次

![img](ReadMe/9e3367f74560c0ef95f2a2a9de66bb05.png)

sc -d -f className  查看JVM已加载的类信息,查看类信息，来源，继承关系，类加载器，类加载异常相关问题如 `ClassNotFoundException, ClassCastException, ClassNoDefException…`

sm -d ClassName method 查看已加载类的方法信息，可指定指定类下所有方法名，出入参类型

jad —source-only ClassName 反编译已加载类的源码，判断代码改动是否提交

`thread -n`: 查询占用cpu最高的前n个线程

`thread id`:查看指定线程堆栈

`thread -b`:查看当前阻塞其他线程的线程

watch ClassName method '{params[0],returnObj}' -x n  方法执行数据观测

trace ClassName method  方法内部调用路径，并输出方法路径上的每个节点上耗时

stack ClassName method -n 5 '1==1'  查看方法执行实际调用路径

tt -t ClassName method 查看方法调用记录,查看异常方法，堆栈

tt -i index   指定方法的index查看

tt -i 1000 -w 'target.getApplicationContext().getBean("activityAdapterServiceImpl").findByUid("111")'

ognl '#context=@com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory@contexts.iterator.next, #context.getBean(“activityServiceImpl").findByUid("111")'
————————————————
版权声明：本文为CSDN博主「+YUAN」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/zhang_jiayuan/article/details/109261904

start --event cpu --interval 10000000  支持生成应用热点的火焰图。本质上是通过不断的采样，然后把收集到的采样结果生成火焰图

| **命令**                                                     | **介绍**                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [dashboard](https://alibaba.github.io/arthas/dashboard.html) | 当前系统的实时数据面板，概览程序的 线程、内存、GC、运行环境信息 |
| [**thread**](https://alibaba.github.io/arthas/thread.html)   | 查看当前 JVM 的线程堆栈信息                                  |
| [**watch**](https://alibaba.github.io/arthas/watch.html)     | 方法执行数据观测                                             |
| **[trace](https://alibaba.github.io/arthas/trace.html)**     | 方法内部调用路径，并输出方法路径上的每个节点上耗时           |
| [**stack**](https://alibaba.github.io/arthas/stack.html)     | 输出当前方法被调用的调用路径                                 |
| [**tt**](https://alibaba.github.io/arthas/tt.html)           | 方法执行数据的时空隧道，记录下指定方法每次调用的入参和返回信息，并能对这些不同的时间下调用进行观测 |
| [monitor](https://alibaba.github.io/arthas/monitor.html)     | 方法执行监控                                                 |
| [jvm](https://alibaba.github.io/arthas/jvm.html)             | 查看当前 JVM 信息                                            |
| [vmoption](https://alibaba.github.io/arthas/vmoption.html)   | 查看，更新 JVM 诊断相关的参数                                |
| [sc](https://alibaba.github.io/arthas/sc.html)               | 查看 JVM 已加载的类信息                                      |
| [sm](https://alibaba.github.io/arthas/sm.html)               | 查看已加载类的方法信息                                       |
| [jad](https://alibaba.github.io/arthas/jad.html)             | 反编译指定已加载类的源码                                     |
| [classloader](https://alibaba.github.io/arthas/classloader.html) | 查看 classloader 的继承树，urls，类加载信息                  |
| [heapdump](https://alibaba.github.io/arthas/heapdump.html)   | 类似 jmap 命令的 heap dump 功能                              |

## CPU

```
private static ExecutorService executorService = Executors.newFixedThreadPool(1);
private static void cpuHigh() {
    Thread thread = new Thread(() -> {
        while (true) {
            log.info("cpu start 100");
        }
    });
    // 添加到线程
    executorService.submit(thread);
}
```

thread  查看所有现场，列出每个线程的cpu使用率及线程id

thread -n x  排列出 CPU 使用率 **Top N** 的线程

thread id  查看消耗cpu最高的线程堆栈信息，定位cpu使用最高的方法

![image-20210318155346020](ReadMe/image-20210318155346020.png)

## 线程阻塞

```
private static void thread() {
    Thread thread = new Thread(() -> {
        while (true) {
            log.debug("thread start");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });
    // 添加到线程
    executorService.submit(thread);
}
```

上面定义了线程池大小为1 的线程池executorService，然后在 `cpuHigh` 方法里提交了一个线程，在 `thread`方法再次提交了一个线程，后面的这个线程因为线程池已满，会阻塞下来。

![image-20210318155624887](ReadMe/image-20210318155624887.png)

## 线程死锁

```
private static void deadThread() {
    /** 创建资源 */
    Object resourceA = new Object();
    Object resourceB = new Object();
    // 创建线程
    Thread threadA = new Thread(() -> {
        synchronized (resourceA) {
            log.info(Thread.currentThread() + " get ResourceA");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info(Thread.currentThread() + "waiting get resourceB");
            synchronized (resourceB) {
                log.info(Thread.currentThread() + " get resourceB");
            }
        }
    });

    Thread threadB = new Thread(() -> {
        synchronized (resourceB) {
            log.info(Thread.currentThread() + " get ResourceB");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info(Thread.currentThread() + "waiting get resourceA");
            synchronized (resourceA) {
                log.info(Thread.currentThread() + " get resourceA");
            }
        }
    });
    threadA.start();
    threadB.start();
}
```

 **thread -b** 命令查看直接定位到死锁信息

![image-20210318160434405](ReadMe/image-20210318160434405.png)

## 反编译

 jad com.onejane.demo.consumer.utils.ArthasUtils

![image-20210318160705311](ReadMe/image-20210318160705311.png)

jad com.onejane.demo.consumer.utils.ArthasUtils thread

![image-20210318160812927](ReadMe/image-20210318160812927.png)

## 类信息

sc -d -f com.onejane.demo.consumer.utils.ArthasUtils  字段信息

![image-20210318161116309](ReadMe/image-20210318161116309.png)

sm com.onejane.demo.consumer.utils.ArthasUtils  类方法信息

![image-20210318161224620](ReadMe/image-20210318161224620.png)

ognl '@com.onejane.demo.consumer.utils.ArthasUtils@hashSet'  查看静态变量内容

ognl '@com.onejane.demo.consumer.utils.ArthasUtils@hashSet.size()'  查看静态变量大小

```
watch com.taobao.container.Test test "params[0]"  查看第一个参数
watch com.taobao.container.Test test "params[0].size()"  查看第一个参数的test
watch com.taobao.container.Test test "@java.lang.Thread@currentThread()" 调用静态方法
watch com.taobao.container.Test test "@java.lang.Thread@currentThread().getContextClassLoader()"  调用静态方法再调用非静态方法
```

## 线程跟踪

trace com.onejane.demo.consumer.controller.UserInfoController getUser  获取方法耗时最长的是UserServiceImpl.get()

trace com.onejane.demo.consumer.service.UserServiceImpl get   继续跟踪耗时高的方法，然后再次访问

monitor -c 5 com.onejane.demo.consumer.service.UserServiceImpl get 统计方法耗时

```
watch com.onejane.demo.consumer.utils.ArthasUtils addHashSet '{params[0],returnObj}'  查看入参和出参
watch com.onejane.demo.consumer.utils.ArthasUtils addHashSet '{params[0],returnObj.size}'  查看入参和出参大小
watch com.onejane.demo.consumer.utils.ArthasUtils addHashSet '{params[0],returnObj.contains("count10")}'  查看入参和出参中是否包含 'count10' 
watch com.onejane.demo.consumer.utils.ArthasUtils addHashSet '{params[0],returnObj.toString()}'  查看入参和出参，出参 toString
stack com.onejane.demo.consumer.service.UserServiceImpl mysql 观察 类com.UserServiceImpl的 mysql 方法调用路径
tt -t com.onejane.demo.consumer.service.UserServiceImpl check  记录下指定方法每次调用的入参和返回信息
tt -i index -p   针对指定方法的index发起重新调用
```





















