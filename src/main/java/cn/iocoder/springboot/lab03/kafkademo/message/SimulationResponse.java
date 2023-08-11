package cn.iocoder.springboot.lab03.kafkademo.message;

import cn.hutool.json.JSONObject;

public class SimulationResponse {
    public static final String TOPIC = "simulation_response";
    private JSONObject jsonObject;

    public void setJsonMessage(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public  JSONObject getJsonMessage() {
        return jsonObject;
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }
}
