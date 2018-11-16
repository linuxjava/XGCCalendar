package com.calendar.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import xgc.free.calendar.DatePickerController;
import xgc.free.calendar.DatePickerView;
import xgc.free.calendar.SimpleMonthAdapter;


public class MainActivity extends AppCompatActivity implements DatePickerController {
    private DatePickerView dayPickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dayPickerView = findViewById(R.id.pickerView);
        //dayPickerView.setSelectedDate(2018, 11, 10);
        dayPickerView.setController(this);
    }

    @Override
    public int preMonths() {
        return 0;
    }

    @Override
    public int afterMonths() {
        return 6;
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day, int weekIndex) {
        String weekStr = "";
        switch (weekIndex) {
            case 1:
                weekStr = "星期日";
                break;
            case 2:
                weekStr = "星期一";
                break;
            case 3:
                weekStr = "星期二";
                break;
            case 4:
                weekStr = "星期三";
                break;
            case 5:
                weekStr = "星期四";
                break;
            case 6:
                weekStr = "星期五";
                break;
            case 7:
                weekStr = "星期六";
                break;
        }

        Log.e("xiao1", day + " / " + month + " / " + year + " / " + weekStr);
    }

    @Override
    public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays) {
        Log.e("xiao1", selectedDays.getFirst().toString() + " --> " + selectedDays.getLast().toString());
    }
}
