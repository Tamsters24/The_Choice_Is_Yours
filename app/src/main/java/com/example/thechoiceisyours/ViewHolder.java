package com.example.thechoiceisyours;

import android.view.View;
import android.widget.TextView;

public class ViewHolder {
    TextView textView;
    ViewHolder(View view) {
        textView=view.findViewById(R.id.textView);
    }
}
