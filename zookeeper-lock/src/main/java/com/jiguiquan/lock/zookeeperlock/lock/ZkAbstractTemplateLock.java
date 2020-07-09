package com.jiguiquan.lock.zookeeperlock.lock;

import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.CountDownLatch;

/**
 * 使用“模板设计模式：将骨架定义为抽象类，将具体的实现交付给子类自己去完成”
 */
public abstract class ZkAbstractTemplateLock implements ZkLock {
    // 配置ZkClient的连接参数
    private static final String ZK_URL = "192.168.174.141:2181";
    private static final int TIME_OUT = 45*1000;
    ZkClient zkClient = new ZkClient(ZK_URL, TIME_OUT);

    protected String path = "/jgqlock";  //想在Zookeeper中创建的临时节点
    //使用CountDownLatch是为了让waitZkLock主程序停在那不向下走，但是当path节点变化时，就可以向下走；
    protected CountDownLatch countDownLatch = null;

    @Override
    public void zkLock() {
        //因为不知道能不能加锁成功，所以需要tryLock()
        if(tryZkLock()){

        }else {
            waitZkLock();  //注意此处不会产生多层的递归调用，耗费内存，因为我们得实现使用的是CountDownLatch堵塞
            zkLock();   //wait结束后，再次尝试获取锁；
        }
    }

    //重点：使用“模板设计模式：将骨架定义为抽象类，将具体的实现交付给子类自己去完成”
    public abstract boolean tryZkLock();
    public abstract void waitZkLock();


    //释放锁比较简单，其实就是关闭客户端连接即可
    @Override
    public void zkUnLock() {
        if (zkClient != null){
            zkClient.close();
        }
    }
}
