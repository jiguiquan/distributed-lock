package com.jiguiquan.lock.zookeeperlock.lock;

/**
 * 养成好习惯，做大事前先定义接口
 */
public interface ZkLock {
    public void zkLock();
    public void zkUnLock();
}
