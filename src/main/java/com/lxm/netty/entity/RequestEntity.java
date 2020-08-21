package com.lxm.netty.entity;

/**
 * 
 * ClassName: com.lxm.netty.entity.RequestEntity <br/>
 * Function: 请求模型 <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public class RequestEntity<T> {

    private int id;
    
    private int type;

    private T   data;
    
    public RequestEntity(int type, T data) {
        super();
        this.type = type;
        this.data = data;
    }

    public RequestEntity(int id, int type, T data) {
        super();
        this.id = id;
        this.type = type;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
