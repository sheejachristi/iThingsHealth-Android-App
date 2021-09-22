package com.iorbit_tech.healthcare.caretakerapp.ble;

public class BleAdvertiserEx extends BleAdvertiser
{
    static int LE_ADVTDATA_OFFSET = 0;
    static int LE_ADVT_OFFSET_SIG = 		LE_ADVTDATA_OFFSET+0;
    static int LE_ADVT_OFFSET_DESTADD = 	LE_ADVTDATA_OFFSET+2;
    static int LE_ADVT_OFFSET_ENCTYPE= 	LE_ADVTDATA_OFFSET+2+6;
    static int LE_ADVT_OFFSET_DEVTYPE= 	LE_ADVTDATA_OFFSET+2+6+1;
    static int LE_ADVT_OFFSET_CMD = 		LE_ADVTDATA_OFFSET+2+6+1+2;
    static int LE_ADVT_OFFSET_SUBCMD = 	LE_ADVTDATA_OFFSET+2+6+1+2+1;
    static int LE_ADVT_OFFSET_SEQNO =  	LE_ADVTDATA_OFFSET+2+6+1+2+1+1;
    static int LE_ADVT_OFFSET_MAXHOPS =	LE_ADVTDATA_OFFSET+2+6+1+2+1+1+1;
    static int LE_ADVT_OFFSET_CUSTDATA =	LE_ADVTDATA_OFFSET+2+6+1+2+1+1+1+1;
    static int LE_ADVT_DATA_OFFSET	=LE_ADVTDATA_OFFSET+2+6+1+2+1+1+1+1+1;

    static String lastSendAddress="";
    static byte lastSeqNum;
    static int lastDevType;
    static boolean advtProgress;
    byte [] payLoad = new byte[24];

    public void FillPayloadDefVals()
    {
        SetSignature();
        SetDestDevAddressZero();
        payLoad[LE_ADVT_OFFSET_ENCTYPE]=0;
        SetHops((byte)3);
    }

    public boolean AdvertiseBookingID(String tocken)
    {
        if(tocken.length()<10){
            for(int i=tocken.length();i<10;++i){
                tocken="0"+tocken;
            }
        }
        SetTocken("bk:"+tocken);
        StartAdvertising(10);
        return true;
    }

    public boolean SetTocken(String tocken){
        if(tocken.length()<=13) {
            String pl = "Tok-" + tocken;
            payLoad = new byte[24];
            byte [] payl = pl.getBytes();
            System.arraycopy(payl,0,payLoad,0,payl.length);
            return true;
        }
        return false;
    }

    public boolean SetWIFIHotSpot(String hostSpotName){

        System.out.println("wifihotspot"+hostSpotName);
        if(hostSpotName.length()<=13) {
            String pl = "WIH-" + hostSpotName;
            payLoad = new byte[24];
            byte [] payl = pl.getBytes();
            System.arraycopy(payl,0,payLoad,0,payl.length);
            StartAdvertising(20);
            return true;
        }
        return false;
    }

    public boolean SetWIFIPassword(String passWord){
        System.out.println("wifihotspot"+passWord);
        if(passWord.length()<=13) {
            String pl = "WIP-" + passWord;
            payLoad = new byte[24];
            byte [] payl = pl.getBytes();
            System.arraycopy(payl,0,payLoad,0,payl.length);
            StartAdvertising(10);
            return true;
        }
        return false;
    }
    public void SetSignature()
    {
        payLoad[LE_ADVT_OFFSET_SIG] ='M';
        payLoad[LE_ADVT_OFFSET_SIG+1] ='E';
    }

    public void SetHops(byte val)
    {
        payLoad[LE_ADVT_OFFSET_MAXHOPS] =(byte) val;
    }
    public void SetSeqNum(byte val)
    {
        payLoad[LE_ADVT_OFFSET_SEQNO] =(byte) val;
        lastSeqNum = val;
    }
    public void SetCommandID(byte command)
    {
        payLoad[LE_ADVT_OFFSET_CMD] =(byte) command;
    }

    public void SetSubCommandID(byte command)
    {
        payLoad[LE_ADVT_OFFSET_SUBCMD] =(byte) command;
    }

    public void setDataOffset(byte[] data)
    {
        for(int i=0;i<data.length;i++)
            payLoad[LE_ADVT_DATA_OFFSET+i]=data[i];
    }

    public boolean SetDestDevAddress(String address)
    {
        String parts[] = address.split(":");
        lastSendAddress=address;
        if(parts.length!=6) return false;
        for(int i=0;i<6;++i)
        {
            payLoad[LE_ADVT_OFFSET_DESTADD+i] = (byte)(Integer.parseInt(parts[i], 16)&0x00ff);
        }
        return true;
    }

    public boolean SetDestDevAddressZero()
    {
        for(int i=0;i<6;++i)
        {
            payLoad[LE_ADVT_OFFSET_DESTADD+i] =0;
        }
        return true;
    }

    public void SetDevType(int devType)
    {
        lastDevType = devType;
        payLoad[LE_ADVT_OFFSET_DEVTYPE]= (byte)(devType&0x00ff);
        payLoad[LE_ADVT_OFFSET_DEVTYPE+1]= (byte)((devType>>8)&0x00ff);
    }

    public void StartAdvertising(int secs)
    {
        advtProgress = true;
        StartAdvertising(payLoad, secs);
    }
    public void StopAdvertising(){
        StopAdvt();
    }
}
