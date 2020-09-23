package com.wiicamp.reactnativexprinter

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import net.posprinter.posprinterface.IMyBinder
import net.posprinter.posprinterface.UiExecute
import net.posprinter.utils.DataForSendToPrinterPos58
import java.io.UnsupportedEncodingException
import java.net.URL


class XprinterModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
  //IMyBinder interface，All methods that can be invoked to connect and send data are encapsulated within this interface
  lateinit var binder: IMyBinder
  var ISCONNECT = false
  val DISCONNECT = "com.posconsend.net.disconnetct"

  //bindService connection
  val conn: ServiceConnection = object : ServiceConnection {
    override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
      //Bind successfully
      binder = iBinder as IMyBinder

      binder.connectNetPort("127.0.0.1", 9100, object : UiExecute {
        override fun onsucess() {
          ISCONNECT = true

          binder.acceptdatafromprinter(object : UiExecute {
            override fun onsucess() {

            }

            override fun onfailed() {
              ISCONNECT = false;
              val intent = Intent();
              intent.action = DISCONNECT;
              reactContext.sendBroadcast(intent);
            }
          });
        }

        override fun onfailed() {
          ISCONNECT = false
        }

      })
      Log.e("connect binder", "connected")
    }

    override fun onServiceDisconnected(componentName: ComponentName) {
      Log.e("disconnect binder", "disconnected")
    }
  }

  override fun getName(): String {
    return "Xprinter"
  }

  override fun initialize() {
    super.initialize()


  }

  @ReactMethod
  fun connectViaNetwork(ipAddress: String, promise: Promise) {
    if (ipAddress.isNullOrEmpty()) {
      promise.reject("INVALID_IP_ADDRESS", "Invalid ip address. Please call connectViaNetwork with valid ip address.");
      return;
    }
  }

  @ReactMethod
  fun print(text: String) {
//    val bytes = URL(filePath).readBytes()
    val list = ArrayList<ByteArray>()

    list.add(DataForSendToPrinterPos58.initializePrinter())
    val data1: ByteArray = strTobytes(text)
    list.add(data1)
    //should add the command of print and feed line,because print only when one line is complete, not one line, no print
    //should add the command of print and feed line,because print only when one line is complete, not one line, no print
    list.add(DataForSendToPrinterPos58.printAndFeedLine())
    //cut pager
    //cut pager
    list.add(DataForSendToPrinterPos58.selectOrCancelCW90(1))

//    binder.write(DataForSendToPrinterPos58., object: UiExecute {
//      override fun onsucess() {
//        TODO("Not yet implemented")
//      }
//
//      override fun onfailed() {
//        TODO("Not yet implemented")
//      }
//
//    });
  }

  private fun strTobytes(str: String): ByteArray {
    var b: ByteArray = ByteArray(0);
    var data: ByteArray = ByteArray(0);
    try {
      b = str.toByteArray(charset("utf-8"))
      data = String(b, Charsets.UTF_8).toByteArray(charset("gbk"))
    } catch (e: UnsupportedEncodingException) {
      // TODO Auto-generated catch block
      e.printStackTrace()
    }
    return data
  }
}
