package com.example.hot_plate_app;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.Snackbar;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.common.api.GoogleApiClient;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;


    //private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    Boolean btScanning = false;
    int deviceIndex = 0;
    //------Tim added in Testing
    public static final String TAG = "Hotplate App";
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    BluetoothDevice mDevice;
    private final static String DEVICE_NAME = "Tim";
    private final static String DEVICE_ADDRESS = "E2:62:C4:C7:04:EE";
    private UartService mService = null;
    private Button btnSend;
    private EditText edtMessage;
    private int mState = UART_PROFILE_DISCONNECTED;
    //------Tim added in Testing

    //------Tim added For Real
    private Button HeatButton;
    private Button StopButton;
    private EditText Setpoint1;
    //private EditText Setpoint2;
    private TextView CurrTemp1;
    //private TextView CurrTemp2;

    //The part of the data packet that will notify the arduino as to
    //wheather to heat the plates. either "ON" or "OFF"
    private String Heating_string = "OFF";
    //The part of the data packet that will contain the setpoints for
    //the two hot plates. Will be decimal numbers 0.0 up to XXX.X
    private String Setpoint1_string;
    //private String Setpoint2_string;

    private String receivedString;
    private String receivedSetpoint1;
    private String receivedSetpoint2;
    private String recievedTemp1;
    //private String recievedTemp2;


    //------Tim added For Real

    BluetoothGatt bluetoothGatt;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public Map<String, String> uuids = new HashMap<String, String>();

    // Stops scanning after 5 seconds.
    private Handler mHandler = new Handler();
    private static final long SCAN_PERIOD = 5000;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        //btnSend=(Button) findViewById(R.id.sendButton);
        //edtMessage = (EditText) findViewById(R.id.sendText);

        //-----------Tim added for Reals
        //Get a reference to all the Widgets used in the UI
        HeatButton = (Button) findViewById(R.id.Heat_Button);
        StopButton = (Button) findViewById(R.id.Stop_Button);
        Setpoint1 = (EditText) findViewById(R.id.Setpoint_Entry_1);
        //Setpoint2 =(EditText) findViewById(R.id.Setpoint_Entry_2);
        CurrTemp1 = (TextView) findViewById(R.id.Curr_Temp_Data_1);
        //CurrTemp2 = (TextView) findViewById(R.id.Curr_Temp_Data_2);

        HeatButton.setText("Press to Heat");
        StopButton.setText("Press to Stop");
        Setpoint1.setText("Enter Setpoint");
        //Setpoint2.setText("Enter Setpoint");
        CurrTemp1.setText("");
        //CurrTemp2.setText("");



        //-----------Tim added for Reals

        //Does some initialization on Uart Service
        service_init();


        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }

        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        startScanning();


        // Handle Send button
        /*btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editText = (EditText) findViewById(R.id.sendText);
                String message = editText.getText().toString();
                byte[] value;
                try {
                    //send data to service
                    value = message.getBytes("UTF-8");
                    mService.writeRXCharacteristic(value);
                    //Update the log with time stamp
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    Log.d(TAG, "Send button Pressed -- " + currentDateTimeString);
                    edtMessage.setText("");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });*/



        // Handle Heat button
        HeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean success1=false;
                //boolean success2=false;


                Setpoint1_string = Setpoint1.getText().toString();
                //Setpoint2_string = Setpoint2.getText().toString();

                //Checks to see if Setpoint fields contain nubers entered by the user
                try{
                    double d1 = Double.parseDouble(Setpoint1_string);
                    success1 = true;
                }catch(NumberFormatException nfe){
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.myConstraintLayout), "Enter a Decimal number for Setpoint 1", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
                /*try{
                    double d2 = Double.parseDouble(Setpoint2_string);
                    success2 = true;
                }catch(NumberFormatException nfe){
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.myConstraintLayout), "Enter a Decimal number for Setpoint 2", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }*/

                // The user has entered both setpoint fields correctlly
                //if(success1&&success2) {
                if(success1) {
                    //make first heating message;
                    Heating_string = "1,ON;";
                    //assemble string array that looks like ["1,ON;","2,3.45;","3,45.8"]
                    //String[] messages={Heating_string,"2,"+Setpoint1_string+";","3,"+Setpoint2_string+";"};
                    String[] messages={Heating_string,"2,"+Setpoint1_string+";"};
                    int index = 0;
                    Handler myHandler = new Handler();
                    //iterate over all the strings in the message array
                    for(final String text : messages)
                    {
                        //make a handler to allow for a delay between transmission
                        //of the messages
                        myHandler.postDelayed(new Runnable() {

                            @Override
                            public void run()
                            {
                                byte[] value;
                                try {
                                    value = text.getBytes("UTF-8");
                                    mService.writeRXCharacteristic(value);
                                    //Update the log with time stamp
                                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                                    Log.d(TAG, "Heating button Pressed -- " + currentDateTimeString);
                                }catch(UnsupportedEncodingException e){

                                    e.printStackTrace();
                                }catch(NullPointerException e){

                                    Log.d(TAG,"writeRXcharacteristic threw null pointer exception. Reconnecting to bluetooth gatt");
                                    mService.connect(DEVICE_ADDRESS);
                                }
                            }
                        }, 100 * index );
                    index += 1;
                }
            }
                }

                /*// The user has entered both setpoint fields correctlly
                if(success1&&success2) {

                    Heating_string = "ON";
                    String message = Heating_string + "," + Setpoint1_string + "," + Setpoint2_string + ";";
                    byte[] value;

                    try {
                        //send data to service
                        value = message.getBytes("UTF-8");
                        Log.d(TAG,"the Size of the byte array being sent: "+ Integer.toString(value.length));
                        try{
                            mService.writeRXCharacteristic(value);
                        }catch(NullPointerException e){
                            Log.d(TAG,"writeRXcharacteristic threw null pointer exception. Reconnecting to bluetooth gatt");
                            mService.connect(DEVICE_ADDRESS);


                        }

                        //Update the log with time stamp
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "Heating button Pressed -- " + currentDateTimeString);

                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }*/


        });

        // Handle Stop button click
        //Just sends the string "OFF;" to the arduino
        StopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean success1=false;
                //boolean success2=false;


                Setpoint1_string = Setpoint1.getText().toString();
                //Setpoint2_string = Setpoint2.getText().toString();

                //Checks to see if Setpoint fields contain nubers entered by the user
                try{
                    double d1 = Double.parseDouble(Setpoint1_string);
                    success1 = true;
                }catch(NumberFormatException nfe){
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.myConstraintLayout), "Enter a Decimal number for Setpoint 1", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
                /*try{double d2 = Double.parseDouble(Setpoint2_string);
                    success2 = true;
                }catch(NumberFormatException nfe){
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.myConstraintLayout), "Enter a Decimal number for Setpoint 2", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }*/


                // The user has entered both setpoint fields correctlly
                //if(success1&&success2) {
                if(success1) {

                    //make first heating message;
                    Heating_string = "1,OFF;";
                    //assemble string array that looks like ["1,ON;","2,3.45;","3,45.8"]
                    //String[] messages={Heating_string,"2,"+Setpoint1_string+";","3,"+Setpoint2_string+";"};
                    String[] messages={Heating_string,"2,"+Setpoint1_string+";"};
                    int index = 0;
                    Handler myHandler = new Handler();
                    //iterate over all the strings in the message array
                    for(final String text : messages)
                    {
                        //make a handler to allow for a delay between transmission
                        //of the messages
                        myHandler.postDelayed(new Runnable() {

                            @Override
                            public void run()
                            {
                                byte[] value;
                                try {
                                    value = text.getBytes("UTF-8");
                                    mService.writeRXCharacteristic(value);
                                    //Update the log with time stamp
                                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                                    Log.d(TAG, "Heating button Pressed -- " + currentDateTimeString);
                                }catch(UnsupportedEncodingException e){

                                    e.printStackTrace();
                                }catch(NullPointerException e){

                                    Log.d(TAG,"writeRXcharacteristic threw null pointer exception. Reconnecting to bluetooth gatt");
                                    mService.connect(DEVICE_ADDRESS);
                                }
                            }
                        }, 100 * index );
                        index += 1;
                    }
                }
                /*if(success1&&success2) {
                    // The user has entered both setpoint fields correctlly
                    Heating_string = "OFF";
                    String message = Heating_string + "," + Setpoint1_string + "," + Setpoint2_string + ";";
                    byte[] value;
                    try {
                        //send data to service
                        value = message.getBytes("UTF-8");
                        mService.writeRXCharacteristic(value);
                        //Update the log with time stamp
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "Stop Button Pressed -- " + currentDateTimeString);

                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }*/

            }
        });



    }

    //UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mService);
            //----Tim Added
            //edtMessage.setEnabled(true);
            //btnSend.setEnabled(true);
            mService.initialize();
            mService.connect(DEVICE_ADDRESS);
            //----Tim Added
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

        }

        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            mService = null;
        }
    };


    //Its in this block that the program receives data transmitted from the nrf8001
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
            //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_CONNECT_MSG");
                        //edtMessage.setEnabled(true);
                        //btnSend.setEnabled(true);
                        mState = UART_PROFILE_CONNECTED;
                    }
                });
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        //edtMessage.setEnabled(false);
                        //btnSend.setEnabled(false);
                        //((TextView) findViewById(R.id.deviceName)).setText("Not Connected");
                        mState = UART_PROFILE_DISCONNECTED;
                        mService.close();
                        //setUiState();

                    }
                });
            }


            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
                //mService.enableRXNotification();
            }
            //*********************//
            // this block is where the Uart data is received
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            receivedString = new String(txValue, "UTF-8");

                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            //Log.d(TAG, "receiving "+currentDateTimeString +" "+receivedString);
                            Log.d(TAG, "receiving "+currentDateTimeString +" "+receivedString);
                            String[] parts = receivedString.split(",|;|\r\n");
                            for(String part:parts){
                                Log.d(TAG, part+" was parsed from received string");


                            }
                            receivedSetpoint1 =parts[0];
                            //receivedSetpoint2 =parts[1];
                            recievedTemp1 = parts[0];
                            //recievedTemp2 = parts[1];
                            //Log.d(TAG, recievedTemp1+" "+recievedTemp2+" ");
                            Log.d(TAG, recievedTemp1);
                            CurrTemp1.setText(recievedTemp1);
                            //CurrTemp2.setText(recievedTemp2);


                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
            //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)){
                showMessage("Device doesn't support UART. Disconnecting");
                mService.disconnect();
            }


        }
    };

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    private void service_init() {
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        unbindService(mServiceConnection);
        mService.stopSelf();
        mService= null;

    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!btAdapter.isEnabled()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }



    //Tim Altered
    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //peripheralTextView.append("Index: " + deviceIndex + ", Device Name: " + result.getDevice().getName() + " rssi: " + result.getRssi() + "address"+result.getDevice().getAddress()+"\n");


            if(result.getDevice().getAddress().equals(DEVICE_ADDRESS)){
                System.out.println("conecting to "+result.getDevice().getName());
                connectToDeviceSelected(result.getDevice());

            }





        }
    };

    // Device connect call back
    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        // this will get called anytime you perform a read or write characteristic operation
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {

            //if(characteristic.getUuid().equals(mService.RX_CHAR_UUID)) {
            //System.out.println("Rx characteristic has changed Main Activity");
            //mService.readRxCharacteristic();
            //}
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    //peripheralTextView.append("device read or wrote to\n");

                }
            });
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects

            System.out.println(newState);
            switch (newState) {
                case 0:

                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {

                            //Tim added
                            gatt.connect();
                        }
                    });
                    break;
                case 2:

                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {

                        }
                    });


                    bluetoothGatt.discoverServices();

                    break;
                default:
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {

                        }
                    });
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {

            // this will get called after the client initiates a 			BluetoothGatt.discoverServices() call
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {

                }
            });
            displayGattServices(bluetoothGatt.getServices());
        }

        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);

                final String dataInput = characteristic.getValue().toString();
                System.out.println("Value read from "+characteristic.getUuid().toString()+" is "+dataInput);
            }
        }
    };

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {

        System.out.println(characteristic.getUuid());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    public void startScanning() {
        System.out.println("start scanning");
        btScanning = true;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScanning();
            }
        }, SCAN_PERIOD);

    }

    public void stopScanning() {
        System.out.println("stopping scanning");

        btScanning = false;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });


    }



    //Tim altered
    public void connectToDeviceSelected(BluetoothDevice device) {

        System.out.println("!!!!!!!!!!!!!!!!!!!!");
        System.out.println("Before connecting to " +device.getName());
        System.out.println("!!!!!!!!!!!!!!!!!!!!");


        try {
            bluetoothGatt = device.connectGatt(this, false, btleGattCallback);
        }catch(Exception e ){

            System.out.println("Caught " +e+" trying to connect to ble device");

        }

        System.out.println("!!!!!!!!!!!!!!!!!!!!");
        System.out.println("After After After " +device.getName());
        System.out.println("!!!!!!!!!!!!!!!!!!!!");
    }

    public void disconnectDeviceSelected() {

        bluetoothGatt.disconnect();
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {

            final String uuid = gattService.getUuid().toString();
            System.out.println("Service discovered: " + uuid);
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {

                }
            });
            new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic :
                    gattCharacteristics) {

                final String charUuid = gattCharacteristic.getUuid().toString();
                System.out.println("Characteristic discovered for service: " + charUuid);
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {

                    }
                });

            }
        }
    }

   /* @Override
    public void onStart() {
        super.onStart();

        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.joelwasserman.androidbleconnectexample/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }*/

    /*@Override
    public void onStop() {
        super.onStop();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.joelwasserman.androidbleconnectexample/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }*/

    /**---------------Added UART service code------------------------**/
    /**---------------Added UART service code------------------------**/
    /**---------------Added UART service code------------------------**/


}

