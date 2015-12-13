package com.yrh.androidfirewall.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Switch;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.yrh.androidfirewall.R;
import com.yrh.androidfirewall.adapter.PagerViewAdapter;
import com.yrh.androidfirewall.adapter.PhoneInfoAdapter;
import com.yrh.androidfirewall.model.PhoneInfo;
import com.yrh.androidfirewall.utils.DBOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Constant
    public static final int PAGE_INCOMMINGBLACKLIST = 0;
    public static final int PAGE_SMSBLACKLIST = 1;
    public static final int PAGE_SETTINGS = 2;

    private LayoutInflater mLayoutInflater;
    private List<String> mTitleList = new ArrayList<>();
    private List<View> mViewList = new ArrayList<>();
    private ViewPager mViewPager;
    private FloatingActionButton fab;
    private DBOpenHelper mDbOpenHelper;
    private List<PhoneInfo> mPhoneInfoList = new ArrayList<>();

    // incommingblacklist 组件
    private PhoneInfoAdapter mIncommingBlackListPhoneInfoAdapter;

    // SMSblacklist 组件
    private PhoneInfoAdapter mSMSBlackListPhoneInfoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initDb();

        initView();

    }

    /**
     * 初始化数据库
     */
    private void initDb() {
        mDbOpenHelper = new DBOpenHelper(this, "db", null, DBOpenHelper.DB_VERSION);

        mPhoneInfoList = mDbOpenHelper.getAllPhoneInfo();
    }


    /**
     * 初始化控件
     */
    private void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        mLayoutInflater = LayoutInflater.from(this);
        View incomingTelegramBlackListView = mLayoutInflater.inflate(R.layout.view_incoming_telegram_black_list, null);
        View SMSBlackListView = mLayoutInflater.inflate(R.layout.view_sms_black_list, null);
        View settingsView = mLayoutInflater.inflate(R.layout.view_settings, null);

        // 添加页卡视图
        mViewList.add(incomingTelegramBlackListView);
        mViewList.add(SMSBlackListView);
        mViewList.add(settingsView);

        // 添加页卡标题
        mTitleList.add("来电黑名单");
        mTitleList.add("短信黑名单");
        mTitleList.add("设置");

        //添加tab选项卡
        for (String title : mTitleList) {
            mTabLayout.addTab(mTabLayout.newTab().setText(title));
        }

        //添加tab选项卡
        PagerViewAdapter pagerViewAdapter = new PagerViewAdapter(mViewList, mTitleList);
        mViewPager.setAdapter(pagerViewAdapter);                  //给 ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);              //将 TabLayout 和 ViewPager关联起来
        mTabLayout.setTabsFromPagerAdapter(pagerViewAdapter);     //给 Tabs设置适配器

        // 初始化来电黑名单 RecyclerView
        initRecyclerView(incomingTelegramBlackListView, R.id.recyclerViewIncommingBlackList, mIncommingBlackListPhoneInfoAdapter);

        // 初始化短信黑名单 RecyclerView
        initRecyclerView(SMSBlackListView, R.id.recyclerViewSMSBlackList, mSMSBlackListPhoneInfoAdapter);
    }

    /**
     * 初始化来电黑名单 RecyclerView
     *
     * @param view
     */
    private void initRecyclerView(View view, int resourceId, PhoneInfoAdapter adapter) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(resourceId);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // 设置 RecyclerView 的显示方式
        recyclerView.setLayoutManager(linearLayoutManager);

        // 设置 Adapter
        if (resourceId == R.id.recyclerViewIncommingBlackList) {
            adapter = new PhoneInfoAdapter(this, getIncommingBlackList());
        } else if (resourceId == R.id.recyclerViewSMSBlackList) {
            adapter = new PhoneInfoAdapter(this, getSMSBlackList());
        }

        recyclerView.setAdapter(adapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab: {
                if (mViewPager.getCurrentItem() == PAGE_INCOMMINGBLACKLIST) {
                    getIncommingBlackListDialog().show();
                } else if (mViewPager.getCurrentItem() == PAGE_SMSBLACKLIST) {
                    getSMSBlackListDialog().show();
                }
                break;
            }
        }
    }

    /**
     * 得到来电黑名单 FloatActionButton 对话框
     * @return
     */
    private DialogPlus getIncommingBlackListDialog() {
        List<HashMap<String, String>> optionList = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();

        map.put("Add", "添加号码至来电黑名单");
        optionList.add(map);

        String[] from = {"Add"};
        int[] to = {R.id.tvDialogAdd};

        SimpleAdapter adapter = new SimpleAdapter(this, optionList, R.layout.view_dialog_item, from, to);

        View headerView = mLayoutInflater.inflate(R.layout.view_dialog_header, null);
        View footerView = mLayoutInflater.inflate(R.layout.view_dialog_footer, null);

        final DialogPlus dialogPlus = DialogPlus.newDialog(this).setAdapter(adapter)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .setFooter(footerView)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {

                    }
                }).create();

        Button dialogFooter = (Button) footerView.findViewById(R.id.btnDialogFooter);
        dialogFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogPlus.isShowing()) {
                    dialogPlus.dismiss();
                }
            }
        });

        return dialogPlus;
    }

    /**
     * 得到来电黑名单 FloatActionButton 对话框
     * @return
     */
    private DialogPlus getSMSBlackListDialog() {
        List<HashMap<String, String>> optionList = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        map.put("Add", "添加号码至短信黑名单");
        optionList.add(map);

        map = new HashMap<>();
        map.put("Add", "管理敏感词");
        optionList.add(map);

        String[] from = {"Add"};
        int[] to = {R.id.tvDialogAdd};

        SimpleAdapter adapter = new SimpleAdapter(this, optionList, R.layout.view_dialog_item, from, to);

        View headerView = mLayoutInflater.inflate(R.layout.view_dialog_header, null);
        View footerView = mLayoutInflater.inflate(R.layout.view_dialog_footer, null);

        final DialogPlus dialogPlus = DialogPlus.newDialog(this).setAdapter(adapter)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .setFooter(footerView)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {


                    }
                }).create();

        Button dialogFooter = (Button) footerView.findViewById(R.id.btnDialogFooter);
        dialogFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogPlus.isShowing()) {
                    dialogPlus.dismiss();
                }
            }
        });

        return dialogPlus;
    }

    public List<PhoneInfo> getIncommingBlackList() {
        List<PhoneInfo> result = new ArrayList<>();
        for (PhoneInfo phoneInfo : mPhoneInfoList) {
            if (phoneInfo.getInIncommingBlack() == 1) {
                result.add(phoneInfo);
            }
        }
        return result;
    }

    public List<PhoneInfo> getSMSBlackList() {
        List<PhoneInfo> result = new ArrayList<>();
        for (PhoneInfo phoneInfo : mPhoneInfoList) {
            if (phoneInfo.getInSMSBlack() == 1) {
                result.add(phoneInfo);
            }
        }
        return result;
    }
}
