package edu.sysu;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.zookeeper.CreateMode.EPHEMERAL_SEQUENTIAL;

/**
 * Created by gary on 22/04/2017.
 */
@Component
public class ZookeeperRegister {

    class StubWatcher implements Watcher{

        @Override
        public void process(WatchedEvent watchedEvent) {

        }
    }

    private final Logger logger= LoggerFactory.getLogger(ZookeeperRegister.class);
    private String ZK_ADDRESS;
    private String ENGINE_ADDRESS;
    private ZooKeeper zk;
    public static String engineId;

    public ZookeeperRegister() {

        this.ZK_ADDRESS=System.getenv("ZK_ADDRESS");
        if(this.ZK_ADDRESS==null){
            this.ZK_ADDRESS="192.168.239.128:2181";

        }
        logger.info(String.format("ZK_ADDRESS="+this.ZK_ADDRESS));



        this.ENGINE_ADDRESS=System.getenv("ENGINE_ADDRESS");
        if(this.ENGINE_ADDRESS==null){
            this.ENGINE_ADDRESS="192.168.239.1:8089";
        }
        logger.info(String.format("ENGINE_ADDRESS="+this.ENGINE_ADDRESS));


    }

    public void run(){
        try {

            zk=new ZooKeeper(this.ZK_ADDRESS, 30000, new StubWatcher());
            if(zk.exists("/engine",new StubWatcher())==null){
                logger.info(zk.create("/engine","".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT ));
            }
            String result=zk.create("/engine/",this.ENGINE_ADDRESS.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, EPHEMERAL_SEQUENTIAL);
            logger.info(result);
            engineId=result.substring(8);
        } catch (KeeperException | InterruptedException | IOException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
