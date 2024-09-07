package me.meiallu.lutags.data;

public interface Storage {

    void writeTag(String uuid, String tag);

    void writeMedal(String uuid, String medal);

    String readTag(String uuid);

    String readMedal(String uuid);
}
