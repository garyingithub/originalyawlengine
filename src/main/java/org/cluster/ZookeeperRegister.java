package org.cluster;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yawlfoundation.yawl.engine.time.YTimer;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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


    @Value("${zk.address}")
    private String ZK_ADDRESS;

    @Value("${engine.address}")
    private String ENGINE_ADDRESS;
    private ZooKeeper zk;
    public static String engineId;

    @PostConstruct
    public void init() {


        if(this.ZK_ADDRESS==null){
            this.ZK_ADDRESS="stack:2184";

        }

        if(this.ENGINE_ADDRESS==null){
            this.ENGINE_ADDRESS="192.168.239.1:8089";
        }
        logger.info(String.format("ENGINE_ADDRESS="+this.ENGINE_ADDRESS));


    }

    public void run(){
        try {

            zk=new ZooKeeper(this.ZK_ADDRESS, 30000, new StubWatcher());
            if(zk.exists("/engine",new StubWatcher())==null){
                String temp=zk.create("/engine","".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT );
                logger.info(temp);
            }
            String result=zk.create("/engine/",this.ENGINE_ADDRESS.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, EPHEMERAL_SEQUENTIAL);
            logger.info(result);
            engineId=result.substring(8);
        } catch (KeeperException | InterruptedException | IOException e) {
            logger.info(e.getMessage());
//            throw new RuntimeException(e);
        }

        while (true){
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    public static String getEngineId(){
        int count=0;
        do{
            String engineId= ZookeeperRegister.engineId;
        }while (engineId==null&&count++<50);
        if(engineId==null){
            engineId="0";
//            throw new RuntimeException("Can't initialize engineId");
        }
        return engineId;
    }
}
