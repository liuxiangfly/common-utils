package com.lxm.netty.entity;

/**
 * 
 * ClassName: com.lxm.netty.entity.ResponseEntity <br/>
 * Function: 响应模型 <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public class ResponseEntity<T> {
    
    private int id;
    
    private int type;
    
    private int status;

    private T data;
    
    public ResponseEntity(int id, int type, int status, T data) {
        super();
        this.id = id;
        this.type = type;
        this.status = status;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    
}
