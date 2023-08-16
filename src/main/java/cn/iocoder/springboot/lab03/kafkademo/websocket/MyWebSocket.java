package cn.iocoder.springboot.lab03.kafkademo.websocket;


import cn.hutool.json.JSONObject;
import cn.iocoder.springboot.lab03.kafkademo.producer.ResponseProducer;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@ServerEndpoint(path = "/ws")
public class MyWebSocket {

    public static Queue<JSONObject> KafkaData = new LinkedList<>();
    @Autowired
    private ResponseProducer responseProducer;
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
        try {
            Thread.sleep(200000);
            JSONObject a = KafkaData.poll();
            System.out.println(a.toString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
    public void onMessage(Session session, String message) {
        System.out.println(message);
        session.sendText("Hello Netty!");


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