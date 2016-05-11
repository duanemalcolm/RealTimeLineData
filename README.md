# RealTimeLineData
A class to manage real-time line chart data for MPAndroidChart

```java
lineData = new RealTimeLineData(500);  // 10 seconds range, 20ms resolution gives 500 samples (10 seconds / 0.02 seconds)
ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
for (int ch = 0; ch < 3; ch++) {
    ArrayList<Entry> yValues = new ArrayList<>();
    LineDataSet dataSet = new LineDataSet(yValues, "Ch" + ch);
    dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
    dataSet.setColor(DEFAULT_COLORS[plotOrder[ch]]);
    lineDataSets.add(dataSet);
}
lineData.setLineDataSets(lineDataSets);

lineChart = (LineChart) rootView.findViewById(R.id.line_chart);
lineChart.setData(lineData.getLineData());
lineChart.invalidate()

lineData.addSample(0, 2, 15f);  // channel=0, x=2 (40ms), y=15f
lineData.addSample(1, 3, 17f);  // channel=1, x=3 (30ms), y=17f
lineData.addSample(1, 6, 21f);  // channel=1, x=6 (120ms), y=21f
lineData.addSample(0, 5, 5f);  // channel=1, x=5 (100ms), y=5f
...
lineData.notifyDataChanged();
```
Once you start adding samples beyond 500 samples or 10 seconds, the data will slide off the end of the buffer and plot only the recent data. This means you don't store or plot a large number of points but it also means you can't slide back to look at the history.
