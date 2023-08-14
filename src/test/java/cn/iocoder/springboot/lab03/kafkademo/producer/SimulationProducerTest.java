package cn.iocoder.springboot.lab03.kafkademo.producer;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.springboot.lab03.kafkademo.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SimulationProducerTest {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private SimulationProducer producer;

    @Test
    public void testSimulationSyncSend() throws ExecutionException, InterruptedException {

//
//      String jsonMessage = "{\"equivalent\":1,\"eventLon\":1,\"sustainTime\":11,\"dataType\":2,\"ip\":\"127.0.0.1\",\"bombType\":1,\"eventType\":2,\"diffusionRange\":[\"10\",\"50\"],\"port\":9092,\"createTime\":1691573546507,\"geography\":\"0\",\"eventLat\":1,\"eventTime\":\"2023-08-10 12:00:00\",\"topic\":\"effect-data-out-topic\",\"id\":\"1689208002922356737\",\"diffusionTime\":[\"1\",\"2\"]}";
        for(int i = 0; i < 10; i++){
            JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("test_id", i);

            System.out.println(jsonObject);
            SendResult result = producer.syncSend(jsonObject);
            logger.info("[testSyncSend][发送json内容：[{}] 发送结果：[{}]]", jsonObject, result);
        }

        // 阻塞等待，保证消费
        new CountDownLatch(1).await();
    }

}
