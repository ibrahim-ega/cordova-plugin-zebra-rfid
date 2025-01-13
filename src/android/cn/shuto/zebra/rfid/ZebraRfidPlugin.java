package cn.shuto.zebra.rfid;

import com.zebra.rfid.api3.TagData;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class ZebraRfidPlugin extends CordovaPlugin {

  private RFIDHandler rfidHandler;
  private CallbackContext mCallbackContext;

  // --
  private static final String CHECK_CONNECT = "check_connect";
  // --
  private static final String CONNECT = "connect";
  // --
  private static final String DISCONNECT = "disconnect";

  private static final String CHANGEMODE = "change_mode";

  private static final String CHANGEPOWER = "change_power";

  private static final String WRITE_TAG = "write_tag";

  private static final String LOCK_TAG = "lock_tag";

  private static final String START_READ = "start_read";
  
  private static final String STOP_READ = "stop_read";

  // --
  private Set<String> tagIdSet = new HashSet<>();
  // --
  private boolean isSend = false;
  // --
  private boolean isHandle = false;

  private int g_max_power = 270;

  private String mode = "RFID";

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    //--
    rfidHandler = new RFIDHandler();
    rfidHandler.init(cordova.getContext().getApplicationContext());
    rfidHandler.setOnChangeListener(callBack);
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    this.mCallbackContext = callbackContext;

    switch (action) {
      case CHECK_CONNECT:
        JSONObject obj = new JSONObject();
        try {
          boolean isConnect = rfidHandler.isReaderConnected();
          if (isConnect) {
            obj.put("code", 1);
            obj.put("msg", "Connected");
          } else {
            obj.put("code", 0);
            obj.put("msg", "Disconnected");
          }
          PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
          result.setKeepCallback(true);
          mCallbackContext.sendPluginResult(result);
        } catch (Error e) {
          obj.put("msg", e.getMessage());
          mCallbackContext.error(obj);
        }
        break;
      case CONNECT:
        JSONObject obj1 = new JSONObject();
        String connect_mode = args.optString(0);
        mode = connect_mode;
        int db_level = args.optInt(1);
        g_max_power = db_level;
        try {
          rfidHandler.setMaxPower(db_level);
          String connect = rfidHandler.connect(connect_mode);
          if ("Connected".equals(connect)) {
            obj1.put("code", 1);
            obj1.put("msg", "Connected to [" + connect_mode + "]");
          } else {
            obj1.put("code", 0);
            obj1.put("msg", "Diconnected");
          }
          PluginResult result = new PluginResult(PluginResult.Status.OK, obj1);
          result.setKeepCallback(true);
          mCallbackContext.sendPluginResult(result);
        } catch (Error e) {
          obj1.put("msg", e.getMessage());
          mCallbackContext.error(obj1);
        }
        break;
      case DISCONNECT:
        JSONObject obj2 = new JSONObject();
        try {
          rfidHandler.disconnect();
          obj2.put("code", 1);
          obj2.put("msg", "Disconnected");
          PluginResult result = new PluginResult(PluginResult.Status.OK, obj2);
          result.setKeepCallback(true);
          mCallbackContext.sendPluginResult(result);
        } catch (Error e) {
          obj2.put("msg", e.getMessage());
          mCallbackContext.error(obj2);
        }
        break;
      /*case CHANGEMODE:
        JSONObject obj3 = new JSONObject();
        String mode = args.optString(0);
        try {
          rfidHandler.setTriggerMode(mode);
          obj3.put("code", 1);
          obj3.put("msg", "Mode Changed");
          PluginResult result = new PluginResult(PluginResult.Status.OK, obj3);
          result.setKeepCallback(true);
          mCallbackContext.sendPluginResult(result);
        } catch (Error e) {
          obj3.put("msg", e.getMessage());
          mCallbackContext.error(obj3);
        }
        break;*/      
      case CHANGEPOWER:
        JSONObject obj4 = new JSONObject();
        int dblevel = args.optInt(0);
        try {
          rfidHandler.setAntennaPower(dblevel);
          obj4.put("code", 1);
          obj4.put("msg", "Antenna Power Changed");
          PluginResult result = new PluginResult(PluginResult.Status.OK, obj4);
          result.setKeepCallback(true);
          mCallbackContext.sendPluginResult(result);
        } catch (Error e) {
          obj4.put("msg", e.getMessage());
          mCallbackContext.error(obj4);
        }
        break;
      case WRITE_TAG:
        JSONObject obj5 = new JSONObject();
        String sourceEPC = args.optString(0);
        String rfid_password = args.optString(1);
        String targetEPC = args.optString(2);
        String type = args.optString(3);
        try {
          rfidHandler.writeTag(sourceEPC, rfid_password, targetEPC, 2, type);
          obj5.put("code", 1);
          obj5.put("msg", "Tag Sucessfully Writed");
          obj5.put("type", type);
          PluginResult result = new PluginResult(PluginResult.Status.OK, obj5);
          result.setKeepCallback(true);
          mCallbackContext.sendPluginResult(result);
        } catch (Error e) {
          obj5.put("msg", e.getMessage());
          mCallbackContext.error(obj5);
        }
        break;
      case LOCK_TAG:
        JSONObject obj6 = new JSONObject();
        String sourceEPC2 = args.optString(0);
        try {
          rfidHandler.lockTag(sourceEPC2);
          obj6.put("code", 1);
          obj6.put("msg", "Tag Sucessfully Locked");
          PluginResult result = new PluginResult(PluginResult.Status.OK, obj6);
          result.setKeepCallback(true);
          mCallbackContext.sendPluginResult(result);
        } catch (Error e) {
          obj6.put("msg", e.getMessage());
          mCallbackContext.error(obj6);
        }
        break;
      // case START_READ:
      //   startInventory(mCallbackContext);
      //   break;
      // case STOP_READ:
      //   stopInventory(mCallbackContext);
      //   break;
    }
    return true;
  }

  RFIDCallBack callBack = new RFIDCallBack() {
    @Override
    public void handleTagData(TagData[] tagData) {
      // --
      // --
      if (isHandle) {
        return;
      }
      isHandle = true;
      for (TagData tagDatum : tagData) {
        if (tagIdSet.contains(tagDatum.getTagID())) {
          isSend = false;
          break;
        } else {
          tagIdSet.add(tagDatum.getTagID());
          isSend = true;
        }
      }
      if (isSend && !tagIdSet.isEmpty()) {
        // --
        JSONObject obj = new JSONObject();
        try {
          obj.put("code", "1");
          obj.put("data", tagIdSet);
          PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
          result.setKeepCallback(true);
          mCallbackContext.sendPluginResult(result);
        } catch (JSONException e) {
          mCallbackContext.error(e.getMessage());
        }
      }
      isHandle = false;
    }

    @Override
    public void handleTriggerPress(boolean pressed) {
      if (pressed) {
        tagIdSet.clear();
        rfidHandler.performInventory();
      } else {
        rfidHandler.stopInventory();
      }
    }
  };

  // private void startInventory(CallbackContext callbackContext) {
  //   tagIdSet.clear();
  //   rfidHandler.performInventory();
  //   JSONObject response = new JSONObject();
  //   try {
  //       response.put("code", 1);
  //       response.put("msg", "Reading started");
  //       PluginResult result = new PluginResult(PluginResult.Status.OK, response);
  //       result.setKeepCallback(true);
  //       callbackContext.sendPluginResult(result);
  //   } catch (JSONException e) {
  //       callbackContext.error("Error starting inventory: " + e.getMessage());
  //   }
  // }

  // private void stopInventory(CallbackContext callbackContext) {
  //   rfidHandler.stopInventory();
  //   JSONObject response = new JSONObject();
  //   try {
  //       response.put("code", 1);
  //       response.put("msg", "Reading stopped");
  //       PluginResult result = new PluginResult(PluginResult.Status.OK, response);
  //       result.setKeepCallback(true);
  //       callbackContext.sendPluginResult(result);
  //   } catch (JSONException e) {
  //       callbackContext.error("Error stopping inventory: " + e.getMessage());
  //   }
  // }

  @Override
  public void onDestroy() {
    rfidHandler.dispose();
  }

  @Override
  public void onPause(boolean multitasking) {
    rfidHandler.disconnect();
  }

  public void onResume(boolean multitasking) {
    rfidHandler.connect(mode);
  }

}
