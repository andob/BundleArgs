package com.michaelflisar.bundlebuilder.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class FirstActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Button button = new Button(this);
        button.setText("Start second activity");
        setContentView(button);

        button.setOnClickListener(v ->
            new SecondActivityBundleBuilder()
                .data(new Data())
                .startActivity(v.getContext()));
    }
}
