package com.zjrstar.test;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Jerry on 4/17/16.
 */
public class ZKTest {

    static ZooKeeper zk = null;
    static CountDownLatch cdl = new CountDownLatch(1);

    static {
        try {
            zk = new ZooKeeper("127.0.0.1:2181", 1000, new MyWatcher(cdl));
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            cdl.await();
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
        try {
            zk.create("/s", "test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch ( KeeperException e ) {
            e.printStackTrace();
        }
    }

    static class MyWatcher implements Watcher {

        private CountDownLatch countDownLatch;

        public MyWatcher(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        public void process(WatchedEvent watchedEvent) {
            if ( watchedEvent.getState() == Event.KeeperState.SyncConnected ) {
                System.err.println("连接上");
                countDownLatch.countDown();
            }
        }
    }
}
