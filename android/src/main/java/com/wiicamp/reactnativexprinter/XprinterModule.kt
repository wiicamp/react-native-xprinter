package com.wiicamp.reactnativexprinter

import android.bluetooth.BluetoothAdapter
import android.content.*
import android.os.IBinder
import android.util.Log
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import net.posprinter.posprinterface.IMyBinder
import net.posprinter.posprinterface.ProcessData
import net.posprinter.posprinterface.UiExecute
import net.posprinter.service.PosprinterService
import net.posprinter.utils.DataForSendToPrinterPos58
import java.io.UnsupportedEncodingException


class XprinterModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
  var binder: IMyBinder? = null;
  var ISCONNECT = false
  var DISCONNECT = "com.posconsend.net.disconnetct"

  private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
  private val InnerPrinterBluetoothMAC: String = "00:11:22:33:44:55"

  private var serviceConnection: ServiceConnection = object : ServiceConnection {
    override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
      Log.v("XPrinter", "connected")
      binder = iBinder as? IMyBinder;

//
//      //ipAddress :ip address; portal:9100
//      binder?.connectNetPort("192.168.0.106", 9100, object : UiExecute {
//        override fun onsucess() {
//          Log.v("XPrinter", "Connect success to ip")
//          binder?.acceptdatafromprinter(object : UiExecute {
//            override fun onsucess() {}
//            override fun onfailed() {}
//          })
//        }
//
//        override fun onfailed() {
//          Log.v("XPrinter", "Connect fail to ip")
//        }
//      })
    }

    override fun onServiceDisconnected(componentName: ComponentName) {
      Log.v("XPrinter", "disconnected")
//      mIPosPrinterService = null
    }
  };

  override fun getName(): String {
    return "Xprinter"
  }

  @ReactMethod
  fun print(imageUrl: String, promise: Promise) {
    if (bluetoothAdapter.isEnabled == false) {
      //open bluetooth
      val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
      reactApplicationContext.startActivityForResult(intent, 1, null)
    } else {
      binder?.connectBtPort(InnerPrinterBluetoothMAC, object : UiExecute {
        override fun onsucess() {
          ISCONNECT = true
          sendDataToPrinter(imageUrl)
        }

        override fun onfailed() {
          ISCONNECT = false
        }
      })
    }
  }

  override fun initialize() {
    super.initialize()

    val intent = Intent()
    intent.setClass(reactApplicationContext.baseContext, PosprinterService::class.java)
    reactApplicationContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
  }

  override fun invalidate() {
    super.invalidate()

    binder?.disconnectCurrentPort(object : UiExecute {
      override fun onsucess() {}
      override fun onfailed() {}
    })

    reactApplicationContext.unbindService(serviceConnection)
  }

  private fun sendDataToPrinter(text: String) {
    binder?.writeDataByYouself(object : UiExecute {
      override fun onsucess() {
        Log.v("XPrinter", "writeDataByYourself")
      }
      override fun onfailed() {}
    }, object : ProcessData {
      override fun processDataBeforeSend(): List<ByteArray> {
        val list = ArrayList<ByteArray>()

        // Command: initialize printer
        list.add(DataForSendToPrinterPos58.initializePrinter())

        // Contents
        list.add(stringToBytes("Name: Stanley Cohen"))
        list.add(DataForSendToPrinterPos58.printAndFeedLine())
        list.add(stringToBytes("Phone: 0987-458-772"))
        list.add(DataForSendToPrinterPos58.printAndFeedLine())
        list.add(stringToBytes("Address:"))
        list.add(DataForSendToPrinterPos58.printAndFeedLine())
        list.add(stringToBytes("[4.5 km] 3817 Edwards Cedar, ..."))
        list.add(DataForSendToPrinterPos58.printAndFeedLine())
        list.add(stringToBytes("--------------------------------"))
        list.add(DataForSendToPrinterPos58.printAndFeedLine())
        list.add(stringToBytes("Wantons Soup with ...(L)   $7.25"))
        list.add(DataForSendToPrinterPos58.printAndFeedLine())
        list.add(stringToBytes("x1"))
        list.add(DataForSendToPrinterPos58.printAndFeedLine())
        list.add(stringToBytes("Hot and Sour Soup          $4.25"))
        list.add(DataForSendToPrinterPos58.printAndFeedLine())
        list.add(stringToBytes("x1"))
        list.add(DataForSendToPrinterPos58.printAndFeedLine())
        list.add(stringToBytes("--------------------------------"))
        list.add(DataForSendToPrinterPos58.printAndFeedLine())
        list.add(stringToBytes("Subtotal (2 items)         $11.5"))
        list.add(DataForSendToPrinterPos58.printAndFeedLine())
        list.add(stringToBytes("Delivery [4.5 km]              $5"))
        list.add(DataForSendToPrinterPos58.printAndFeedLine())
        list.add(stringToBytes("--------------------------------"))
        list.add(stringToBytes("Total                        $14"))

        // Command: break line, should add, because print only when one line is complete, not one line, no print
        list.add(DataForSendToPrinterPos58.printAndFeed(100))
//                list.add(DataForSendToPrinterPos58.printAndFeedLine())

        return list
      }
    })
  }

  fun stringToBytes(str: String): ByteArray {
//        var b: ByteArray = ByteArray(0)
    var data: ByteArray = ByteArray(0)
    try {
//            b = str.toByteArray(charset("utf-8"))
      data = str.toByteArray(charset("gbk"))
    } catch (e: UnsupportedEncodingException) {
      // TODO Auto-generated catch block
      e.printStackTrace()
    }
    return data
  }
}
