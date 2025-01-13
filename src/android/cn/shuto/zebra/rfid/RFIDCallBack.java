package cn.shuto.zebra.rfid;


import com.zebra.rfid.api3.TagData;

interface RFIDCallBack {
    void handleTagData(TagData[] tagData);

    void handleTriggerPress(boolean pressed);
}
