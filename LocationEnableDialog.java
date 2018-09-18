package com.demoapp.com.demoapp.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.demoapp.R;
import com.demoapp.com.demoapp.Utils.Utils;

/**
 * Created by user 2 on 4/7/2017.
 */

public class LocationEnableDialog extends Dialog {
    TextView tv_lochead, tv_loctext, tv_enable;
    Context ctx;

    public LocationEnableDialog(Context context) {
        super(context);
        this.ctx = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.locationenablelayout);

        tv_lochead = (TextView) findViewById(R.id.tv_lochead);
        tv_loctext = (TextView) findViewById(R.id.tv_loctext);
        tv_enable = (TextView) findViewById(R.id.tv_enable);

        //Typeface
        tv_lochead.setTypeface(Utils.mTypeface(ctx, 1));
        tv_loctext.setTypeface(Utils.mTypeface(ctx, 1));
        tv_enable.setTypeface(Utils.mTypeface(ctx, 1));

        tv_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                ctx.startActivity(myIntent);
                dismiss();
            }
        });
    }
}
