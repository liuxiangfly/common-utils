package com.lxm.rest.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

public class AsyncRestTemplateUtilTest {

    /**
     * @param args
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newFixedThreadPool(3);
        AsyncRestTemplateUtil templateUtil = new AsyncRestTemplateUtil();
        templateUtil.setConnectTimeout(5000);
        templateUtil.setReadTimeout(5000);
        templateUtil.init();
        String url = "http://ec-wolf.test.za.biz//redis/set?key=tst_123431&value=23423fdfdf";
        List<ListenableFuture<ResponseEntity<String>>> list = new ArrayList<>();
        for(int i = 0; i < 100; i++){
            ListenableFuture<ResponseEntity<String>> future = templateUtil.exchange(url, "post", null, null, String.class, res -> {
                // AsyncRestTemplateUtil请求使用netty框架执行，netty客户端EventLoopGroup默认线程数为processor*2，这里不可阻塞，否则会阻塞netty请求整体处理执行
                es.execute(() -> {
                    System.out.println("result:" + res.getBody());
                    try {
                        Thread.sleep(5000L);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                });
            }, ex -> {
                System.out.println(ex);
            });
            list.add(future);
        }
        
        for(ListenableFuture<ResponseEntity<String>> future: list){
            System.out.println(future.get().getBody());
        }
        
    }

}
