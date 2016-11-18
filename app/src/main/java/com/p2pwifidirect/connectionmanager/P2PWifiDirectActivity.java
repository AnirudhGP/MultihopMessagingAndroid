package com.p2pwifidirect.connectionmanager;


import com.p2pwifidirect.connectionmanager.ReceiveMessageTest;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ListFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TabHost.TabSpec;


public class P2PWifiDirectActivity extends Activity{
	
	P2PConnectionManager c;
	P2PRoutingManager rm;
	P2PFileDiscoveryManager fm;
	Context context;
	WifiP2pManager wfp2pmgr;
	ListView peerlist,msglist,reqlist;
	TextView console1, console2, console4;
	
	IntentFilter intntfltr;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setupTabs();
    	context = getApplicationContext();
    	wfp2pmgr = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
    	
    	//test
    	//WifiManager wfmgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	//WifiLock wfl = wfmgr.createWifiLock(WifiManager.WIFI_MODE_FULL, "wifilock");
    	//wfl.acquire();
    	//PowerManager pmgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	//WakeLock wl = pmgr.newWakeLock(PowerManager.FULL_WAKE_LOCK, "wakelock");
    	//wl.acquire();

    	
    	console1 = (TextView)findViewById(R.id.consoleOutput1);
    	console1.setMovementMethod(new ScrollingMovementMethod());
    	console2 = (TextView)findViewById(R.id.consoleOutput2);
    	console2.setMovementMethod(new ScrollingMovementMethod());
    	console4 = (TextView)findViewById(R.id.result);    //Jay
    	console4.setMovementMethod(new ScrollingMovementMethod());
    	console1.append("Connection Manager Started.\n");
    	console2.append("Routing Manager Started.\n");
    	console4.append("File Manager Started.\n");

    	c = new P2PConnectionManager(context,getMainLooper(),wfp2pmgr,console1);
    	rm = new P2PRoutingManager(context,c,console2);
    	fm = new P2PFileDiscoveryManager(context,console4,console4);
    	
    	//setup the scan alarm
    	/*AlarmManager alrmmgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    	Intent i1=new Intent("ScanAlarm");
    	PendingIntent pi1=PendingIntent.getBroadcast(context, 0, i1, 0);
        alrmmgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5, pi1); // Millisec * Second * Minute

    	Intent i2=new Intent("msgCacheAlarm");
    	PendingIntent pi2=PendingIntent.getBroadcast(context, 0, i2, 0);
        alrmmgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 10, pi2); // Millisec * Second * Minute
        
        //this is our intenent filter where we register to receive android wifi p2p intents
        //connection intents and the scan alarm
		intntfltr = new IntentFilter();
		intntfltr.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intntfltr.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intntfltr.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intntfltr.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		intntfltr.addAction("ScanAlarm");
		intntfltr.addAction("RestartServer");
		registerReceiver(c, intntfltr);*/
    	
    	
    	peerlist = (ListView)findViewById(R.id.peer_list);
    	peerlist.setAdapter(c.adapter);
    	peerlist.setOnItemClickListener( new OnItemClickListener() {
    		@Override
    	    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    			c.tryConnection(arg2);
    	    }
    	});

    	msglist = (ListView)findViewById(R.id.message_list);
    	msglist.setAdapter(rm.adapter);
    	msglist.setOnItemClickListener( new OnItemClickListener() {
    		@Override
    	    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    			//should we allow user to remove message here?
    	    }
    	});
    	
    	reqlist = (ListView)findViewById(R.id.request_list);   
    	reqlist.setAdapter(fm.adapter);
    	reqlist.setOnItemClickListener( new OnItemClickListener() {
    		@Override
    	    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    			final int selection = arg2;
                fm.sendResponse(selection);

    			/*AlertDialog.Builder builder = new AlertDialog.Builder(context);
    			builder.setMessage("Are you sure you want to share this file?")
    			       .setCancelable(false)
    			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    			           public void onClick(DialogInterface dialog, int id) {
    			                fm.sendResponse(selection);
    			           }
    			       })
    			       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    			           public void onClick(DialogInterface dialog, int id) {
    			                dialog.cancel();
    			           }
    			       });*/
    		}
    	});

    }
    
    public void onPause(){
    	super.onPause();
    }
    
    public void onResume(){
    	super.onResume();
    }
    
    public void startScan(View v){
    	if(((Button)findViewById(R.id.scanbutton)).getText().equals("Start Scanning")){
    		c.startDiscovery();
    		((Button)findViewById(R.id.scanbutton)).setText("Stop Scanning");
    	}else{
    		c.stopDiscovery();
    		((Button)findViewById(R.id.scanbutton)).setText("Start Scanning");
    	}
    }
    
    public void closeConnections(View v){
    	c.closeConnections();
    }
    
    public void setupTabs() {       
    	TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        TabSpec spec1=tabHost.newTabSpec("Tab 1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Connection Manager");

        TabSpec spec2=tabHost.newTabSpec("Tab 2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Routing Manager");

        TabSpec spec3=tabHost.newTabSpec("Tab 3");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("File Manager");

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);
    }
    
    public void startSearch(View v) {  
    	String searchfile = ((EditText)findViewById(R.id.requestfile)).getText().toString();
    	fm.searchForFile(searchfile);
  	}

    
}