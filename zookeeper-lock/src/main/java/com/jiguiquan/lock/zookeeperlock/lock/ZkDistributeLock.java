package com.jiguiquan.lock.zookeeperlock.lock;

import org.I0Itec.zkclient.IZkDataListener;

import java.util.concurrent.CountDownLatch;

public class ZkDistributeLock extends ZkAbstractTemplateLock {
    @Override
    public boolean tryZkLock() {
        //尝试创建一个临时节点
        try {
            zkClient.createEphemeral(path);
            return true;  //创建临时节点成功，则代表抢占锁成功
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public void waitZkLock() {
        //在等待锁的过程中，要对刚刚的path临时节点进行监听watch
        //创建监听器
        IZkDataListener iZkDataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                //当监听的节点被删除时，countDownLatch减1
                if (countDownLatch != null){
                    countDownLatch.countDown();
                }
            }
        };

        //注册监听器
        zkClient.subscribeDataChanges(path, iZkDataListener);

        //如果有path这个节点，程序不可以继续向下走，
        // 只有当这个节点down掉我们才能继续向下走，如何阻止主程序往下走，但是却能在path节点发生变化时，就可以继续向下走
        // 我们使用CountDownLatch方式堵塞实现
        if (zkClient.exists(path)){
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //释放监听器
        zkClient.unsubscribeDataChanges(path, iZkDataListener);
    }
}
