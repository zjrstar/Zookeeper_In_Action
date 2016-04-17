package com.zjrstar.main;

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
public class SyscConfig implements Watcher {

    private static final String host = "127.0.0.1";
    private static final String port = "2181";
    private static final int sessionTimeout = 1000;
    private static final String znode = "/catepl";
    private ZooKeeper zk;
    private CountDownLatch cdl = new CountDownLatch(1);

    public void process(WatchedEvent watchedEvent) {
        if ( watchedEvent.getState().SyncConnected == Event.KeeperState.SyncConnected ) {
            cdl.countDown();
        }
    }

    public SyscConfig() {
        try {
            zk = new ZooKeeper(host + ":" + port, sessionTimeout, this);
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void createNode(String data) {
        try {
            if ( zk.exists(znode, this) != null ) {
                try {
                    zk.setData(znode, data.getBytes(), -1);
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                } catch ( KeeperException e ) {
                    e.printStackTrace();
                }
            } else {
                zk.create(znode, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch ( KeeperException e ) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            zk.close();
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SyscConfig syscConfig = new SyscConfig();
        syscConfig.createNode("zhenglong");
        syscConfig.close();
    }
}
