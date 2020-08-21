package com.lxm.netty.client.handler;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lxm.netty.entity.ResponseEntity;

import io.netty.channel.ChannelPromise;

public final class ClientPromiseHolder {

    private static final Map<Integer, SimpleEntry<ChannelPromise, ResponseEntity>> PROMISE_MAP = new ConcurrentHashMap<>();

    public static void putPromise(int xid, ChannelPromise promise) {
        PROMISE_MAP.put(xid, new SimpleEntry<ChannelPromise, ResponseEntity>(promise, null));
    }

    public static SimpleEntry<ChannelPromise, ResponseEntity> getEntry(int xid) {
        return PROMISE_MAP.get(xid);
    }

    public static void remove(int xid) {
        PROMISE_MAP.remove(xid);
    }

    public static <T> boolean completePromise(int xid, ResponseEntity<T> response) {
        if (!PROMISE_MAP.containsKey(xid)) {
            return false;
        }
        SimpleEntry<ChannelPromise, ResponseEntity> entry = PROMISE_MAP.get(xid);
        if (entry != null) {
            ChannelPromise promise = entry.getKey();
            if (promise.isDone() || promise.isCancelled()) {
                return false;
            }
            entry.setValue(response);
            promise.setSuccess();
            return true;
        }
        return false;
    }

    private ClientPromiseHolder() {}
}
