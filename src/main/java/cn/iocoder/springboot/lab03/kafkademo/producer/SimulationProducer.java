package cn.iocoder.springboot.lab03.kafkademo.producer;

import cn.hutool.json.JSONObject;
import cn.iocoder.springboot.lab03.kafkademo.message.*;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@Component
public class SimulationProducer {

    @Resource
    private KafkaTemplate<Object, Object> kafkaTemplate;

    public SendResult syncSend(JSONObject jsonMessage) throws ExecutionException, InterruptedException {

        SimulationResponse simulationResponse = new SimulationResponse();

        //ProducerRecord<Object, String> producerRecord = new ProducerRecord<>("DEMO_05", jsonMessage);
        // 同步发送消息
        return kafkaTemplate.send(simulationResponse.TOPIC, jsonMessage).get();
    }

    public ListenableFuture<SendResult<Object, Object>> asyncSend(String message) {


        // 异步发送消息
        return kafkaTemplate.send(Demo01Message.TOPIC, message);
    }

}
