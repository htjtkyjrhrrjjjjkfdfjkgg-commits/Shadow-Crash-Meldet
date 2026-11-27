package com.shadow.crashmeldet;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class MainActivity extends Activity {
    private final ArrayList<Double> values = new ArrayList<>();
    private LinearLayout list;
    private TextView stats;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(28, 36, 28, 24);
        page.setBackgroundColor(Color.rgb(8, 12, 11));

        TextView title = new TextView(this);
        title.setText("SHADOW CRASH MELDET");
        title.setTextColor(Color.rgb(57, 255, 130));
        title.setTextSize(23);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 8, 0, 22);
        page.addView(title);

        TextView note = new TextView(this);
        note.setText("سجّل النتائج السابقة واعرض إحصاءاتها فقط. لا يمكن معرفة المضاعف القادم أو ضمان الربح.");
        note.setTextColor(Color.LTGRAY);
        note.setTextSize(14);
        note.setGravity(Gravity.CENTER);
        page.addView(note);

        EditText entry = new EditText(this);
        entry.setSingleLine(true);
        entry.setHint("أدخل نتيجة الجولة مثل 2.35");
        entry.setHintTextColor(Color.GRAY);
        entry.setTextColor(Color.WHITE);
        entry.setInputType(8194);
        page.addView(entry);

        Button add = new Button(this);
        add.setText("إضافة النتيجة");
        page.addView(add);

        stats = new TextView(this);
        stats.setTextColor(Color.rgb(57,255,130));
        stats.setTextSize(16);
        stats.setPadding(0, 24, 0, 16);
        page.addView(stats);

        TextView historyTitle = new TextView(this);
        historyTitle.setText("آخر النتائج");
        historyTitle.setTextColor(Color.WHITE);
        historyTitle.setTextSize(18);
        page.addView(historyTitle);

        ScrollView scroll = new ScrollView(this);
        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(list);
        page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        add.setOnClickListener(v -> {
            try {
                double x = Double.parseDouble(entry.getText().toString().trim());
                if (!Double.isFinite(x) || x < 1.0) throw new NumberFormatException();
                values.add(x);
                entry.setText("");
                refresh();
            } catch (Exception e) {
                Toast.makeText(this, "أدخل رقماً صحيحاً أكبر من أو يساوي 1.00", Toast.LENGTH_SHORT).show();
            }
        });
        refresh();
        setContentView(page);
    }

    private void refresh() {
        if (values.isEmpty()) {
            stats.setText("عدد الجولات: 0\nأضف نتائجك لعرض الإحصاءات.");
        } else {
            double sum = 0, min = Double.MAX_VALUE, max = 0;
            int c2=0, c5=0, c10=0;
            for (double x : values) {
                sum += x; min = Math.min(min,x); max = Math.max(max,x);
                if (x >= 2) c2++; if (x >= 5) c5++; if (x >= 10) c10++;
            }
            ArrayList<Double> sorted = new ArrayList<>(values);
            Collections.sort(sorted);
            double median = sorted.size()%2==1 ? sorted.get(sorted.size()/2) :
                (sorted.get(sorted.size()/2-1)+sorted.get(sorted.size()/2))/2.0;
            stats.setText(String.format(Locale.US,
                "عدد الجولات: %d\nالمتوسط: %.2fx | الوسيط: %.2fx\nأقل نتيجة: %.2fx | أعلى نتيجة: %.2fx\nنسبة 2x+: %.1f%% | 5x+: %.1f%% | 10x+: %.1f%%",
                values.size(), sum/values.size(), median, min, max,
                100.0*c2/values.size(), 100.0*c5/values.size(), 100.0*c10/values.size()));
        }
        list.removeAllViews();
        int start = Math.max(0, values.size()-30);
        for (int i=values.size()-1; i>=start; i--) {
            TextView row = new TextView(this);
            row.setText(String.format(Locale.US, "%d.  %.2fx", i+1, values.get(i)));
            row.setTextColor(Color.WHITE);
            row.setTextSize(16);
            row.setPadding(4, 10, 4, 10);
            list.addView(row);
        }
    }
}
