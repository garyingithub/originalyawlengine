package org.springframework.boot.actuate.metrics;

import org.apache.kafka.common.PartitionInfo;
import org.cluster.ZookeeperRegister;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.writer.GaugeWriter;
import org.springframework.stereotype.Component;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by gary on 19/04/2017.
 */

public class KafkaGaugeWriter implements GaugeWriter {


    String servers;

    private Producer<String,String> producer;
    private String engineId;
    public final String TOPIC="ENGINE-METRIC";

    private Logger logger= LoggerFactory.getLogger(KafkaGaugeWriter.class);

    public KafkaGaugeWriter(String servers){
        this.servers=servers;
        Map props=new Properties();
        if(servers==null){
            servers="222.200.180.59:9092";
        }
        props.put("bootstrap.servers", servers);
        props.put("acks", "0");
        props.put("retries", 0);
        props.put("batch.size", 16384);

        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);

        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
       // props.put("log.message.timestamp.type","LogAppendTime");

        Producer<String, String> producer = new KafkaProducer<String, String>(props);


        for (PartitionInfo info : producer.partitionsFor(TOPIC)) {

            logger.info(info.leader().toString());

        }

        this.producer=producer;

        int count=0;
        engineId=ZookeeperRegister.getEngineId();



    }



    private ExecutorService service= Executors.newFixedThreadPool(1);

    @Override
    public void set(Metric<?> metric) {

        final String key=metric.getName()+":"+engineId;
        final String value=metric.getValue().toString();
        final long cur=System.currentTimeMillis();

       // logger.info(key+" "+value);
        service.submit(new Runnable() {
            @Override
            public void run() {


                producer.send(new ProducerRecord<>(TOPIC,null,cur,key,value));
                logger.info("has sent "+key+" "+value);

            }
        });

    }

}
