package org.damein.miaosha.redis;

public interface keyPrefix {
    public int expireSeconds();
    public String getPrefix();
}
