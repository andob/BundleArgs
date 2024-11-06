package com.michaelflisar.bundlebuilder.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import com.michaelflisar.bundlebuilder.Arg;
import com.michaelflisar.bundlebuilder.BundleArgs;
import com.michaelflisar.bundlebuilder.BundleBuilder;

@BundleBuilder
public class SecondActivity extends Activity
{
    @Arg
    public Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        BundleArgs.bind(this, getIntent().getExtras());

        Button button = new Button(this);
        button.setText(data.toString());
        setContentView(button);

        button.setOnClickListener(v -> finish());
    }
}
