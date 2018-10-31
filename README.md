# XGCCalendar
### 一切样式皆可定制的日历控件
- 支持单选和多选；
- 支持矩形、圆形、圆角矩形选择效果；
- 支持控制显示多少个月；
- 支持选择默认日期；
- 支持所有样式可定制；

## APK下载
[Download](https://github.com/linuxjava/XGCCalendar/raw/master/apk/app-debug.apk)

## 使用
### 添加依赖
```xml
implementation 'xgc.free.calendar:Calendar:0.2'
implementation 'xgc.free.pinned.decoration:PinnedDecoration:0.2'
```
## 方法

```
/**
 * 单选时设置默认选择日期
 *
 * @param year
 * @param month 从0到11
 * @param day
 */
public DatePickerView setSelectedDate(int year, int month, int day)

public void setController(DatePickerController controller)
```

```
public interface DatePickerController {
    /**
     * 当前月前显示多少个月
     *
     * @return
     */
    int preMonths();

    /**
     * 当前月开始往后显示多少个月
     *
     * @return
     */
    int afterMonths();

    /**
     * 选择日期
     *
     * @param year
     * @param month 0到11
     * @param day
     * @param weekIndex 1=SUNDAY, 2=MONDAY, 3=TUESDAY, 4=WEDNESDAY, 5=THURSDAY, 6=FRIDAY, 7=SATURDAY
     */
    void onDayOfMonthSelected(int year, int month, int day, int weekIndex);

    /**
     * 多选日期
     *
     * @param selectedDays
     */
    void onDateRangeSelected(final SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays);

}
```
## 属性说明

### 整体说明
![image](https://github.com/linuxjava/XGCCalendar/raw/master/screenshot/1.png)

整个日历分为2个大的部分：
- 顶部红色框中是星期；
- 绿色框中为一个月的日历；
绿色框又分为两部分：
- 头部的年月；
- 底部的day的显示；

### 支持的样式

#### 单选和多选
![image](https://github.com/linuxjava/XGCCalendar/raw/master/screenshot/2.png)
![image](https://github.com/linuxjava/XGCCalendar/raw/master/screenshot/3.png)

#### 圆形、矩形、圆角矩形
![image](https://github.com/linuxjava/XGCCalendar/raw/master/screenshot/2.png)
![image](https://github.com/linuxjava/XGCCalendar/raw/master/screenshot/4.png)
![image](https://github.com/linuxjava/XGCCalendar/raw/master/screenshot/5.png)

#### 星期样式

```
<attr name="topWeekHeight" format="dimension" />
<attr name="topWeekBgColor" format="color" />
<attr name="topWeekTextColor" format="color" />
<attr name="topWeekTextSize" format="dimension" />
```

#### 年月样式

```
<!--年月的高度-->
<attr name="headerYearMonthHeight" format="dimension" />
<attr name="headerPaddingTop" format="dimension" />
<attr name="headerPaddingBottom" format="dimension" />
<!--年月颜色-->
<attr name="colorYearMonth" format="color" />
<!--年月文本大小-->
<attr name="textSizeYearMonth" format="dimension" />
```

#### day样式

```
<!--选择日期背景颜色-->
<attr name="colorSelectedDayBackground" format="color" />
<!--开始和结束之间日期的背景颜色-->
<attr name="colorSelectedBetweenDayBackground" format="color" />
<!--选择日期文本颜色-->
<attr name="colorSelectedDayText" format="color" />
<!--今天的颜色-->
<attr name="colorCurrentDay" format="color" />
<!--日期是周末时的text颜色-->
<attr name="colorWeekDay" format="color" />
<!--今天之前的颜色-->
<attr name="colorPreviousDay" format="color" />
<!--公历day颜色-->
<attr name="colorSolarDay" format="color" />
<!--农历day颜色-->
<attr name="colorLunarDay" format="color" />
<!--公历day大小-->
<attr name="textSizeSolarDay" format="dimension" />
<!--农历day大小-->
<attr name="textSizeLunarDay" format="dimension" />
<!--圆形背景半径或矩形背景圆角-->
<attr name="selectedDayRadius" format="dimension" />
<!--矩形背景宽度-->
<attr name="rectWidth" format="dimension" />
<!--矩形背景高度-->
<attr name="rectHeight" format="dimension" />
<!--日历一个月的高度-->
<attr name="calendarHeight" format="dimension" />
<!--使能过去日期-->
<attr name="enablePreviousDay" format="boolean" />
<attr name="previousDayOffset" format="integer" />
<!--是否默认选择当天-->
<attr name="currentDaySelected" format="boolean" />

<attr name="drawShape" format="enum">
    <enum name="rect" value="0" />
    <enum name="circle" value="1" />
</attr>
<attr name="selectWay" format="enum">
    <!--单选-->
    <enum name="ratio" value="0" />
    <!--多选-->
    <enum name="multi" value="1" />
</attr>
```

## 附录所有属性

```
<declare-styleable name="DatePickerView">
    <!--**********************顶部星期属性**********************-->
    <attr name="topWeekHeight" format="dimension" />
    <attr name="topWeekBgColor" format="color" />
    <attr name="topWeekTextColor" format="color" />
    <attr name="topWeekTextSize" format="dimension" />

    <!--**********************年月属性**********************-->
    <!--年月的高度-->
    <attr name="headerYearMonthHeight" format="dimension" />
    <attr name="headerPaddingTop" format="dimension" />
    <attr name="headerPaddingBottom" format="dimension" />
    <!--年月颜色-->
    <attr name="colorYearMonth" format="color" />
    <!--年月文本大小-->
    <attr name="textSizeYearMonth" format="dimension" />

    <!--**********************日历属性**********************-->
    <!--选择日期背景颜色-->
    <attr name="colorSelectedDayBackground" format="color" />
    <!--开始和结束之间日期的背景颜色-->
    <attr name="colorSelectedBetweenDayBackground" format="color" />
    <!--选择日期文本颜色-->
    <attr name="colorSelectedDayText" format="color" />
    <!--今天的颜色-->
    <attr name="colorCurrentDay" format="color" />
    <!--日期是周末时的text颜色-->
    <attr name="colorWeekDay" format="color" />
    <!--今天之前的颜色-->
    <attr name="colorPreviousDay" format="color" />
    <!--公历day颜色-->
    <attr name="colorSolarDay" format="color" />
    <!--农历day颜色-->
    <attr name="colorLunarDay" format="color" />
    <!--公历day大小-->
    <attr name="textSizeSolarDay" format="dimension" />
    <!--农历day大小-->
    <attr name="textSizeLunarDay" format="dimension" />
    <!--圆形背景半径或矩形背景圆角-->
    <attr name="selectedDayRadius" format="dimension" />
    <!--矩形背景宽度-->
    <attr name="rectWidth" format="dimension" />
    <!--矩形背景高度-->
    <attr name="rectHeight" format="dimension" />
    <!--日历一个月的高度-->
    <attr name="calendarHeight" format="dimension" />
    <!--使能过去日期-->
    <attr name="enablePreviousDay" format="boolean" />
    <attr name="previousDayOffset" format="integer" />
    <!--是否默认选择当天-->
    <attr name="currentDaySelected" format="boolean" />

    <attr name="drawShape" format="enum">
        <enum name="rect" value="0" />
        <enum name="circle" value="1" />
    </attr>
    <attr name="selectWay" format="enum">
        <!--单选-->
        <enum name="ratio" value="0" />
        <!--多选-->
        <enum name="multi" value="1" />
    </attr>
</declare-styleable>
```




