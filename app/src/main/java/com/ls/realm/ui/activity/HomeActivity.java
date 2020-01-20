package com.ls.realm.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.ls.realm.R;
import com.ls.realm.model.db.RealmManager;
import com.ls.realm.model.db.data.User;
import com.ls.realm.model.db.utils.DataGenerator;
import com.ls.realm.ui.adapter.RealmAdapter;

import java.util.Calendar;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class HomeActivity extends AppCompatActivity {
    Button btnAdd, btnDeleteAll, btnGetAll, btnGenerateData;
    private RealmAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_home);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnDeleteAll = (Button) findViewById(R.id.btnDeleteAll);
        btnGetAll = (Button) findViewById(R.id.btnGetAll);
        btnGenerateData = (Button) findViewById(R.id.btnGenerateData);
        RealmManager.open();
        initViews();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user =DataGenerator.generateUser();
                user.setAge(Calendar.getInstance().getTimeInMillis());
                RealmManager.createUserDao().save(user);

            }
        });
        btnGetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadUserListAsync();
            }
        });
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RealmManager.clear();
            }
        });
        btnGenerateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserList();
            }
        });

    }

    @Override
    protected void onDestroy() {
        RealmManager.close();
        super.onDestroy();
    }

    private void initViews() {
        mAdapter = new RealmAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void saveUserList() {
        RealmManager.createUserDao().save(DataGenerator.generateUserList());
    }

    private void loadUserListAsync() {
        final RealmResults<User> dataList = RealmManager.createUserDao().loadAllAsync();
        dataList.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                updateRecyclerView(dataList);
            }
        });
    }

    private void updateRecyclerView(List<User> userList) {
        mAdapter.setData(userList);
        mAdapter.notifyDataSetChanged();

    }
}
