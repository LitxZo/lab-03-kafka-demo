package cn.iocoder.springboot.lab03.kafkademo.websocket;


import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.springboot.lab03.kafkademo.message.SimulationMessage;
import cn.iocoder.springboot.lab03.kafkademo.message.SimulationResponse;
import cn.iocoder.springboot.lab03.kafkademo.producer.ResponseProducer;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MultiValueMap;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

@ServerEndpoint(path = "/ws")
public class MyWebSocket {

    @Autowired
    private ResponseProducer responseProducer;

    @Value("${spring.server.ip}")
    private String serverIp;
    @BeforeHandshake
    public void handshake(Session session, HttpHeaders headers, @RequestParam String req, @RequestParam MultiValueMap reqMap, @PathVariable String arg, @PathVariable Map pathMap){
        session.setSubprotocols("stomp");
//        if (!"ok".equals(req)){
//            System.out.println("Authentication failed!");
//            session.close();
//        }
    }
    
    @OnOpen
    public void onOpen(Session session, HttpHeaders headers, @RequestParam String req, @RequestParam MultiValueMap reqMap, @PathVariable String arg, @PathVariable Map pathMap){
        System.out.println("new connection");
        System.out.println(req);

        /*TODO：
            向UE发送redis里的数据
         */

    }

    @OnClose
    public void onClose(Session session) throws IOException {
       System.out.println("one connection closed"); 
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(Session session, String message) throws ExecutionException, InterruptedException {
        System.out.println(message);
        session.sendText("Hello Netty!");
        /*TODO：
            if msg == OK
                接收到taskId和端口号并返回给kafka
            if msg == Done
                向kafka返回数据
         */
        JSONObject jsonObject = JSONUtil.parseObj(message);
        JSONObject responseObject = JSONUtil.createObj();
        String status = jsonObject.getStr("status");
        if (Objects.equals(status, "OK")){

            responseObject.put("task_id", jsonObject.getInt("task_id"));
            responseObject.put("url",serverIp + ":" + jsonObject.getStr("port"));
            responseProducer.syncSend(responseObject);
        }
        else if (Objects.equals(status, "Done")) {
            responseObject.put("task_id", jsonObject.getInt("task_id"));
            responseObject.put("data", jsonObject.getStr("data"));
            responseProducer.syncSend(responseObject);
        }



    }

    @OnBinary
    public void onBinary(Session session, byte[] bytes) {
        for (byte b : bytes) {
            System.out.println(b);
        }
        session.sendBinary(bytes); 
    }

    @OnEvent
    public void onEvent(Session session, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    System.out.println("read idle");
                    break;
                case WRITER_IDLE:
                    System.out.println("write idle");
                    break;
                case ALL_IDLE:
                    System.out.println("all idle");
                    break;
                default:
                    break;
            }
        }
    }

}