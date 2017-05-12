package org.springframework.boot.actuate.metrics;

import edu.sysu.ZookeeperRegister;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.writer.GaugeWriter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

/**
 * Created by gary on 19/04/2017.
 */
@Component
public class KafkaGaugeWriter implements GaugeWriter {

    @Value("${bootstrap.servers}")
    String servers;

    private Producer<String,String> producer;
    private String engineId;
    public final String TOPIC;

    private Logger logger= LoggerFactory.getLogger(KafkaGaugeWriter.class);

    public KafkaGaugeWriter(){
        Map props=new Properties();
        props.put("bootstrap.servers", servers);
        props.put("acks", "0");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<String, String>(props);


        this.producer=producer;

        int count=0;
        do{
            engineId= ZookeeperRegister.engineId;
        }while (engineId==null&&count++<50);
        if(engineId==null){
            throw new RuntimeException("Can't initialize engineId");
        }
        this.TOPIC="ENGINE-METRIC";
        //logger.info(String.format("Topic is "+TOPIC));
    }




    @Override
    public void set(Metric<?> metric) {

        String key=metric.getName()+":"+engineId;
        String value=metric.getValue().toString();

        //producer.send(new ProducerRecord<>(TOPIC,key,value));
        logger.info(key+" "+value);
    }

}
