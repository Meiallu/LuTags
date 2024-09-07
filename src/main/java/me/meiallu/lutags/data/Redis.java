package me.meiallu.lutags.data;

import me.meiallu.lutags.LuTags;
import me.meiallu.lutags.util.Util;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPooled;

public class Redis implements Storage {

    private final JedisPooled jedis;

    @Override
    public void writeTag(String uuid, String tag) {
        jedis.hset("LuTags:" + uuid, "tag", tag == null ? Tag.getPlayerTag(uuid).name : tag);
    }

    @Override
    public void writeMedal(String uuid, String medal) {
        jedis.hset("LuTags:" + uuid, "medal", medal == null ? Medal.getPlayerMedal(uuid).name : medal);
    }

    @Override
    public String readTag(String uuid) {
        return jedis.hget("LuTags:" + uuid, "tag");
    }

    @Override
    public String readMedal(String uuid) {
        return jedis.hget("LuTags:" + uuid, "medal");
    }

    public Redis() {
        Util.log("Connecting to Redis database...");

        String adress = LuTags.sql.database.get("redis").get("adress");
        int port = Integer.getInteger(LuTags.sql.database.get("redis").get("port"));
        String password = LuTags.sql.database.get("redis").get("password");

        DefaultJedisClientConfig config = DefaultJedisClientConfig.builder()
                .timeoutMillis(60000)
                .password(password)
                .build();

        jedis = new JedisPooled(new HostAndPort(adress, port), config);
        Util.log("Successfully connected and setup!");
    }
}

