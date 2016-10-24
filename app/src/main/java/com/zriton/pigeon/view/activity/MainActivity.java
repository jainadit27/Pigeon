package com.zriton.pigeon.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zriton.pigeon.R;
import com.zriton.pigeon.view.fragment.MessageFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @BindView(R.id.toolbar)
    Toolbar mToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);
        setUpToolbar();
        initFragment();
    }

    /**
     * Add toolbar to layout
     */
    private void setUpToolbar()
    {
        setSupportActionBar(mToolbar);
    }

    /**
     * Add message fragment
     */
    private void initFragment()
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, MessageFragment.newInstance()).commit();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if(!getSupportFragmentManager().popBackStackImmediate())
                finish();
        }
        return super.onOptionsItemSelected(item);

    }


}
