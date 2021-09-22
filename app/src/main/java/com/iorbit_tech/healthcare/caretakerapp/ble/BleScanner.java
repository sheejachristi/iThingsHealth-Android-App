package com.iorbit_tech.healthcare.caretakerapp.ble;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.Toast;

import com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare.WifiActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BleScanner
{
    BluetoothManager btManager;
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner leScanner;

    Activity parent;
    BleThread bleThread;
    /*
     #define ADVTREPORT_OFFSET_HEAD sizeof( LM_EV_ADVERTISING_REPORT_T)
     #define ADVTREPORT_OFFSET_SIGN (0+ADVTREPORT_OFFSET_HEAD)
     #define ADVTREPORT_OFFSET_DESTADDR (2+ADVTREPORT_OFFSET_HEAD)
     #define ADVTREPORT_OFFSET_ENC (2+6+ADVTREPORT_OFFSET_HEAD)
     #define ADVTREPORT_OFFSET_DEVTYPE (2+6+1+ADVTREPORT_OFFSET_HEAD)
     #define ADVTREPORT_OFFSET_CMD    (2+6+1+2+ADVTREPORT_OFFSET_HEAD)
     #define ADVTREPORT_OFFSET_SUBCMD (2+6+1+2+1+ADVTREPORT_OFFSET_HEAD)
     #define ADVTREPORT_OFFSET_SEQNUM (2+6+1+2+1+1+ADVTREPORT_OFFSET_HEAD)
     #define ADVTREPORT_OFFSET_MAXHOPS (2+6+1+2+1+1+1+ADVTREPORT_OFFSET_HEAD)
     #define ADVTREPORT_OFFSET_CUSTDAT (2+6+1+2+1+1+1+1+ADVTREPORT_OFFSET_HEAD)
   */
    static int LE_ADVTDATA_OFFSET = 7;
    static int LE_ADVT_OFFSET_SIG = 		LE_ADVTDATA_OFFSET+0;
    static int LE_ADVT_OFFSET_DESTADD = 	LE_ADVTDATA_OFFSET+2;
    static int LE_ADVT_OFFSET_ENCTYPE= 	LE_ADVTDATA_OFFSET+2+6;
    static int LE_ADVT_OFFSET_DEVTYPE= 	LE_ADVTDATA_OFFSET+2+6+1;
    static int LE_ADVT_OFFSET_CMD = 		LE_ADVTDATA_OFFSET+2+6+1+2;
    static int LE_ADVT_OFFSET_SUBCMD = 	LE_ADVTDATA_OFFSET+2+6+1+2+1;
    static int LE_ADVT_OFFSET_SEQNO =  	LE_ADVTDATA_OFFSET+2+6+1+2+1+1;
    static int LE_ADVT_OFFSET_MAXHOPS =	LE_ADVTDATA_OFFSET+2+6+1+2+1+1+1;
    static int LE_ADVT_OFFSET_CUSTDATA =	LE_ADVTDATA_OFFSET+2+6+1+2+1+1+1+1;
    /************COMMAND Specific**************************************/
    /************ENROL* Sensor Trigger*************************************************/
    static int LE_ADVT_OFFSET_ENROLLSTS = 18;
    static int LE_ADVT_OFFSET_SENSORNAME_LEN = 19;
    static int LE_ADVT_OFFSET_SENSORNAME = 20;
    /************BATTLOW*************************************************/
    static int LE_ADVT_OFFSET_BATLEV = 18;

    final static byte LE_ADVT_CMD_SENSOR = 10;
    final static byte LE_ADVT_CMD_DEVICE = 11;
    final static byte LE_ADVT_CMD_LIGHT  = 12;
    final static byte LE_ADVT_CMD_FAN    = 13;

    final static byte LE_ADVT_SUBCMD_SENSOR_ENROLL = 0;
    final static byte LE_ADVT_SUBCMD_SENSOR_TRIG = 1;
    final static byte LE_ADVT_SUBCMD_SENSOR_LOWBAT = 2;
    final static byte LE_ADVT_SUBCMD_SENSOR_HB = 3;
    final static byte LE_ADVT_SUBCMD_SENSOR_CURVAL = 4; //similar to sensor trigger

    final static byte LE_ADVT_SUBCMD_SENSOR_ENROLL_REQ_ACCEPT =10;
    final static byte LE_ADVT_SUBCMD_SENSOR_ENROLL_REQ_REJECT =11;
    final static byte LE_ADVT_SUBCMD_SENSOR_ENROLL_REQ_COMPLETE =12;

    final static byte IRON_CMD_ON = 10;
    final static byte  IRON_CMD_MOTION = 11;
    final static byte  IRON_CMD_IDLE_VERT = 12;
    final static byte  IRON_CMD_IDLE_HORIZ =  13;
    final static byte  IRON_CMD_OFF = 14;

    final static byte PLUGTOP_CMD_POWERON = 10;
    final static byte PLUGTOP_CMD_ONOFF = 20;
    final static byte PLUGTOP_CMD_ONOFF_REPLY = 21;
    final static byte PLUGTOP_CMD_STATUS_QUERY = 22;
    final static byte PLUGTOP_CMD_STATUS_QUERY_REPLY = 23;

    /*
    final static byte SENSOR_ENROLL_REQ = 0;
    final static byte SENSOR_ENROLL_ACCEPT = 1;
    final static byte SENSOR_ENROLL_COMPLET = 2;
    final static byte SENSOR_ENROLL_REJECT = 3;
    final static byte SENSOR_TRIGGER = 4;
    final static byte SENSOR_HB = 5;
    final static byte SENSOR_BATT_LOW = 6;
    */
    static int LE_APP_STATE_NORMAL= 0;
    static int LE_APP_STATE_SENSOR_ENROLL= 1;

    int appState;

    public class EventDetails
    {
        public boolean advtReply=false;
        public int devType;
        public byte seqNum;
        public Object eventData;
        public String btAddress;
    }

    public  class Sensor
    {
        String sensorName;
        int sensorID;
    }
    public enum SensorEventType
    {
        EnrollReq,
        SensorTrigger,
        SensorHB,
        SensorBatLow,
        SensorInfoAlert

    }

    public enum RelayEvents
    {
        RelayPowerOn,
        ChannelOn,
        ChannelOff,
        QueryRes,
        EnrollSuccess,
        EnrollFailed,
        ClearSuccess,
        ClearFailed
    }

    public enum RelayModuleType
    {
        Plugtop,
        FourChannel,
        EightChannel
    }

    public class RelayModule
    {
        RelayModuleType devType;
        RelayEvents eventType;
        int numChannels;
        String sensorName;
        byte [] switchStatus = new byte [3];//status of 24 switches in one array
        int channelNum; //Indivigual channel number
        int channelStatus; //Indivigual channel status
        int commandType;
    }
    public enum SensorDevType
    {
        DoorSensor,
        DS_With_Vibration_Sensor,
        Vibration_Sensor,
        PIR,
        LPG_Leak_Sensor,
        Beam_Sensor,
        Temparature_Sensor,
        Accelerometer,
        Gyro,
        Accel_Gyro,
        Current_Sensor,
        Voltage,
        Humidity,
        Rain_Sensor,
        Touch_Switch,
        Switch
    }

    public class SensorEventDetails
    {
        SensorEventType eventType;
        SensorDevType sensorDevType;
        String sensorName;
        String sensorAddress;
        byte [] custData;
        boolean open;
        int level;
        int seqNum;
        int watts;
        int wattsToday;
        int wattsMonth;
    }

    enum IronEventType
    {
        On,
        Off,
        IdleVertical,
        IdleHorizontal,
        SwitchingOff,
        MotionDetected,
        FallDetected
    }

    enum IronType
    {
        DryIron,
        DryIronTempControlled,
        SteamIron,
        SteamIronTempControlled
    }
    public class IronBoxEventDetails
    {
        IronEventType eventType;
        IronType ironType;
        String ironName;
        int idleTime;
    }
    ArrayList<SensorEventNotify> callbacks;
    Sensor sensor;
    public boolean  InitScanner(Activity activity)
    {
        this.parent = activity;
        callbacks = new ArrayList<SensorEventNotify>();

        btManager = (BluetoothManager) parent.getSystemService(Context.BLUETOOTH_SERVICE);
        if(btManager==null) return false;

        bluetoothAdapter = btManager.getAdapter();
        if(bluetoothAdapter==null) return false;


        leScanner = bluetoothAdapter.getBluetoothLeScanner();
        if(leScanner!=null) { Toast.makeText(parent, "Low energy scanner enabled", Toast.LENGTH_LONG).show();}
        else  { Toast.makeText(parent, "Low energy sacnner FAILED", Toast.LENGTH_LONG).show();}

        if(leScanner==null) return false;

        bleThread = new BleThread();
        bleThread.start();

        return true;
    }

    public boolean  InitMokoScanner(Activity activity)
    {
        this.parent = activity;
        callbacks = new ArrayList<SensorEventNotify>();

        btManager = (BluetoothManager) parent.getSystemService(Context.BLUETOOTH_SERVICE);
        if(btManager==null) return false;

        bluetoothAdapter = btManager.getAdapter();
        if(bluetoothAdapter==null) return false;


        leScanner = bluetoothAdapter.getBluetoothLeScanner();
        if(leScanner!=null) { Toast.makeText(parent, "Low energy scanner enabled", Toast.LENGTH_LONG).show();}
        else  { Toast.makeText(parent, "Low energy scanner FAILED", Toast.LENGTH_LONG).show();}

        if(leScanner==null) return false;

        return true;
    }
    //
    //Can cause exception if used with out initializing
    public void RegisterCallback(SensorEventNotify notify)
    {
        callbacks.add(notify);
    }

    public void Unregister(SensorEventNotify notify)
    {
        callbacks.remove(notify);
    }
    public void StartScan()
    {

        ScanSettings scanSetting = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        List<ScanFilter> filters = new ArrayList<ScanFilter>();

        leScanner.startScan(filters, scanSetting, mBleScanCallback);

    }
    public void StartMokoScan()
    {

        ScanSettings scanSetting = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        List<ScanFilter> filters = new ArrayList<ScanFilter>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            System.out.println("enable bluetooth");
        }
        else {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
            leScanner.startScan(filters, scanSetting, mokoScanCallback);
        }



    }
    public void StopScan()
    {

        ScanSettings scanSetting = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        List<ScanFilter> filters = new ArrayList<ScanFilter>();

        leScanner.stopScan(mBleScanCallback);

    }

    //Assign sensorName and ID
    void EnrollSensor(String sensorName,int sensorID)
    {
        sensor.sensorID = sensorID;
        sensor.sensorName = sensorName;

        appState = LE_APP_STATE_SENSOR_ENROLL;
    }

    void SendCallbackNotifications(final EventDetails eventDetails)
    {
        new Thread(new Runnable() {

            public void run() {
                // TODO Auto-generated method stub
                for(int i=0;i<callbacks.size();++i) callbacks.get(i).SensorEvent(eventDetails);

            }
        }).start();

    }

    SensorDevType GetSensorType( byte sensorTypeVal)
    {
        switch(sensorTypeVal)
        {
            case 0: return SensorDevType.DoorSensor;
            case 11: return SensorDevType.Current_Sensor;

        }
        return SensorDevType.DoorSensor;
    }

    int GetDeviceType(byte [] scanRes)
    {
        int devID = (int)((int)scanRes[LE_ADVT_OFFSET_DEVTYPE+1]*256+(int)(scanRes[LE_ADVT_OFFSET_DEVTYPE]&0x00ff));
        return devID;
    }
    void ProcessAdvtMessages(byte [] scanRes, ScanResult result)
    {
        int devType = GetDeviceType(scanRes);



        EventDetails eventDetMast = new EventDetails();
        BluetoothDevice btDev = result.getDevice();

        if(btDev!=null)
        {
            eventDetMast.btAddress = btDev.getAddress();
        }
        else
        {
            eventDetMast.btAddress = null;
        }

        if((BleAdvertiserEx.lastDevType==devType)&&(BleAdvertiserEx.lastSendAddress.contains(eventDetMast.btAddress))/*&&(BleAdvertiserEx.lastSeqNum==scanRes[LE_ADVT_OFFSET_SEQNO])*/&&BleAdvertiserEx.advtProgress)
        {
            BleAdvertiserEx.advtProgress=false;
            eventDetMast.advtReply = true;
            SendCallbackNotifications(eventDetMast);
        }

        if((devType)==0x81)//Iron
        {
            eventDetMast.devType = devType;
            eventDetMast.seqNum = scanRes[LE_ADVT_OFFSET_SEQNO];
            IronBoxEventDetails eventDet = new IronBoxEventDetails();
            eventDet.ironType = IronType.DryIron;

            switch(scanRes[LE_ADVT_OFFSET_CMD])
            {
                case IRON_CMD_ON:
                    eventDet.eventType = IronEventType.On;
                    break;
                case IRON_CMD_MOTION:
                    eventDet.eventType = IronEventType.MotionDetected;
                    break;
                case IRON_CMD_IDLE_HORIZ:
                    eventDet.eventType = IronEventType.IdleHorizontal;
                    eventDet.idleTime = scanRes[LE_ADVT_OFFSET_SUBCMD];
                case IRON_CMD_IDLE_VERT:
                    eventDet.eventType = IronEventType.IdleVertical;
                    eventDet.idleTime = scanRes[LE_ADVT_OFFSET_SUBCMD];
                    break;
                case IRON_CMD_OFF:
                    eventDet.eventType = IronEventType.Off;
                    break;
            }

            eventDetMast.eventData = eventDet;

            SendCallbackNotifications(eventDetMast);

        }
        if(devType==11)//Current sensors
        {
            // eventDetMast = new EventDetails();
            eventDetMast.seqNum = scanRes[LE_ADVT_OFFSET_SEQNO];
            eventDetMast.devType = devType;

            SensorEventDetails eventDet = new SensorEventDetails();
            eventDet.sensorDevType = SensorDevType.Current_Sensor;
            eventDet.eventType = SensorEventType.EnrollReq;
            BluetoothDevice btDev2 = result.getDevice();
            if(btDev!=null)
            {
                eventDet.sensorAddress = btDev2.getAddress();
            }
            else
            {
                eventDet.sensorAddress = null;
            }

            eventDet.watts = ((int)(scanRes[LE_ADVT_OFFSET_CUSTDATA]&0xff)+ (int)(scanRes[LE_ADVT_OFFSET_CUSTDATA+1]&0xff)*255);
            eventDet.wattsToday = ((int)(scanRes[LE_ADVT_OFFSET_CUSTDATA+2]&0xff)+ (int)(scanRes[LE_ADVT_OFFSET_CUSTDATA+3]&0xff)*255);
            eventDet.wattsMonth = ((int)(scanRes[LE_ADVT_OFFSET_CUSTDATA+4]&0xff)+ (int)(scanRes[LE_ADVT_OFFSET_CUSTDATA+5]&0xff)*255);
            eventDet.custData = scanRes;

            eventDetMast.eventData =  eventDet;

            SendCallbackNotifications(eventDetMast);
        }
        if(devType==0) //Security Sensor Door
        {


            switch(scanRes[LE_ADVT_OFFSET_CMD])
            {
                case LE_ADVT_CMD_SENSOR:

                    if(scanRes[LE_ADVT_OFFSET_SUBCMD]==LE_ADVT_SUBCMD_SENSOR_ENROLL) //Enroll request from sensor
                    {

                        eventDetMast.devType = devType;
                        eventDetMast.seqNum = scanRes[LE_ADVT_OFFSET_SEQNO];

                        SensorEventDetails eventDet = new SensorEventDetails();
                        eventDet.sensorDevType = GetSensorType(scanRes[LE_ADVT_OFFSET_DEVTYPE]);
                        eventDet.eventType = SensorEventType.EnrollReq;
                        BluetoothDevice btDev2 = result.getDevice();
                        if(btDev!=null)
                        {
                            eventDet.sensorAddress = btDev2.getAddress();
                        }
                        else
                        {
                            eventDet.sensorAddress = null;
                        }


                        eventDet.custData = scanRes;

                        eventDetMast.eventData = eventDet;
                        SendCallbackNotifications(eventDetMast);
                    }
                    if(scanRes[LE_ADVT_OFFSET_SUBCMD]==LE_ADVT_SUBCMD_SENSOR_TRIG)
                    {

                        eventDetMast.devType = devType;
                        eventDetMast.seqNum = scanRes[LE_ADVT_OFFSET_SEQNO];

                        SensorEventDetails eventDet = new SensorEventDetails();
                        eventDet.sensorDevType = GetSensorType(scanRes[LE_ADVT_OFFSET_DEVTYPE]);
                        eventDet.eventType = SensorEventType.SensorTrigger;
                        BluetoothDevice btDev2 = result.getDevice();
                        if(btDev!=null)
                        {
                            eventDet.sensorAddress = btDev2.getAddress();
                        }
                        else
                        {
                            eventDet.sensorAddress = null;
                        }
                        eventDet.seqNum = scanRes[LE_ADVT_OFFSET_SUBCMD+1];
                        eventDet.custData = scanRes;
                        eventDet.level = ((int) scanRes[LE_ADVT_OFFSET_CUSTDATA]*255)+((int)scanRes[LE_ADVT_OFFSET_CUSTDATA+1]);

                        eventDet.custData = scanRes;

                        eventDetMast.eventData = eventDet;
                        SendCallbackNotifications(eventDetMast);

                    }

                    if(scanRes[LE_ADVT_OFFSET_SUBCMD]==LE_ADVT_SUBCMD_SENSOR_LOWBAT)
                    {
                        eventDetMast = new EventDetails();
                        eventDetMast.devType = devType;
                        eventDetMast.seqNum = scanRes[LE_ADVT_OFFSET_SEQNO];

                        SensorEventDetails eventDet = new SensorEventDetails();
                        eventDet.sensorDevType = GetSensorType(scanRes[LE_ADVT_OFFSET_DEVTYPE]);
                        eventDet.eventType = SensorEventType.SensorBatLow;
                        BluetoothDevice btDev2 = result.getDevice();
                        if(btDev!=null)
                        {
                            eventDet.sensorAddress = btDev2.getAddress();
                        }
                        else
                        {
                            eventDet.sensorAddress = null;
                        }

                        eventDet.custData = scanRes;
                        eventDet.level = scanRes[LE_ADVT_OFFSET_BATLEV];

                        eventDet.custData = scanRes;

                        eventDetMast.eventData = eventDet;
                        SendCallbackNotifications(eventDetMast);

                    }

                    if(scanRes[LE_ADVT_OFFSET_SUBCMD]==LE_ADVT_SUBCMD_SENSOR_HB)
                    {
                        eventDetMast = new EventDetails();
                        eventDetMast.devType = devType;
                        eventDetMast.seqNum = scanRes[LE_ADVT_OFFSET_SEQNO];

                        SensorEventDetails eventDet = new SensorEventDetails();
                        eventDet.sensorDevType = GetSensorType(scanRes[LE_ADVT_OFFSET_DEVTYPE]);
                        eventDet.eventType = SensorEventType.SensorHB;
                        BluetoothDevice btDev2 = result.getDevice();
                        if(btDev!=null)
                        {
                            eventDet.sensorAddress = btDev2.getAddress();
                        }
                        else
                        {
                            eventDet.sensorAddress = null;
                        }

                        eventDet.custData = scanRes;
                        eventDet.level = scanRes[LE_ADVT_OFFSET_BATLEV];

                        eventDet.custData = scanRes;

                        eventDetMast.eventData = eventDet;
                        SendCallbackNotifications(eventDetMast);

                    }


            }
        }

        if((devType==131) ||(devType==130))//Plugtop or LED bulb
        {
            RelayModule rm = new RelayModule();
            rm.devType = RelayModuleType.Plugtop;
            rm.numChannels = 1;
            switch(scanRes[LE_ADVT_OFFSET_CMD])
            {
                case PLUGTOP_CMD_POWERON:
                    rm.commandType=PLUGTOP_CMD_POWERON;
                    rm.eventType = RelayEvents.RelayPowerOn;
                    if(scanRes[LE_ADVT_OFFSET_SUBCMD]==0)
                    { rm.channelNum=0; rm.channelStatus=0;}
                    else
                    { rm.channelNum=0; rm.channelStatus=1;}
                    break;
                case PLUGTOP_CMD_ONOFF_REPLY:
                    rm.commandType=PLUGTOP_CMD_ONOFF_REPLY;
                    break;
                case PLUGTOP_CMD_STATUS_QUERY_REPLY:
                    rm.commandType=PLUGTOP_CMD_STATUS_QUERY_REPLY;
                    if(scanRes[LE_ADVT_OFFSET_SUBCMD]==0)
                    { rm.channelNum=0; rm.channelStatus=0;rm.eventType = RelayEvents.ChannelOn;}
                    else
                    { rm.channelNum=0; rm.channelStatus=1;rm.eventType = RelayEvents.ChannelOff;}
                    break;
            }

            eventDetMast.devType = devType;
            eventDetMast.seqNum = scanRes[LE_ADVT_OFFSET_SEQNO];
            eventDetMast.eventData = rm;
            SendCallbackNotifications(eventDetMast);
        }

        if((devType)==135)
        {
            switch(scanRes[LE_ADVT_OFFSET_CMD]) {
                case LE_ADVT_CMD_SENSOR:

                    if (scanRes[LE_ADVT_OFFSET_SUBCMD] == LE_ADVT_SUBCMD_SENSOR_ENROLL) //Enroll request from sensor
                    {

                        eventDetMast.devType = devType;
                        eventDetMast.seqNum = scanRes[LE_ADVT_OFFSET_SEQNO];

                        SensorEventDetails eventDet = new SensorEventDetails();
                        eventDet.sensorDevType = GetSensorType(scanRes[LE_ADVT_OFFSET_DEVTYPE]);
                        eventDet.eventType = SensorEventType.EnrollReq;
                        BluetoothDevice btDev2 = result.getDevice();
                        if (btDev != null) {
                            eventDet.sensorAddress = btDev2.getAddress();
                        } else {
                            eventDet.sensorAddress = null;
                        }


                        eventDet.custData = scanRes;

                        eventDetMast.eventData = eventDet;
                        SendCallbackNotifications(eventDetMast);
                    }
            }
        }

    }

  /*
  Advt Data format
  Offset      Len     Desc                        Value
   0          1       Advt Type
   1          1		  Company Data
   2   		  1		  Company Data
   3          4       Signature                   "MIPS"
   7          1       Encription                  0
   8          1       Command-Sensor              10
   9          1       Sub Command Enroll          1
   10          2       Source Dev ID               0x0000
   12          2       Target Dev ID               0x0000
   */

    private ScanCallback mokoScanCallback = new ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {
            if (result != null) {
                BluetoothDevice device = result.getDevice();
                byte[] scanRecord = result.getScanRecord().getBytes();
                int rssi = result.getRssi();
                if (TextUtils.isEmpty(device.getName()) || scanRecord.length == 0 || rssi == 127) {
                    return;
                }
                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.name = device.getName();
                deviceInfo.rssi = rssi;
                deviceInfo.mac = device.getAddress();
                String scanRecordStr = bytesToHexString(scanRecord);
                deviceInfo.scanRecord = scanRecordStr;
                deviceInfo.scanResult = result;

                parseDeviceInfo(deviceInfo);
            }

        }
    };


    public static void writeLog(String content, String event)
    {

        try {

            String filePath = "";
            File file = null;
            try {
                filePath = Environment.getExternalStorageDirectory()
                        + "/BLE" + "/Log";
                if (!(new File(Environment.getExternalStorageDirectory()
                        + "/BLE" + "/")).exists()) {
                    (new File(Environment.getExternalStorageDirectory()
                            + "/BLE" + "/")).mkdirs();
                }
                file = new File(filePath);

            } catch (Exception e) {
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            System.out.println(dateFormat.format(date));

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(dateFormat.format(date) + " : " + content + " : " +event
                    + "\r\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeLogAxis(String content, String event)
    {

        try {

            String filePath = "";
            File file = null;
            try {
                filePath = Environment.getExternalStorageDirectory()
                        + "/BLE" + "/LogAxis";
                if (!(new File(Environment.getExternalStorageDirectory()
                        + "/BLE" + "/")).exists()) {
                    (new File(Environment.getExternalStorageDirectory()
                            + "/BLE" + "/")).mkdirs();
                }
                file = new File(filePath);

            } catch (Exception e) {
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            System.out.println(dateFormat.format(date));

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(dateFormat.format(date) + " : " + content + " : " +event
                    + "\r\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BeaconInfo parseDeviceInfo(DeviceInfo deviceInfo) {
        ScanResult result = deviceInfo.scanResult;
        SparseArray<byte[]> manufacturer = result.getScanRecord().getManufacturerSpecificData();
        if (manufacturer == null || manufacturer.size() == 0) {
            return null;
        }
        String manufacturerSpecificData = MokoUtils.bytesToHexString(result.getScanRecord().getManufacturerSpecificData(manufacturer.keyAt(0)));
        if (TextUtils.isEmpty(manufacturerSpecificData) || !manufacturerSpecificData.startsWith("0215")) {
            return null;
        }
        // 0215fda50693a4e24fb1afcfc6eb0764782500000000c5
        // 0215e2c56db5dffb48d2b060d0f5a71096e000000000b0
        // LogModule.i("ManufacturerSpecificData:" + MokoUtils.bytesToHexString(result.getScanRecord().getManufacturerSpecificData(manufacturer.keyAt(0))));
        Map<ParcelUuid, byte[]> map = result.getScanRecord().getServiceData();
        if (map == null || map.isEmpty()) {
            return null;
        }
        String serviceDataUuid = null;
        String serviceData = null;
        for (ParcelUuid uuid : map.keySet()) {
            // 0000ff00-0000-1000-8000-00805f9b34fb
            // 0000ff01-0000-1000-8000-00805f9b34fb
            serviceDataUuid = uuid.getUuid().toString();
            if (!serviceDataUuid.startsWith("0000ff00") && !serviceDataUuid.startsWith("0000ff01")) {
                return null;
            }
            // 5e000000005080
            // 64000000005081
            serviceData = MokoUtils.bytesToHexString(result.getScanRecord().getServiceData(uuid));
            if (TextUtils.isEmpty(serviceData)) {
                return null;
            }
        }
        // uuid
        String hexString = manufacturerSpecificData.substring(4, 36);
        StringBuilder sb = new StringBuilder();
        sb.append(hexString.substring(0, 8));
        sb.append("-");
        sb.append(hexString.substring(8, 12));
        sb.append("-");
        sb.append(hexString.substring(12, 16));
        sb.append("-");
        sb.append(hexString.substring(16, 20));
        sb.append("-");
        sb.append(hexString.substring(20, 32));
        String uuid = sb.toString().toUpperCase();

        byte[] manufacturerSpecificDataByte = result.getScanRecord().getManufacturerSpecificData(manufacturer.keyAt(0));
        int major = (manufacturerSpecificDataByte[18] & 0xff) * 0x100 + (manufacturerSpecificDataByte[19] & 0xff);
        int minor = (manufacturerSpecificDataByte[20] & 0xff) * 0x100 + (manufacturerSpecificDataByte[21] & 0xff);
        int battery = Integer.parseInt(serviceData.substring(0, 2), 16);

        // 连接状态在版本号最高位，0不可连接，1可连接，判断后将版本号归位
        String versionStr = MokoUtils.hexString2binaryString(serviceData.substring(12, 14));
        // LogModule.i("version binary: " + versionStr);
        String connState = versionStr.substring(0, 1);
        boolean isConnected = Integer.parseInt(connState) == 1;
        String versionBinary = isConnected ? "0" + versionStr.substring(1, versionStr.length()) : versionStr;
        int version = Integer.parseInt(MokoUtils.binaryString2hexString(versionBinary), 16);
        // distance
        int acc = Integer.parseInt(serviceData.substring(10, 12), 16);
        String mac = deviceInfo.mac;
        double distance = MokoUtils.getDistance(deviceInfo.rssi, acc);
        String distanceDesc = "Unknown";
        if (distance <= 0.1) {
            distanceDesc = "Immediate";
        } else if (distance > 0.1 && distance <= 1.0) {
            distanceDesc = "Near";
        } else if (distance > 1.0) {
            distanceDesc = "Far";
        }
        // txPower;
        byte[] scanRecord = MokoUtils.hex2bytes(deviceInfo.scanRecord);
        // log
        String log = MokoUtils.bytesToHexString(scanRecord);
        int txPower = scanRecord[32];

        // services
        String threeAxis = null;
        if (serviceDataUuid != null && serviceDataUuid.startsWith("0000ff01")) {
            byte[] threeAxisBytes = new byte[6];
            System.arraycopy(scanRecord, 44, threeAxisBytes, 0, 6);
            threeAxis = MokoUtils.bytesToHexString(threeAxisBytes).toUpperCase();
        }
        // ========================================================
        String distanceStr = new DecimalFormat("#0.00").format(distance);
        BeaconInfo beaconInfo = new BeaconInfo();
        beaconInfo.name = deviceInfo.name;
        beaconInfo.rssi = deviceInfo.rssi;
        beaconInfo.distance = distanceStr;
        beaconInfo.distanceDesc = distanceDesc;
        beaconInfo.major = major;
        beaconInfo.minor = minor;
        beaconInfo.txPower = txPower;
        beaconInfo.uuid = uuid;
        beaconInfo.batteryPower = battery;
        beaconInfo.version = version;
        beaconInfo.scanRecord = log;
        beaconInfo.isConnected = isConnected;
        beaconInfo.mac = mac;
        beaconInfo.threeAxis = threeAxis;
        writeLog("ScanRES",deviceInfo.scanRecord);
        writeLogAxis("Axis",threeAxis);
        return beaconInfo;


    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }



    private ScanCallback mBleScanCallback = new ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {
            ScanRecord scanRec = result.getScanRecord();
            byte [] scanRes =  scanRec.getBytes();
            byte [] sig = new byte [2];
            System.arraycopy(scanRes, LE_ADVT_OFFSET_SIG, sig, 0, 2);

            BluetoothDevice advtDev = result.getDevice();
            String address = advtDev.getAddress();

            // ShowText(address);

            String sigs = new String(sig);
            if(sigs.contains("ME")||(sigs.contains("MS"))||sigs.contains("MI"))
            {
                ProcessAdvtMessages(scanRes,result);
            }
        }
    };



    class BleThread extends Thread
    {

        public Handler handler;
        @Override
        public void run()
        {
            Looper.prepare();
            handler = new Handler()
            {
                public void handleMessage(Message msg)
                {
                    switch(msg.what)
                    {
                        case 100:
                            StartScan();
                            break;
                        case 200:

                            break;
                    }
                }
            };

            handler.sendEmptyMessageDelayed(100, 200);
            Looper.loop();
        }

    }

    void ShowText(final String mesg)
    {
        parent.runOnUiThread(new Runnable() {

            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(parent, mesg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
