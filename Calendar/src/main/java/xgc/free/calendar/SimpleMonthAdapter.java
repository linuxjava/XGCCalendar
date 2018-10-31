/***********************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2014 Robin Chutaux
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ***********************************************************************************/
package xgc.free.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SimpleMonthAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SimpleMonthView.OnDayClickListener {
    protected static final int MONTHS_IN_YEAR = 12;
    public static final int ITEM_TYPE_HEADER_WEEK = 0;
    public static final int ITEM_TYPE_MONTH = 1;
    private final TypedArray typedArray;
    private final Context mContext;
    private DatePickerController controller;
    private final Calendar calendar;
    private final SelectedDays<CalendarDay> selectedDays;
    private int startYear, startMonth;//开始年月年月，注startMonth释从0开始的
    private int selectWay = 1;//默认多选
    private int defaultSelectedYear = -1;
    private int defaultSelectedMonth = -1;
    private int defaultSelectedDay = -1;
    private int weekHeight;
    private int weekBgColor;
    private int weekTextColor;
    private int weekTextSize;

    public SimpleMonthAdapter(Context context, TypedArray typedArray) {
        this.typedArray = typedArray;
        calendar = Calendar.getInstance();
        selectedDays = new SelectedDays<>();
        mContext = context;

        Resources resources = context.getResources();
        weekHeight = typedArray.getDimensionPixelSize(R.styleable.DatePickerView_topWeekHeight, resources.getDimensionPixelSize(R.dimen.week_height));
        weekTextSize = typedArray.getDimensionPixelSize(R.styleable.DatePickerView_topWeekTextSize, resources.getDimensionPixelSize(R.dimen.week_text_size));
        weekBgColor = typedArray.getColor(R.styleable.DatePickerView_topWeekBgColor, resources.getColor(R.color.week_bg_color));
        weekTextColor = typedArray.getColor(R.styleable.DatePickerView_topWeekTextColor, resources.getColor(R.color.week_text_color));
    }

    /**
     * 单选时设置默认选择日期
     *
     * @param year
     * @param month
     * @param day
     */
    public void setSelectedDate(int year, int month, int day) {
        defaultSelectedYear = year;
        defaultSelectedMonth = month;
        defaultSelectedDay = day;
    }

    public void setController(DatePickerController controller) {
        this.controller = controller;
        init();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_HEADER_WEEK;
        }

        return ITEM_TYPE_MONTH;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == ITEM_TYPE_HEADER_WEEK) {
            View headerView = LayoutInflater.from(mContext).inflate(R.layout.item_header_week, viewGroup, false);
            return new HeaderViewHolder(headerView);
        } else {
            final SimpleMonthView simpleMonthView = new SimpleMonthView(mContext, typedArray);
            return new MonthViewHolder(simpleMonthView, this);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) viewHolder).init();
            return;
        }

        final SimpleMonthView v = ((MonthViewHolder) viewHolder).simpleMonthView;
        final HashMap<String, Integer> drawingParams = new HashMap<String, Integer>();
        int month;
        int year;

        month = (startMonth + position - 1) % MONTHS_IN_YEAR;
        year = startYear + (position - 1 + startMonth) / MONTHS_IN_YEAR;

        int selectedFirstDay = -1;
        int selectedLastDay = -1;
        int selectedFirstMonth = -1;
        int selectedLastMonth = -1;
        int selectedFirstYear = -1;
        int selectedLastYear = -1;

        if (selectedDays.getFirst() != null) {
            selectedFirstDay = selectedDays.getFirst().day;
            selectedFirstMonth = selectedDays.getFirst().month;
            selectedFirstYear = selectedDays.getFirst().year;
        }

        if (selectedDays.getLast() != null) {
            selectedLastDay = selectedDays.getLast().day;
            selectedLastMonth = selectedDays.getLast().month;
            selectedLastYear = selectedDays.getLast().year;
        }

        //v.reuse();//貌似没有作用

        drawingParams.put(SimpleMonthView.VIEW_PARAMS_SELECTED_BEGIN_YEAR, selectedFirstYear);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_SELECTED_LAST_YEAR, selectedLastYear);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_SELECTED_BEGIN_MONTH, selectedFirstMonth);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_SELECTED_LAST_MONTH, selectedLastMonth);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_SELECTED_BEGIN_DAY, selectedFirstDay);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_SELECTED_LAST_DAY, selectedLastDay);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_YEAR, year);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_MONTH, month);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_WEEK_START, calendar.getFirstDayOfWeek());
        v.setMonthParams(drawingParams);
        v.invalidate();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        int itemCount = 1;//1表示最顶部的header
        itemCount += controller.preMonths() > 0 ? controller.preMonths() : 0;
        itemCount += controller.afterMonths() > 0 ? controller.afterMonths() : 0;

        return itemCount;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout mWeekHeaderView;

        HeaderViewHolder(View itemView) {
            super(itemView);
            mWeekHeaderView = (LinearLayout) itemView;
        }

        public void init() {
            mWeekHeaderView.getLayoutParams().height = weekHeight;
            mWeekHeaderView.setBackgroundColor(weekBgColor);
            int count = mWeekHeaderView.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = mWeekHeaderView.getChildAt(i);
                if (child instanceof TextView) {
                    ((TextView) child).setTextColor(weekTextColor);
                    ((TextView) child).setTextSize(TypedValue.COMPLEX_UNIT_PX, weekTextSize);
                }
            }
        }
    }

    public class MonthViewHolder extends RecyclerView.ViewHolder {
        final SimpleMonthView simpleMonthView;

        public MonthViewHolder(View itemView, SimpleMonthView.OnDayClickListener onDayClickListener) {
            super(itemView);
            simpleMonthView = (SimpleMonthView) itemView;
            simpleMonthView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            simpleMonthView.setClickable(true);
            simpleMonthView.setOnDayClickListener(onDayClickListener);
        }
    }

    protected void init() {
        //初始化最早的年月
        startYear = calendar.get(Calendar.YEAR);
        startMonth = calendar.get(Calendar.MONTH);
        for (int i = 0; i < controller.preMonths(); i++) {
            --startMonth;
            if (startMonth < 0) {
                --startYear;
                startMonth = 11;
            }
        }

        selectWay = typedArray.getInt(R.styleable.DatePickerView_selectWay, 1);

        if (selectWay == 0) {
            if (defaultSelectedYear != -1 && defaultSelectedMonth != -1 && defaultSelectedDay != -1) {
                onDayTapped(new CalendarDay(defaultSelectedYear, defaultSelectedMonth, defaultSelectedDay));
            } else if (typedArray.getBoolean(R.styleable.DatePickerView_currentDaySelected, false)) {
                onDayTapped(new CalendarDay(System.currentTimeMillis()));
            }
        }
    }

    @Override
    public void onDayClick(SimpleMonthView simpleMonthView, CalendarDay calendarDay) {
        if (calendarDay != null) {
            onDayTapped(calendarDay);
        }
    }

    protected void onDayTapped(CalendarDay calendarDay) {
        int weekIndex = CalendarUtils.dayToWeek(calendarDay.year, calendarDay.month, calendarDay.day);
        controller.onDayOfMonthSelected(calendarDay.year, calendarDay.month, calendarDay.day, weekIndex);
        setSelectedDay(calendarDay);
    }

    public void setSelectedDay(CalendarDay calendarDay) {
        if (selectWay == 0) {//单选
            selectedDays.setFirst(calendarDay);
        } else {//多选
            if (selectedDays.getFirst() != null && selectedDays.getLast() == null) {
                if (selectedDays.getFirst().compare(calendarDay) == 0) {
                    return;
                }
                selectedDays.setLast(calendarDay);
                controller.onDateRangeSelected(selectedDays);
            } else if (selectedDays.getLast() != null) {
                selectedDays.setFirst(calendarDay);
                selectedDays.setLast(null);
            } else {
                selectedDays.setFirst(calendarDay);
            }
        }

        notifyDataSetChanged();
    }

    public static class CalendarDay implements Serializable {
        private static final long serialVersionUID = -5456695978688356202L;
        private Calendar calendar;

        int day;
        int month;
        int year;

        public CalendarDay() {
            setTime(System.currentTimeMillis());
        }

        public CalendarDay(int year, int month, int day) {
            setDay(year, month, day);
        }

        public CalendarDay(long timeInMillis) {
            setTime(timeInMillis);
        }

        public CalendarDay(Calendar calendar) {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        private void setTime(long timeInMillis) {
            if (calendar == null) {
                calendar = Calendar.getInstance();
            }
            calendar.setTimeInMillis(timeInMillis);
            month = this.calendar.get(Calendar.MONTH);
            year = this.calendar.get(Calendar.YEAR);
            day = this.calendar.get(Calendar.DAY_OF_MONTH);
        }

        public void set(CalendarDay calendarDay) {
            year = calendarDay.year;
            month = calendarDay.month;
            day = calendarDay.day;
        }

        public void setDay(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public Date getDate() {
            if (calendar == null) {
                calendar = Calendar.getInstance();
            }
            calendar.set(year, month, day);
            return calendar.getTime();
        }

        public int compare(CalendarDay calendarDay) {
            if (year == calendarDay.year && month == calendarDay.month && day == calendarDay.day) {
                return 0;
            } else if ((year > calendarDay.year) || (year == calendarDay.year && month > calendarDay.month)
                    || (year == calendarDay.year && month == calendarDay.month && day > calendarDay.day)) {
                return 1;
            } else {
                return -1;
            }
        }

        @Override
        public String toString() {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{ year: ");
            stringBuilder.append(year);
            stringBuilder.append(", month: ");
            stringBuilder.append(month);
            stringBuilder.append(", day: ");
            stringBuilder.append(day);
            stringBuilder.append(" }");

            return stringBuilder.toString();
        }
    }

    public SelectedDays<CalendarDay> getSelectedDays() {
        return selectedDays;
    }

    public static class SelectedDays<K> implements Serializable {
        private static final long serialVersionUID = 3942549765282708376L;
        private K first;
        private K last;

        public K getFirst() {
            return first;
        }

        public void setFirst(K first) {
            this.first = first;
        }

        public K getLast() {
            return last;
        }

        public void setLast(K last) {
            this.last = last;
        }
    }
}