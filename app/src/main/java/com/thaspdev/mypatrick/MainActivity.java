package com.thaspdev.mypatrick;

import android.app.Activity;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DevicesList.OnFragmentInteractionListener, CamAndControlFragment.OnFragmentInteractionListener {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ArrayAdapter<String> appareilsConnectés;
    ArrayAdapter<String> arrayAdapterAppareils;
    String chaineCaractèresAppareilsAppairés;
    ConnectThread connect = null;
    Handler mHandler;
    boolean btON = false;
    String BTmessage = "";
    ConnectedThread connected = null;
    BluetoothSocket socketBT;
    InputStream is;
    OutputStream os;
    FragmentManager manager = getSupportFragmentManager();
    BluetoothDevice appareilBTChoisi = null;
    ThreadBT threadBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO: Supprimer ou trouver une utilité à ce bouton
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        chaineCaractèresAppareilsAppairés = getResources().getString(R.string.paired);//On récupère la valeur de cette chaîne de caractère depuis les ressources pour supporter plusieurs langues

        appareilsConnectés = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
        arrayAdapterAppareils = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch(msg.what){
                    case 0://Si on a réussi a se connecter
                        //TODO: Do something
                        connected = new ConnectedThread((BluetoothSocket) msg.obj);
                        Toast.makeText(getApplicationContext(),"Successfully connected", Toast.LENGTH_SHORT).show();
                        connected.write("Test message from an android phone".getBytes());
                        break;
                    case 1://Si MESSAGE_READ (si on lit un message)
                        break;
                    case 2:
                        if (connected != null){
                            connected.write(BTmessage.getBytes());
                        }
                }
            }
        /*@Override
        public void publish(LogRecord logRecord) {

        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }*/
        };

        //Amène l'utilisateur directement dans la partie HomeFragment
        HomeFragment homeFrag = new HomeFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content_main, homeFrag).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the home action
            HomeFragment homeFrag = new HomeFragment();
            manager.beginTransaction().replace(R.id.content_main, homeFrag).commit();
        } else if (id == R.id.nav_config) {
            ConfigFragment configFrag = new ConfigFragment();
            manager.beginTransaction().replace(R.id.content_main, configFrag).commit();
        } else if (id == R.id.nav_cam_control) {
            CamAndControlFragment camAndControlFrag = new CamAndControlFragment();
            manager.beginTransaction().replace(R.id.content_main, camAndControlFrag).commit();
        } else if (id == R.id.nav_dashboard) {

        } else if (id == R.id.nav_account_settings) {

        } else if (id == R.id.nav_settings) {
            if(createBluetooth()){
                DevicesList devList = new DevicesList();
                manager.beginTransaction().replace(R.id.content_main, devList).commit();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }












    //Fonctions (en rapport avec le) bluetooth
    private BluetoothAdapter adaptateurBluetooth = BluetoothAdapter.getDefaultAdapter();
    private Set<BluetoothDevice> appareilsAppairés; //Initialisation de l'ensemble d'appareil bluetooth appareilsAppairés, qui contiendra tous les appareils bluetooth appairés
    BroadcastReceiver bReceiver = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(),"Vous devez aciver le bluetoothe pour pouvoir utiliser MyPatrick",Toast.LENGTH_LONG).show();
            btON = false;
        }
        btON = true;
    }

    boolean createBluetooth(){//Renvoie true si on peut utiliser le bluetooth, false sinon
        if(adaptateurBluetooth==null){//Si le téléphone n'a pas d'adaptateur bluetooth
            Toast.makeText(getApplicationContext(),"Your phone does not support bluetooth, therefore you won't be able to control your robot nor configure it via bluetooth",Toast.LENGTH_LONG).show();
            return false;
        } else {//Sinon
            if (!adaptateurBluetooth.isEnabled()){//Si le bluetooth n'est pas activé
                askTurnOn();//On demande" à l'utilisateur de le faire
                if (btON){
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

    }

    void getappareilsAppairésArray() {//Renvoie un ArrayAdapter contenant tous les appareils appairés
        appareilsAppairés = adaptateurBluetooth.getBondedDevices(); //On donne au set (ensemble) d'appareils bluetooth la valeur retournée par la fonction getBondedDevices --> appareils appairés
        //arrayAdapterAppareils.clear();
        //arrayAdapterAppareils.notifyDataSetChanged();
        if (appareilsAppairés.size() > 0) {//S'il y a au moins 1 appareil dans le set
            for(BluetoothDevice btdevice:appareilsAppairés) {
                arrayAdapterAppareils.add(btdevice.getName());//Ajoute le nom périphériques Bluetooth appairés à l'ArrayAdapter
            }
        }
    }

    void updateappareilsConnectés () {
        getappareilsAppairésArray();
        IntentFilter bluetoothDevFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);//Intent filter -> qd on détecte un appareil bluetooth
        IntentFilter discStarted = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//Intent filter -> qd on entre en mode "discovery"
        IntentFilter discFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//Intent filter -> qd on sort du mode "discovery"
        IntentFilter stateChg = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);//Intent filter -> qd on change d'état

        //appareilsConnectés.clear();//Pour éviter qu'un appariel ne se réaffiche s'il l'a déjà été
        //appareilsConnectés.notifyDataSetChanged();//Doit être appelée à chaque fois que l'on "clear"/nettoie un ArrayAdapter

        bReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();//action -> que s'est-il passé

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {//S'il s'est passé quelque chose en rapport avec un appareil bluetooth
                    BluetoothDevice bDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);//On définit bDevice comme étant l'appareil a/ lequel il s'est passé qqch

                    if (arrayAdapterAppareils != null && bDevice != null) {
                        String paired = "";//Chaîne de caractère qui sera vide si l'appareil détecté n'est pas appairé, et vaudra appairé (paired/emparejado) sinon
                        for (int i = 0; i < arrayAdapterAppareils.getCount(); i++) {//Pour chaque appareil appairé
                            if (bDevice.getName().equals(arrayAdapterAppareils.getItem(i))) {//Si le nom de l'appareil trouvé égale celui d'un appareil appairé
                                paired = " (" + chaineCaractèresAppareilsAppairés + ")";
                                break;//On sort de la boucle car il ne sert à rien de tester les autres appareils puisque l'on a déjà trouvé le bon
                            }
                        }
                        String contenuArrayAdapter = "";
                        for (int i = 0; i < appareilsConnectés.getCount();i++){
                            if (appareilsConnectés.getItem(i) != null) {
                                    contenuArrayAdapter += appareilsConnectés.getItem(i);
                            }
                        }
                        if (!contenuArrayAdapter.contains(bDevice.getAddress())){//Si l'appareil découvert n'est pas déjà dans la liste
                            appareilsConnectés.add(bDevice.getName() + paired + "\n" + bDevice.getAddress());//On ajoute l'appareil à l'arrayAdapter/aux appareils connectés
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),"No bluetooth device found", Toast.LENGTH_SHORT).show();
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {//Si l'état du bluetooth a changé
                    if (adaptateurBluetooth.getState() == BluetoothAdapter.STATE_OFF) {//Si le bluetooth est désactivé
                        Toast.makeText(getApplicationContext(), "Bluetooth is off. Please turn it on in order to use MyPatrick", Toast.LENGTH_SHORT).show();
                        askTurnOn();//On demande à l'utilisateur de l'activer
                    }
                }
            }
        };
        //Les 4 lignes (et en fait les IntentFilter) qui suivent servent à enregistrer des BroadcastReceiver qui ne font recevoir que les types d'infos précisées dans les filtres (d'intention) (IntentFilter)
        getApplicationContext().registerReceiver(bReceiver, bluetoothDevFound);
        getApplicationContext().registerReceiver(bReceiver, discStarted);
        getApplicationContext().registerReceiver(bReceiver, discFinished);
        getApplicationContext().registerReceiver(bReceiver, stateChg);
        startBTDevicesDiscovery();
    }

    public void askTurnOn(){
        Intent requestBTON = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);//On crée une intention (Intent) demandant à l'utilisateur d'activer le bluetooth
        startActivityForResult(requestBTON, 1);//On démarre l'activité et on attend son résultat
    }

    public void startBTDevicesDiscovery(){
        adaptateurBluetooth.cancelDiscovery();//On arrête la "découverte" d'appareils si elle est déjà en cours
        adaptateurBluetooth.startDiscovery();
    }

    //Fragments Methods

    public ArrayAdapter onDevListFragmentCreate(){//Quand un fragment DevicesList est créé
        //DevicesList devList = (DevicesList) getSupportFragmentManager().findFragmentById(R.id.content_main);//On définit le fragment devList (on a mis R.id.content_main en paramètre de findFragmentById car c'est le conteneur que l'on a utilisé lors de la "transaction" (cf. documentation officielle))
        if (createBluetooth()) {
            updateappareilsConnectés();
            return appareilsConnectés;
        } else {
            return null;
        }
    }

    public void onDevListFragmentPause(){
        try {
            unregisterReceiver(bReceiver);
        } catch (IllegalArgumentException exception) {
            //There's no BroadcastReceiver to unregister
        }
    }

    public void onDevListItemClicked(AdapterView<?> l, View v, int position, long id){
        if(adaptateurBluetooth.isDiscovering()){//Si l'adaptateur est en mode "discovery" (découverte)
            adaptateurBluetooth.cancelDiscovery();//On arrête le mode découverte
        }
        if (appareilsConnectés != null && appareilsAppairés != null && appareilsConnectés.getItem(position) != null) {
            if (appareilsConnectés.getItem(position).contains(chaineCaractèresAppareilsAppairés)) {
                Object[] o = appareilsAppairés.toArray();//Tableau d'objets
                for (int i = 0; i < o.length;i++) {
                    for (int j = 0; j < appareilsConnectés.getCount();j++) {
                        if (appareilsConnectés.getItem(j).contains(o[i].toString())) {
                            Log.d("UUIDs",o[i].toString());
                            appareilBTChoisi = adaptateurBluetooth.getRemoteDevice(o[i].toString());//On assigne à appareilBTChoisi la valeur de l'appareil bluetooth de o à la position correspond à celle envoyée par le fragment

                                threadBT = new ThreadBT(appareilBTChoisi);
                        }
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(),"This device is not paired with yours. You first need to pair your phone with your Patrick in order to control it via Bluletooth", Toast.LENGTH_LONG).show();
            }
        }
        Toast.makeText(getApplicationContext(),"Connected successfully",Toast.LENGTH_SHORT).show();
    }
    private class ThreadBT extends Thread {
        BluetoothSocket sockBT;
        OutputStream os;
        public ThreadBT(BluetoothDevice btdev) {
            try {
                sockBT = btdev.createRfcommSocketToServiceRecord(MY_UUID);
                sockBT.connect();
                os = sockBT.getOutputStream();
                os.write("TELEPHONEANDROIDVERSPATRICK".getBytes());
            } catch (IOException io) {Log.d("threadBT","ioexc");}
        }

        public void sendMessage(String message) {
            try {
                os.write(message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*@SuppressWarnings("EmptyCatchBlock")
    private class threadBT extends Thread {
        BluetoothSocket socketBT = null;

        public threadBT(BluetoothDevice btdev){
            try {
                socketBT = btdev.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException ioex){
                try {
                    socketBT = btdev.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                } catch (IOException ioexc) {
                }
            }
        }

        public void connect() {
            adaptateurBluetooth.cancelDiscovery();//On s'assure que la découverte d'appareils est bien arrêtée
            if (socketBT != null) {
                try {
                    socketBT.connect();
                } catch (IOException ioe) {
                    Log.d("threadBT", "could not connect");
                }
            }
        }

        public void sendMessage(String message) {
            if (socketBT != null){
                try {
                    OutputStream os = socketBT.getOutputStream();
                    os.write(message.getBytes());
                } catch (IOException ioe) {Log.d("threadBT","could not send message");}
            }
        }
    }*/








    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            adaptateurBluetooth.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            mHandler.obtainMessage(0,mmSocket).sendToTarget();//0 -> SUCCESS_CONNECT
        }

        public void sendMessage(String message){
            BTmessage = message;
            mHandler.obtainMessage(2,mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI activity
                mHandler.obtainMessage(1, bytes, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    //CAMERA AND CONTROL METHODS / MÉTHODES POUR LA PARTIE camAndControl


    @Override
    public void onCamAndControlCreate() {
        /*if (socketBT == null) {
            DevicesList devicesList = new DevicesList();
            manager.beginTransaction().replace(R.id.content_main, devicesList).commit();
        }*/
    }

    @Override
    public void onCamButtonClicked(String butName, boolean down){
        Log.d("MYPAT","Entered onCam");
        /*if(socketBT == null){
            try {
                socketBT = appareilBTChoisi.createRfcommSocketToServiceRecord(MY_UUID);
                socketBT.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        /*try {
            if (socketBT != null && os == null) {
                os = socketBT.getOutputStream();
            }
            try {*/
                if (down) {
                    switch (butName) {
                        case "robotUp":
                            /*try {
                                Log.d("Sent", "Before");
                                os.write("ANDROIDBT:AV-START".getBytes());
                                Log.d("Sent", "AV-START");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                            threadBT.sendMessage("ANDROIDBT:AV-START");
                            break;
                        case "robotDown":
                            /*
                            try {
                                os.write("ANDROIDBT:AR-START".getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                            threadBT.sendMessage("ANDROIDBT:AR-START");
                            break;
                        case "robotRotLeft":
                            /*try {
                                os.write("ANDROIDBT:RAG-START".getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                            threadBT.sendMessage("ANDROIDBT:RAG-START");
                            break;
                        case "robotRotRight":
                            /*try {
                                os.write("ANDROIDBT:RCA-START".getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                            threadBT.sendMessage("ANDROIDBT:RCA-START");
                            break;
                    }
                } else {
                    switch (butName) {
                        case "robotUp":
                            /*try {
                                os.write("ANDROIDBT:AV-STOP".getBytes());
                                Log.d("Sent", "AV-STOP");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                            threadBT.sendMessage("ANDROIDBT:AV-STOP");
                            break;
                        case "robotDown":
                            /*try {
                                os.write("ANDROIDBT:AR-STOP".getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                            threadBT.sendMessage("ANDROIDBT:AR-STOP");
                            break;
                        case "robotRotLeft":
                            /*try {
                                os.write("ANDROIDBT:RAG-STOP".getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                            threadBT.sendMessage("ANDROIDBT:RAG-STOP");
                            break;
                        case "robotRotRight":
                            /*try {
                                os.write("ANDROIDBT:RCA-STOP".getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                            threadBT.sendMessage("ANDROIDBT:RCA-STOP");
                            break;
                    }
                }
            /*} catch (NullPointerException npe) {
                Log.d("NPE", "NullPointer");
            }
        } catch (IOException ioexcep) {
            ioexcep.printStackTrace();
        }*/
    }
}