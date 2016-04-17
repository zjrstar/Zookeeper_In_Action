package com.zjrstar.main;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Jerry on 4/17/16.
 */
public class ZookeeperExample implements Watcher, AsyncCallback.StatCallback, Runnable {

    private volatile boolean dead = true;
    private static String znode = "/createpl";
    private CopyOnWriteArrayList<String> set = new CopyOnWriteArrayList<String>();
    public ZooKeeper zk;
    public Stat stat;

    public ZookeeperExample() {
        try {
            zk = new ZooKeeper("127.0.0.1:2181", 1000, this);
            if ( zk.exists(znode, this) == null ) {
                zk.create(znode, "招聘|常规招聘=56".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            stat = new Stat();
            System.err.println("第一次获取catpel的数据如下");
            System.err.println(new String(zk.getData(znode, true, stat)));
            System.err.println(stat);
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch ( KeeperException e ) {
            e.printStackTrace();
        }
    }

    public void run() {
        synchronized (this) {
            try {
                wait();
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }

    public void processResult(int i, String s, Object o, Stat stat) {
        System.err.println(11);
    }

    public void process(WatchedEvent event) {
        String path = event.getPath();
        if ( event.getType() == Event.EventType.None ) {
            // 节点没有发生改变，无节点创建、无接点删除、节点数据未改变、子节点未改变
            // 那么说明可能是会话状态发生了改变
            switch (event.getState()) {
                case SyncConnected:
                    //
                    System.err.println(" 此客户端处于连接状态，不需要做任何事");
                    break;
                case Expired:
                    // 会话失效，结束
                    this.close();
                    break;
            }
        } else {
            // 状态改变了，检查是否znode节点值改变。如果改变则取出
            System.err.println(112233);
            if ( path != null && path.equals(znode) ) {
                zk.exists(znode, true, this, null);
            }
        }
    }

    public void close() {
        synchronized (this) {
            this.notifyAll();
        }
    }

    public static void main(String[] args) {
        ZookeeperExample temp = new ZookeeperExample();
        new Thread(new ZookeeperExample()).start();
        try {
            temp.zk.setData(znode, "第二次放入数据".getBytes(), temp.stat.getVersion());
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch ( KeeperException e ) {
            e.printStackTrace();
        }
    }
}
