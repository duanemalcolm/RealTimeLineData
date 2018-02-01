package com.enzuredigital.realtimelinedata;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * This class stores and manages a real-time line data set for the MPAndroidChart library.
 *
 *     Future Work
 * ===================
 * As a possible improvement, a buffer double the size of the number of samples can be created and
 * instead of shifting the entries down the dataset, we can shift the graph window to show the
 * right range. Once the end of the dataset is reached, one shift and prune is done to the dataset
 * and the window is shifted back to the start of the dataset.
 *
 * This would require twice the memory but might be faster. The speed improvement would need to be
 * tested.
 */
public class RealTimeLineData {

    private int channels = 0;
    private long maxX = 0;
    private long[] lastX;
    private int samples = 1000;
    private LineData lineData;
    private boolean debug = false;

    public RealTimeLineData(int samples) {
        this.samples = samples;
    }

    public void setDebug() {
        debug = true;
    }

    private void msg(String txt) {
        if (debug) {
            System.out.println(txt);
        }
    }

    private static ArrayList<String> createXValues(int samples) {
        ArrayList<String> xValues = new ArrayList<>();
        for (int i = 0; i < samples; i++) {
            xValues.add(String.valueOf((i + 1)));
        }
        return xValues;
    }

    public void setLineDataSets(ArrayList<ILineDataSet> lineDataSets) {
        channels = lineDataSets.size();
        lastX = new long[channels];
        for (int i = 0; i < channels; i++) {
            lastX[i] = -1;
        }
        lineData = new LineData(createXValues(samples), lineDataSets);
    }

    public LineData getLineData() {
        return lineData;
    }

    public void notifyDataChanged() {
        lineData.notifyDataChanged();
    }

    public void addSample(int channel, long x, float value) {
        if (x < lastX[channel]) {
            return;
        }
        ILineDataSet dataset;

        // Shift all datasets
        if (x > maxX) {
            int delta = (int) (x - maxX);
            for (int ch = 0; ch < channels; ch++) {
                dataset = lineData.getDataSetByIndex(ch);
                shiftAndPruneDataset(dataset, delta);
            }
            maxX = x;
        }

        // Add sample
        int index = (int) (maxX - x + samples - 1);
        dataset = lineData.getDataSetByIndex(channel);
        dataset.addEntry(new Entry(value, index));
        lastX[channel] = x;
    }

    /**
     * Shifts the entries in a dataset and pruned the entries that fall off the end of the dataset.
     *
     * @param dataset to be shifted and pruned
     * @param delta amount to shift by.
     */
    private void shiftAndPruneDataset(ILineDataSet dataset, int delta) {
        Entry e;
        int index;
        int newIndex;
        int n0 = dataset.getEntryCount() - 1;
        for (int i = n0; i > -1; i--) {
            e = dataset.getEntryForIndex(i);
            index = e.getXIndex();
            newIndex = index - delta;
            if (newIndex < 0) {
                dataset.removeEntry(e);
            } else {
                e.setXIndex(newIndex);
            }
        }
    }

}
