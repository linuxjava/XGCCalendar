package xgc.free.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DatePickerView extends LinearLayout {
    private RecyclerView recyclerView;
    private SimpleMonthAdapter adapter;
    private LinearLayout weekHeaderView;
    private int weekHeight;
    private int weekBgColor;
    private int weekTextColor;
    private int weekTextSize;

    public DatePickerView(Context context) {
        this(context, null);
    }

    public DatePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DatePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DatePickerView);
        Resources resources = context.getResources();
        weekHeight = typedArray.getDimensionPixelSize(R.styleable.DatePickerView_topWeekHeight, resources.getDimensionPixelSize(R.dimen.week_height));
        weekTextSize = typedArray.getDimensionPixelSize(R.styleable.DatePickerView_topWeekTextSize, resources.getDimensionPixelSize(R.dimen.week_text_size));
        weekBgColor = typedArray.getColor(R.styleable.DatePickerView_topWeekBgColor, resources.getColor(R.color.week_bg_color));
        weekTextColor = typedArray.getColor(R.styleable.DatePickerView_topWeekTextColor, resources.getColor(R.color.week_text_color));


        LayoutInflater.from(context).inflate(R.layout.calendar, this);

        weekHeaderView = (LinearLayout) findViewById(R.id.header);
        recyclerView = (RecyclerView) findViewById(R.id.calendar);

        //设置星期文本属性
        weekHeaderView.getLayoutParams().height = weekHeight;
        weekHeaderView.setBackgroundColor(weekBgColor);
        int count = weekHeaderView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = weekHeaderView.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTextColor(weekTextColor);
                ((TextView) child).setTextSize(TypedValue.COMPLEX_UNIT_PX, weekTextSize);
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setVerticalScrollBarEnabled(false);
        recyclerView.setFadingEdgeLength(0);
        adapter = new SimpleMonthAdapter(context, typedArray);
    }

    /**
     * 单选时设置默认选择日期
     *
     * @param year
     * @param month 从0到11
     * @param day
     */
    public DatePickerView setSelectedDate(int year, int month, int day) {
        adapter.setSelectedDate(year, month, day);
        return this;
    }

    public void setController(DatePickerController controller) {
        adapter.setController(controller);
        recyclerView.setAdapter(adapter);

        if (adapter.getScrollPosition() >= 0 && adapter.getScrollPosition() < adapter.getItemCount()) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (recyclerView.getLayoutManager() != null && recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(adapter.getScrollPosition(), 0);
                    }
                }
            });
        }
    }

    /**
     * 多选日期
     *
     * @return
     */
    public SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> getSelectedDays() {
        if (adapter == null) {
            return null;
        }
        return adapter.getSelectedDays();
    }
}
