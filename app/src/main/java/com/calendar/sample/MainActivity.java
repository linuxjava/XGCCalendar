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
        dayPickerView.setController(this);

        //dayPickerView.scrollToPosition(5);
    }

    @Override
    public int preMonths() {
        return 1;
    }

    @Override
    public int afterMonths() {
        return 6;
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
        Log.e("xiao1", day + " / " + month + " / " + year);
    }

    @Override
    public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays) {

        Log.e("xiao1", selectedDays.getFirst().toString() + " --> " + selectedDays.getLast().toString());
    }
}
