package com.enzuredigital.realtimelinedata;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class UnitTest {

    private static final int[] DEFAULT_COLORS = {0xFF0000, 0x00FF00, 0x0000FF, 0xFF00FF};
    private RealTimeLineData rtLineData;

    private void msg(String txt) {
        System.out.println(txt);
    }

    @Before
    public void setUp() throws Exception {
        rtLineData = new RealTimeLineData(10);
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
        for (int ch = 0; ch < 4; ch++) {
            ArrayList<Entry> yValues = new ArrayList<>();
            LineDataSet dataSet = new LineDataSet(yValues, "Ch" + ch);
            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet.setColor(DEFAULT_COLORS[ch]);
            dataSet.setDrawValues(false);
            dataSet.setDrawCircles(false);
            lineDataSets.add(dataSet);
        }
        rtLineData.setLineDataSets(lineDataSets);
    }

    @Test
    public void adding_four_datasets() throws Exception {
        LineData lineData = rtLineData.getLineData();
        List<String> xValues = lineData.getXVals();
        assertEquals(10, xValues.size());
        assertEquals(4, lineData.getDataSets().size());
    }

    @Test
    public void adding_a_sample() throws Exception {
        int ch1 = 1;
        Entry e;
        LineData lineData = rtLineData.getLineData();

        rtLineData.addSample(ch1, 0, 2f);
        e = lineData.getDataSets().get(ch1).getEntryForIndex(0);
        assertEquals(9, e.getXIndex());
        assertEquals(2f, e.getVal(), 0.001);
    }

    @Test
    public void adding_a_sample_nonzero_start() throws Exception {
        int ch1 = 1;
        Entry e;
        LineData lineData = rtLineData.getLineData();

        rtLineData.addSample(ch1, 5, 2f);
        e = lineData.getDataSets().get(ch1).getEntryForIndex(0);
        assertEquals(9, e.getXIndex());
        assertEquals(2f, e.getVal(), 0.001);
    }

    @Test
    public void adding_two_sequential_samples() throws Exception {
        int ch1 = 1;
        Entry e;
        LineData lineData = rtLineData.getLineData();

        rtLineData.addSample(ch1, 0, 2f);
        rtLineData.addSample(ch1, 1, 7f);

        e = lineData.getDataSets().get(ch1).getEntryForIndex(0);
        assertEquals(8, e.getXIndex());
        assertEquals(2f, e.getVal(), 0.001);
        e = lineData.getDataSets().get(ch1).getEntryForIndex(1);
        assertEquals(9, e.getXIndex());
        assertEquals(7f, e.getVal(), 0.001);
    }

    @Test
    public void adding_two_separated_samples() throws Exception {
        int ch1 = 1;
        Entry e;
        LineData lineData = rtLineData.getLineData();

        rtLineData.addSample(ch1, 0, 2f);
        rtLineData.addSample(ch1, 5, 7f);

        e = lineData.getDataSets().get(ch1).getEntryForIndex(0);
        assertEquals(4, e.getXIndex());
        assertEquals(2f, e.getVal(), 0.001);
        e = lineData.getDataSets().get(ch1).getEntryForIndex(1);
        assertEquals(9, e.getXIndex());
        assertEquals(7f, e.getVal(), 0.001);
    }

    @Test
    public void adding_overflow_samples() throws Exception {
        int ch1 = 1;
        Entry e;
        LineData lineData = rtLineData.getLineData();

        rtLineData.addSample(ch1, 0, 2f);
        rtLineData.addSample(ch1, 5, 7f);
        rtLineData.addSample(ch1, 10, 14f);
        rtLineData.addSample(ch1, 15, 21f);

        e = lineData.getDataSets().get(ch1).getEntryForIndex(0);
        assertEquals(4, e.getXIndex());
        assertEquals(14f, e.getVal(), 0.001);
        e = lineData.getDataSets().get(ch1).getEntryForIndex(1);
        assertEquals(9, e.getXIndex());
        assertEquals(21f, e.getVal(), 0.001);
    }

    @Test
    public void multichannel1() throws Exception {
        int ch1 = 1;
        int ch3 = 3;
        Entry e;
        LineData lineData = rtLineData.getLineData();

        rtLineData.addSample(ch1, 0, 2f);
        rtLineData.addSample(ch3, 2, 102f);

        e = lineData.getDataSets().get(ch1).getEntryForIndex(0);
        assertEquals(7, e.getXIndex());
        assertEquals(2, e.getVal(), 0.001);

        e = lineData.getDataSets().get(ch3).getEntryForIndex(0);
        assertEquals(9, e.getXIndex());
        assertEquals(102f, e.getVal(), 0.001);
    }

    @Test
    public void multichannel2() throws Exception {
        int ch1 = 1;
        int ch3 = 3;
        Entry e;
        LineData lineData = rtLineData.getLineData();

        rtLineData.addSample(ch1, 0, 2f);
        rtLineData.addSample(ch3, 2, 102f);
        rtLineData.addSample(ch1, 2, 4f);
        rtLineData.addSample(ch3, 5, 104f);

        e = lineData.getDataSets().get(ch1).getEntryForIndex(0);
        assertEquals(4, e.getXIndex());
        assertEquals(2f, e.getVal(), 0.001);
        e = lineData.getDataSets().get(ch1).getEntryForIndex(1);
        assertEquals(6, e.getXIndex());
        assertEquals(4f, e.getVal(), 0.001);

        e = lineData.getDataSets().get(ch3).getEntryForIndex(0);
        assertEquals(6, e.getXIndex());
        assertEquals(102f, e.getVal(), 0.001);
        e = lineData.getDataSets().get(ch3).getEntryForIndex(1);
        assertEquals(9, e.getXIndex());
        assertEquals(104f, e.getVal(), 0.001);
    }

    @Test
    public void multichannel_overflow() throws Exception {
        int ch1 = 1;
        int ch3 = 3;
        Entry e;
        rtLineData.setDebug();

        LineData lineData = rtLineData.getLineData();

        rtLineData.addSample(ch1, 0, 2f);
        rtLineData.addSample(ch3, 2, 102f);
        rtLineData.addSample(ch1, 5, 7f);
        rtLineData.addSample(ch3, 7, 105f);
        rtLineData.addSample(ch3, 9, 107f);
        rtLineData.addSample(ch1, 10, 14f);
        rtLineData.addSample(ch3, 11, 114f);
        rtLineData.addSample(ch1, 15, 21f);
        rtLineData.addSample(ch3, 18, 121f);

        e = lineData.getDataSets().get(ch1).getEntryForIndex(0);
        assertEquals(1, e.getXIndex());
        assertEquals(14f, e.getVal(), 0.001);
        e = lineData.getDataSets().get(ch1).getEntryForIndex(1);
        assertEquals(6, e.getXIndex());
        assertEquals(21f, e.getVal(), 0.001);

        e = lineData.getDataSets().get(ch3).getEntryForIndex(0);
//        assertEquals(0, e.getXIndex());
        assertEquals(107f, e.getVal(), 0.001);
        e = lineData.getDataSets().get(ch3).getEntryForIndex(1);
        assertEquals(2, e.getXIndex());
        assertEquals(114f, e.getVal(), 0.001);
        e = lineData.getDataSets().get(ch3).getEntryForIndex(2);
        assertEquals(9, e.getXIndex());
        assertEquals(121f, e.getVal(), 0.001);
    }

}