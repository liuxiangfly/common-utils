package com.lxm.netty.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lxm.netty.client.config.ClientConfig;
import com.lxm.netty.codec.coder.data.StringEntityCoder;
import com.lxm.netty.codec.registry.EntityDecoderRegistry;
import com.lxm.netty.codec.registry.EntityEncoderRegistry;
import com.lxm.netty.entity.RequestEntity;
import com.lxm.netty.entity.ResponseEntity;

/**
 * ClassName: com.lxm.netty.client.TestClientMain <br/>
 * Function: FUNCTION <br/>
 * Date: 2020年8月21日 <br/>
 * @author liuxiangming
 */
public class TestClientMain {

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        Gson gson = new GsonBuilder().create();
        // 注册类型编码解码器
        EntityDecoderRegistry.addDecoder(1, new StringEntityCoder());
        EntityEncoderRegistry.addEncoder(1, new StringEntityCoder());
        ClientConfig.setRequestTimeout(10000);
        NettyClient client = new NettyClient("127.0.0.1", 8088);
        client.start();
        while (!client.isReady()) {
            Thread.sleep(500L);
        }
        Thread[] threads = new Thread[5];
        for(int i = 0; i < threads.length; i++){
            threads[i] = new Thread(new Runnable() {
                
                @Override
                public void run() {
                    RequestEntity request = new RequestEntity<String>(1, "test netty request data 111222.");
                    try {
                        ResponseEntity<?> response = client.sendRequest(request);
                        System.out.println(gson.toJson(response));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                }
            });
            threads[i].start();
        }
        
        Thread.sleep(60000L);
        client.stop();
    }

}
