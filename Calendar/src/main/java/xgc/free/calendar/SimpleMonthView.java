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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.HashMap;

class SimpleMonthView extends View {
    private static final boolean DEBUG = false;
    private static final int ONE_DAY_MILLIONS = 24 * 60 * 60 * 1000;
    public static final String VIEW_PARAMS_MONTH = "month";
    public static final String VIEW_PARAMS_YEAR = "year";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_DAY = "selected_begin_day";
    public static final String VIEW_PARAMS_SELECTED_LAST_DAY = "selected_last_day";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_MONTH = "selected_begin_month";
    public static final String VIEW_PARAMS_SELECTED_LAST_MONTH = "selected_last_month";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_YEAR = "selected_begin_year";
    public static final String VIEW_PARAMS_SELECTED_LAST_YEAR = "selected_last_year";
    public static final String VIEW_PARAMS_WEEK_START = "week_start";

    protected static int DEFAULT_HEIGHT = 32;
    protected static final int DEFAULT_NUM_ROWS = 6;

    protected int mPadding = 0;

    protected Paint mYearMonthPaint;//绘制年月画笔
    protected Paint mSolarDayPaint;//公历day画笔
    protected Paint mLunarDayPaint;//农历day画笔
    protected Paint mSelectedBgPaint;//选中背景画笔

    protected int mYearMonthTextColor;//年月text颜色
    protected int mWeekDayColor = -1;//日期是周末时的text颜色
    protected int mSolarDayColor;//公历day颜色
    protected int mLunarDayColor;//农历day颜色
    protected int mCurrentDayTextColor;//当天text颜色
    protected int mPreviousDayColor;//当天之前的text颜色
    protected int mSelectedDayColor;//选择日期的text颜色
    protected int mSelectedDayBgColor;//选择日期的背景颜色
    protected int mSelectedBetweenDayBgColor;//开始和结束之间日期的背景颜色

    protected int mYearMonthTextSize;//年月字体大小
    protected int mSolarDayTextSize;//公历day字体大小
    protected int mLunarDayTextSize;//农历字体大小
    protected int mSelectedRadius;//圆形选择背景大小
    protected int mHeaderSize;//年月高度
    protected int mHeaderPaddingTop;
    protected int mHeaderPaddingBottom;
    protected int mRectBgWidth;//矩形背景宽度
    protected int mRectBgHeight;//矩形背景高度

    protected int mDrawShape = 0;
    private final StringBuilder mStringBuilder;

    protected int mSelectedBeginDay = -1;
    protected int mSelectedLastDay = -1;
    protected int mSelectedBeginMonth = -1;
    protected int mSelectedLastMonth = -1;
    protected int mSelectedBeginYear = -1;
    protected int mSelectedLastYear = -1;
    protected int mWeekStart = 1;//这周的第一天
    protected int mNumDays = 7;
    protected int mNumCells = mNumDays;//这个月有多少天
    private int mDayOfWeekStart = 0;//星期的索引(顺序为SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, and SATURDAY)

    protected float mRowHeight = DEFAULT_HEIGHT;
    protected float mGridWidth;//网格宽度
    protected int mWidth;
    protected int mYear;//SimpleMonthView对应的年
    protected int mMonth;//SimpleMonthView对应的月

    final Time today;

    private final Calendar mCalendar;
    private final Boolean isPrevDayEnabled;//是否使能已过去的日期
    private int mPreviousDayOffset;

    private int mNumRows = DEFAULT_NUM_ROWS;

    private long beginTimeStamp, lastTimeStamp;
    private Paint.FontMetrics mYearMonthFontMetrics;

    private OnDayClickListener mOnDayClickListener;

    public SimpleMonthView(Context context, TypedArray typedArray) {
        super(context);

        Resources resources = context.getResources();
        mCalendar = Calendar.getInstance();
        today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        mCurrentDayTextColor = typedArray.getColor(R.styleable.DatePickerView_colorCurrentDay, resources.getColor(R.color.solar_day));
        mYearMonthTextColor = typedArray.getColor(R.styleable.DatePickerView_colorYearMonth, resources.getColor(R.color.year_month));
        mSolarDayColor = typedArray.getColor(R.styleable.DatePickerView_colorSolarDay, resources.getColor(R.color.solar_day));
        mLunarDayColor = typedArray.getColor(R.styleable.DatePickerView_colorLunarDay, resources.getColor(R.color.lunar_day));
        mPreviousDayColor = typedArray.getColor(R.styleable.DatePickerView_colorPreviousDay, resources.getColor(R.color.previous_day));
        mSelectedDayBgColor = typedArray.getColor(R.styleable.DatePickerView_colorSelectedDayBackground, resources.getColor(R.color.selected_day_background));
        mSelectedBetweenDayBgColor = typedArray.getColor(R.styleable.DatePickerView_colorSelectedBetweenDayBackground, resources.getColor(R.color.selected_day_background));
        mSelectedDayColor = typedArray.getColor(R.styleable.DatePickerView_colorSelectedDayText, resources.getColor(R.color.selected_day_text));

        mYearMonthTextSize = typedArray.getDimensionPixelSize(R.styleable.DatePickerView_textSizeYearMonth, resources.getDimensionPixelSize(R.dimen.text_size_year_month));
        mSolarDayTextSize = typedArray.getDimensionPixelSize(R.styleable.DatePickerView_textSizeSolarDay, resources.getDimensionPixelSize(R.dimen.text_size_solar_day));
        mLunarDayTextSize = typedArray.getDimensionPixelSize(R.styleable.DatePickerView_textSizeLunarDay, resources.getDimensionPixelSize(R.dimen.text_size_lunar_day));
        //mHeaderSize = typedArray.getDimensionPixelOffset(R.styleable.DayPickerView_headerYearMonthHeight, resources.getDimensionPixelOffset(R.dimen.header_month_height));
        mHeaderPaddingTop = typedArray.getDimensionPixelOffset(R.styleable.DatePickerView_headerPaddingTop, resources.getDimensionPixelOffset(R.dimen.header_padding_top));
        mHeaderPaddingBottom = typedArray.getDimensionPixelOffset(R.styleable.DatePickerView_headerPaddingBottom, resources.getDimensionPixelOffset(R.dimen.header_padding_bottom));
        mSelectedRadius = typedArray.getDimensionPixelSize(R.styleable.DatePickerView_selectedDayRadius, -1);
        mRectBgWidth = typedArray.getDimensionPixelSize(R.styleable.DatePickerView_rectWidth, resources.getDimensionPixelOffset(R.dimen.rect_width));
        mRectBgHeight = typedArray.getDimensionPixelSize(R.styleable.DatePickerView_rectHeight, resources.getDimensionPixelOffset(R.dimen.rect_height));

        mDrawShape = typedArray.getInt(R.styleable.DatePickerView_drawShape, 0);
        isPrevDayEnabled = typedArray.getBoolean(R.styleable.DatePickerView_enablePreviousDay, true);
        mPreviousDayOffset = typedArray.getInt(R.styleable.DatePickerView_previousDayOffset, 0);

        mHeaderSize += mHeaderPaddingTop + mHeaderPaddingBottom + mYearMonthTextSize;

        mStringBuilder = new StringBuilder(50);
        int calendarHeight = typedArray.getDimensionPixelSize(R.styleable.DatePickerView_calendarHeight, resources.getDimensionPixelOffset(R.dimen.calendar_height));
        mRowHeight = (calendarHeight - mHeaderSize) * 1.0f / 6;

        initView();
    }

    private int calculateNumRows() {
        int offset = findDayOffset();
        int dividend = (offset + mNumCells) / mNumDays;
        int remainder = (offset + mNumCells) % mNumDays;
        return (dividend + (remainder > 0 ? 1 : 0));
    }

    /**
     * 绘制年月
     *
     * @param canvas
     */
    private void drawYearMonth(Canvas canvas) {
        float x = mWidth * 1.0f / 2;
        float y = mYearMonthTextSize * 1.0f / 2 + (-mYearMonthFontMetrics.top - mYearMonthFontMetrics.bottom) / 2 + mHeaderPaddingTop;
        StringBuilder stringBuilder = new StringBuilder(getMonthAndYearString().toLowerCase());
        stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
        test1(canvas);
        canvas.drawText(stringBuilder.toString(), x, y, mYearMonthPaint);
    }

    private void test1(Canvas canvas) {
        if (DEBUG) {
            Paint testPaint = new Paint();
            testPaint.setAntiAlias(true);
            testPaint.setColor(0xffff0000);
            testPaint.setStyle(Style.FILL);

            canvas.drawRect(0, 0, mWidth, mHeaderSize, testPaint);
        }
    }

    private int findDayOffset() {
        return (mDayOfWeekStart < mWeekStart ? (mDayOfWeekStart + mNumDays) : mDayOfWeekStart)
                - mWeekStart;
    }

    private String getMonthAndYearString() {
        mStringBuilder.setLength(0);
        mStringBuilder.append(mYear);
        mStringBuilder.append("年");
        mStringBuilder.append(mMonth + 1);
        mStringBuilder.append("月");

        return mStringBuilder.toString();
    }

    private void onDayClick(SimpleMonthAdapter.CalendarDay calendarDay) {
        if (mOnDayClickListener != null && (isPrevDayEnabled || !prevDay(calendarDay.day, today))) {
            mOnDayClickListener.onDayClick(this, calendarDay);
        }
    }

    private boolean prevDay(int monthDay, Time time) {
        long targetTimeStamp = CalendarUtils.getTimeInMillis(time.year, time.month + 1, time.monthDay) - mPreviousDayOffset * ONE_DAY_MILLIONS;
        long dayTimeStamp = CalendarUtils.getTimeInMillis(mYear, mMonth + 1, monthDay);
        return dayTimeStamp < targetTimeStamp;
    }

    private void test2(Canvas canvas) {
        if (DEBUG) {
            canvas.drawLine(mGridWidth, 0, mGridWidth, getMeasuredHeight(), mLunarDayPaint);
            canvas.drawLine(mGridWidth * 2, 0, mGridWidth * 2, getMeasuredHeight(), mLunarDayPaint);
            canvas.drawLine(mGridWidth * 3, 0, mGridWidth * 3, getMeasuredHeight(), mLunarDayPaint);
            canvas.drawLine(mGridWidth * 4, 0, mGridWidth * 4, getMeasuredHeight(), mLunarDayPaint);
            canvas.drawLine(mGridWidth * 5, 0, mGridWidth * 5, getMeasuredHeight(), mLunarDayPaint);
            canvas.drawLine(mGridWidth * 6, 0, mGridWidth * 6, getMeasuredHeight(), mLunarDayPaint);
            canvas.drawLine(mGridWidth * 7, 0, mGridWidth * 7, getMeasuredHeight(), mLunarDayPaint);

            canvas.drawLine(0, mRowHeight + mHeaderSize, getMeasuredWidth(), mRowHeight + mHeaderSize, mLunarDayPaint);
            canvas.drawLine(0, mRowHeight * 2 + mHeaderSize, getMeasuredWidth(), mRowHeight * 2 + mHeaderSize, mLunarDayPaint);
            canvas.drawLine(0, mRowHeight * 3 + mHeaderSize, getMeasuredWidth(), mRowHeight * 3 + mHeaderSize, mLunarDayPaint);
            canvas.drawLine(0, mRowHeight * 4 + mHeaderSize, getMeasuredWidth(), mRowHeight * 4 + mHeaderSize, mLunarDayPaint);
            canvas.drawLine(0, mRowHeight * 5 + mHeaderSize, getMeasuredWidth(), mRowHeight * 5 + mHeaderSize, mLunarDayPaint);
            canvas.drawLine(0, mRowHeight * 6 + mHeaderSize, getMeasuredWidth(), mRowHeight * 6 + mHeaderSize, mLunarDayPaint);
            canvas.drawLine(0, mRowHeight * 7 + mHeaderSize, getMeasuredWidth(), mRowHeight * 7 + mHeaderSize, mLunarDayPaint);
        }
    }

    protected void drawDay(Canvas canvas) {
        float y = mHeaderSize + mRowHeight / 2;

        Paint.FontMetrics solarFontMetrics = mSolarDayPaint.getFontMetrics();
        Paint.FontMetrics lunarFontMetrics = mLunarDayPaint.getFontMetrics();

        float solarBaseY = (-solarFontMetrics.top - solarFontMetrics.bottom) / 2;
        float lunarBaseY = (-lunarFontMetrics.top - lunarFontMetrics.bottom) / 2;

        int margin = CalendarUtils.dp2px(getContext(), 2);
        float solarDayY = mHeaderSize + mRowHeight / 2 + solarBaseY - (solarBaseY + lunarBaseY) / 2 - margin;
        float lunarDayY = mHeaderSize + mRowHeight / 2 + lunarBaseY + (solarBaseY + lunarBaseY) / 2 + margin;

        int dayOffset = findDayOffset();//偏移天数
        int day = 1;

        test2(canvas);

        while (day <= mNumCells) {
            long currentTimeStamp = CalendarUtils.getTimeInMillis(mYear, mMonth + 1, day);
            float x = mGridWidth / 2 + dayOffset * mGridWidth + mPadding;
            String lunar = LunarCalendarUtils.getLunarDayString(mYear, mMonth + 1, day, false);

            //开始和结束日期的背景颜色
            if ((mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear) || (mMonth == mSelectedLastMonth && mSelectedLastDay == day && mSelectedLastYear == mYear)) {
                mSelectedBgPaint.setColor(mSelectedDayBgColor);
                drawSelectedBackground(canvas, x, y);
            }

            if (today.year == mYear && today.month == mMonth && today.monthDay == day) {
                //设置今天文本颜色
                mSolarDayPaint.setColor(mCurrentDayTextColor);
                mSolarDayPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                mLunarDayPaint.setColor(mCurrentDayTextColor);
                mLunarDayPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {//设置非今天文本颜色
                mSolarDayPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                mLunarDayPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                if (CalendarUtils.isWeek(mCalendar, mYear, mMonth, day) && mWeekDayColor != -1) {//周末
                    mSolarDayPaint.setColor(mWeekDayColor);
                    mLunarDayPaint.setColor(mWeekDayColor);
                } else {//非周末
                    mSolarDayPaint.setColor(mSolarDayColor);
                    mLunarDayPaint.setColor(mLunarDayColor);
                }
            }

            //选择日期的文本颜色
            if ((mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear)
                    || (mMonth == mSelectedLastMonth && mSelectedLastDay == day && mSelectedLastYear == mYear)) {
                mSolarDayPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                mLunarDayPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                mSolarDayPaint.setColor(mSelectedDayColor);
                mLunarDayPaint.setColor(mSelectedDayColor);
            }

            //设置开始和结束之间日期的样式
            if (mSelectedBeginDay != -1 && mSelectedLastDay != -1) {
                if ((currentTimeStamp > beginTimeStamp && currentTimeStamp < lastTimeStamp) ||
                        (currentTimeStamp > lastTimeStamp && currentTimeStamp < beginTimeStamp)) {
                    mSelectedBgPaint.setColor(mSelectedBetweenDayBgColor);
                    drawSelectedBackground(canvas, x, y);

                    mSolarDayPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    mSolarDayPaint.setColor(mSelectedDayColor);
                    mLunarDayPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    mLunarDayPaint.setColor(mSelectedDayColor);
                }
            }

            //设置当天之前day的文本颜色
            if (!isPrevDayEnabled && prevDay(day, today)) {
                mSolarDayPaint.setColor(mPreviousDayColor);
                mLunarDayPaint.setColor(mPreviousDayColor);
            }

            canvas.drawText(String.format("%d", day), x, solarDayY, mSolarDayPaint);//公历
            canvas.drawText(lunar, x, lunarDayY, mLunarDayPaint);//农历

            dayOffset++;
            if (dayOffset == mNumDays) {
                dayOffset = 0;
                y += mRowHeight;
                solarDayY += mRowHeight;
                lunarDayY += mRowHeight;
            }
            day++;
        }
    }

    private void drawSelectedBackground(Canvas canvas, float x, float y) {
        if (mDrawShape == 0) {
            float width = Math.min(mRectBgWidth, mGridWidth);
            float height = Math.min(mRectBgHeight, mRowHeight);

            float left = x - width / 2;
            float right = left + width;
            float top = y - height / 2;
            float bottom = top + height;
            RectF rectF = new RectF(left, top, right, bottom);
            if (mSelectedRadius != -1) {
                canvas.drawRoundRect(rectF, mSelectedRadius, mSelectedRadius, mSelectedBgPaint);
            } else {
                canvas.drawRect(rectF, mSelectedBgPaint);
            }
        } else if (mDrawShape == 1) {
            float radius = Math.min(mGridWidth, mRowHeight) / 2 - CalendarUtils.dp2px(getContext(), 5);
            if (mSelectedRadius != -1) {
                radius = Math.min(radius, mSelectedRadius);
            }
            canvas.drawCircle(x, y, radius, mSelectedBgPaint);
        }
    }

    public SimpleMonthAdapter.CalendarDay getDayFromLocation(float x, float y) {
        int padding = mPadding;
        if ((x < padding) || (x > mWidth - mPadding)) {
            return null;
        }

        int yDay = (int) ((y - mHeaderSize) / mRowHeight);
        int day = 1 + ((int) ((x - padding) * mNumDays / (mWidth - padding - mPadding)) - findDayOffset()) + yDay * mNumDays;

        if (mMonth > 11 || mMonth < 0 || CalendarUtils.getDaysInMonth(mMonth, mYear) < day || day < 1)
            return null;

        return new SimpleMonthAdapter.CalendarDay(mYear, mMonth, day);
    }

    protected void initView() {
        //年月Paint
        mYearMonthPaint = new Paint();
        mYearMonthPaint.setFakeBoldText(true);
        mYearMonthPaint.setAntiAlias(true);
        mYearMonthPaint.setTextSize(mYearMonthTextSize);
        mYearMonthPaint.setColor(mYearMonthTextColor);
        mYearMonthPaint.setTextAlign(Align.CENTER);
        mYearMonthPaint.setStyle(Style.FILL);

        //选中背景画笔
        mSelectedBgPaint = new Paint();
        mSelectedBgPaint.setFakeBoldText(true);
        mSelectedBgPaint.setAntiAlias(true);
        mSelectedBgPaint.setColor(mSelectedDayBgColor);
        mSelectedBgPaint.setTextAlign(Align.CENTER);
        mSelectedBgPaint.setStyle(Style.FILL);

        //day画笔
        mSolarDayPaint = new Paint();
        mSolarDayPaint.setAntiAlias(true);
        mSolarDayPaint.setTextSize(mSolarDayTextSize);
        mSolarDayPaint.setStyle(Style.FILL);
        mSolarDayPaint.setTextAlign(Align.CENTER);
        mSolarDayPaint.setFakeBoldText(false);

        mLunarDayPaint = new Paint(mSolarDayPaint);
        mLunarDayPaint.setTextSize(mLunarDayTextSize);

        mYearMonthFontMetrics = mYearMonthPaint.getFontMetrics();
    }

    protected void onDraw(Canvas canvas) {
        drawYearMonth(canvas);
        //drawWeek(canvas);
        drawDay(canvas);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (int) (mRowHeight * mNumRows + mHeaderSize));
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mGridWidth = mWidth * 1.0f / mNumDays;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            SimpleMonthAdapter.CalendarDay calendarDay = getDayFromLocation(event.getX(), event.getY());
            if (calendarDay != null) {
                onDayClick(calendarDay);
            }
        }
        return true;
    }

    public void reuse() {
        mNumRows = DEFAULT_NUM_ROWS;
        requestLayout();
    }

    public void setMonthParams(HashMap<String, Integer> params) {
        if (!params.containsKey(VIEW_PARAMS_MONTH) && !params.containsKey(VIEW_PARAMS_YEAR)) {
            throw new InvalidParameterException("You must specify month and year for this view");
        }
        setTag(params);

        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_DAY)) {
            mSelectedBeginDay = params.get(VIEW_PARAMS_SELECTED_BEGIN_DAY);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_DAY)) {
            mSelectedLastDay = params.get(VIEW_PARAMS_SELECTED_LAST_DAY);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_MONTH)) {
            mSelectedBeginMonth = params.get(VIEW_PARAMS_SELECTED_BEGIN_MONTH);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_MONTH)) {
            mSelectedLastMonth = params.get(VIEW_PARAMS_SELECTED_LAST_MONTH);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_YEAR)) {
            mSelectedBeginYear = params.get(VIEW_PARAMS_SELECTED_BEGIN_YEAR);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_YEAR)) {
            mSelectedLastYear = params.get(VIEW_PARAMS_SELECTED_LAST_YEAR);
        }

        if (mSelectedBeginYear != -1 && mSelectedBeginMonth != -1 && mSelectedBeginDay != -1) {
            beginTimeStamp = CalendarUtils.getTimeInMillis(mSelectedBeginYear, mSelectedBeginMonth + 1, mSelectedBeginDay);
        }
        if (mSelectedLastYear != -1 && mSelectedLastMonth != -1 && mSelectedLastDay != -1) {
            lastTimeStamp = CalendarUtils.getTimeInMillis(mSelectedLastYear, mSelectedLastMonth + 1, mSelectedLastDay);
        }

        mMonth = params.get(VIEW_PARAMS_MONTH);
        mYear = params.get(VIEW_PARAMS_YEAR);

        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mDayOfWeekStart = mCalendar.get(Calendar.DAY_OF_WEEK);//本月第一天是星期几

        //getFirstDayOfWeek得到一周的第一天是从星期几开始的(顺序为SUNDAY=1, MONDAY=2, TUESDAY=3, WEDNESDAY=4, THURSDAY=5, FRIDAY=6, and SATURDAY=7)
        if (params.containsKey(VIEW_PARAMS_WEEK_START)) {
            mWeekStart = params.get(VIEW_PARAMS_WEEK_START);
        } else {
            mWeekStart = mCalendar.getFirstDayOfWeek();
        }

        mNumCells = CalendarUtils.getDaysInMonth(mMonth, mYear);//计算这个月有多少天
        mNumRows = calculateNumRows();//计算需要多少行
    }

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        mOnDayClickListener = onDayClickListener;
    }

    public interface OnDayClickListener {
        void onDayClick(SimpleMonthView simpleMonthView, SimpleMonthAdapter.CalendarDay calendarDay);
    }
}