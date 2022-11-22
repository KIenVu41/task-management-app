package com.kma.taskmanagement.ui.main;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import com.kma.taskmanagement.R;
import com.kma.taskmanagement.broadcastReceiver.NetworkChangeReceiver;
import com.kma.taskmanagement.data.local.DatabaseHelper;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.data.remote.request.InviteRequest;
import com.kma.taskmanagement.data.repository.GroupRepository;
import com.kma.taskmanagement.data.repository.impl.GroupRepositoryImpl;
import com.kma.taskmanagement.listener.NetworkReceiverCallback;
import com.kma.taskmanagement.ui.intro.IntroActivity;
import com.kma.taskmanagement.ui.main.fragments.ChartFragment;
import com.kma.taskmanagement.ui.main.fragments.GroupTaskFragment;
import com.kma.taskmanagement.ui.main.fragments.ManageGroupTaskFragment;
import com.kma.taskmanagement.ui.main.fragments.PersonTaskFragment;
import com.kma.taskmanagement.ui.main.fragments.SettingFragment;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.DateUtils;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;
import com.kma.taskmanagement.utils.Utils;
import com.shrikanthravi.customnavigationdrawer2.data.MenuItem;
import com.shrikanthravi.customnavigationdrawer2.widget.SNavigationDrawer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import tech.gusavila92.websocketclient.WebSocketClient;

public class MainActivity extends AppCompatActivity {

//    @BindView(R.id.tab_layout)
//    public TabLayout tabLayout;
//    @BindView(R.id.view_pager2)
//    public ViewPager2 viewPager2;
//    public TaskPagerAdapter adapter;
    private GroupViewModel groupViewModel;
    private GroupRepository groupRepository = new GroupRepositoryImpl();
    private String token = "";
    private BroadcastReceiver mNetworkReceiver;
    private SNavigationDrawer sNavigationDrawer;
    private DatabaseHelper db;
    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver noNWBroadcastReceiver;
    private WebSocketClient webSocketClient;
    int color1=0;
    Class fragmentClass;
    public static Fragment fragment;

    @Override
    protected void onStart() {
        super.onStart();
        //mNetworkReceiver = new NetworkChangeReceiver();
        //registerNetworkBroadcastForNougat();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ButterKnife.bind(this);
//        String[] labels = getResources().getStringArray(R.array.mainTab);
//        adapter = new TaskPagerAdapter(this);
//        viewPager2.setAdapter(adapter);
//
//        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
//            tab.setText(labels[position]);
//        }).attach();
//
//        viewPager2.setCurrentItem(0, false);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        sNavigationDrawer = findViewById(R.id.navigationDrawer);
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(getResources().getString(R.string.personal),R.drawable.one));
        menuItems.add(new MenuItem(getResources().getString(R.string.group),R.drawable.two));
        menuItems.add(new MenuItem(getResources().getString(R.string.chart),R.drawable.three));
        menuItems.add(new MenuItem(getResources().getString(R.string.settingtab),R.drawable.one));
        sNavigationDrawer.setMenuItemList(menuItems);
        sNavigationDrawer.setAppbarTitleTextColor(getResources().getColor(R.color.COLOR_BUTTON));
        sNavigationDrawer.setMenuiconTintColor(getResources().getColor(R.color.COLOR_BUTTON));
        fragmentClass =  PersonTaskFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, fragment).commit();
        }

        groupViewModel =  new ViewModelProvider(this, new GroupViewModelFactory(groupRepository)).get(GroupViewModel.class);
        token = SharedPreferencesUtil.getInstance(getApplicationContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);

        try {
            groupViewModel.getInvites(Constants.BEARER + token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        groupViewModel.getInviteResponse().observeForever(new Observer<List<InviteRequest>>() {
            @Override
            public void onChanged(List<InviteRequest> inviteRequests) {
                if (inviteRequests.size() != 0) {
                    for(InviteRequest ir: inviteRequests) {
                        openDialog(ir);
                    }
                }
            }
        });
        sNavigationDrawer.setOnMenuItemClickListener(new SNavigationDrawer.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClicked(int position) {
                switch (position){
                    case 0:{
                        color1 = R.color.red;
                        fragmentClass = PersonTaskFragment.class;
                        break;
                    }
                    case 1:{
                        color1 = R.color.orange;
                        //fragmentClass = GroupTaskFragment.class;
                        fragmentClass = ManageGroupTaskFragment.class;
                        break;
                    }
                    case 2:{
                        color1 = R.color.green;
                        fragmentClass = ChartFragment.class;
                        break;
                    }
                    case 3:{
                        color1 = R.color.COLOR_BUTTON;
                        fragmentClass = SettingFragment.class;
                        break;
                    }
                }
                sNavigationDrawer.setDrawerListener(new SNavigationDrawer.DrawerListener() {

                    @Override
                    public void onDrawerOpened() {

                    }

                    @Override
                    public void onDrawerOpening(){

                    }

                    @Override
                    public void onDrawerClosing(){
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (fragment != null) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, fragment).commit();

                        }
                    }

                    @Override
                    public void onDrawerClosed() {

                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        System.out.println("State "+newState);
                    }
                });
            }
        });
    }

    private void openDialog(InviteRequest ir) {
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                this);

        alertDialog2.setTitle(getResources().getString(R.string.invite));

        alertDialog2.setMessage(getResources().getString(R.string.invitetitle));
        alertDialog2.setIcon(R.drawable.ic_baseline_group_24);
        alertDialog2.setCancelable(true);

        ir.setInvitation_id(ir.getId());
        alertDialog2.setPositiveButton(getResources().getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ir.setIs_accept(true);
                        groupViewModel.join(Constants.BEARER + token, ir);
                    }
                });
        alertDialog2.setNegativeButton(getResources().getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        ir.setIs_accept(false);
                        groupViewModel.join(Constants.BEARER + token, ir);
                        dialog.cancel();
                    }
                });

        alertDialog2.show();

        createWebSocketClient();
    }

    private void createWebSocketClient() {
        URI uri;
        try {
            // Connect to local host
            uri = new URI("ws://10.0.2.2:8080/websocket");
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WebSocket", "Session is starting");
                webSocketClient.send("Hello World!");
            }

            @Override
            public void onTextReceived(String s) {
                Log.i("WebSocket", "Message received");
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{

                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onBinaryReceived(byte[] data) {
            }

            @Override
            public void onPingReceived(byte[] data) {
            }

            @Override
            public void onPongReceived(byte[] data) {
            }

            @Override
            public void onException(Exception e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
                System.out.println("onCloseReceived");
            }
        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }

    private void registerNetworkBroadcastForNougat() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(mNetworkReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // unregisterReceiver(mNetworkReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //registerNetworkBroadcastForNougat();
    }
}