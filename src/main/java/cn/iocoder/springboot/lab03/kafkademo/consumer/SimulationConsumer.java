package cn.iocoder.springboot.lab03.kafkademo.consumer;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.springboot.lab03.kafkademo.message.Demo04Message;
import cn.iocoder.springboot.lab03.kafkademo.message.Demo05Message;
import cn.iocoder.springboot.lab03.kafkademo.message.SimulationMessage;
import cn.iocoder.springboot.lab03.kafkademo.message.SimulationResponse;
import cn.iocoder.springboot.lab03.kafkademo.producer.ResponseProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SimulationConsumer {

    private AtomicInteger count = new AtomicInteger(0);

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ResponseProducer responseProducer = new ResponseProducer();

    @KafkaListener(topics = SimulationMessage.TOPIC,
            groupId = "request-consumer-group-" + SimulationMessage.TOPIC)
    public void onMessage(String record) throws ExecutionException, InterruptedException {
        logger.info("收到simulation request");
        logger.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), record);
        JSONObject jsonObject = JSONUtil.parseObj(record);
        System.out.println(jsonObject);

        int test_id = (int) jsonObject.get("test_id");
        System.out.println(test_id );
        JSONObject responseJson = JSONUtil.createObj();
        responseJson.put("test_id", test_id);
        responseJson.put("msg", "处理成功");
        System.out.println(responseJson);
        SendResult result = responseProducer.syncSend(responseJson);
        System.out.println("向服务端返回数据" + result);

        // 注意，此处抛出一个 RuntimeException 异常，模拟消费失败
//        throw new RuntimeException("我就是故意抛出一个异常");
    }

}
