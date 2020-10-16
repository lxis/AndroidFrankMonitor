package com.frankui.frankmonitor;

public class MonitorConfig {

    private final static int MILLS_TO_NANO = 1000000;
    // 每帧之间差异达到多少判断是卡
    private int jankThresholdMills = 20;
    // stack耗时达到多少输出log
    private int treePrintThresholdMills = 1;
    // stack耗时达到多少需要折叠
    private int treeFoldPrintThresholdMills = 5;

    public int getJankThresholdNano() {
        return jankThresholdMills * MILLS_TO_NANO;
    }

    public void setJankThresholdMills(int jankThreshold) {
        this.jankThresholdMills = jankThreshold;
    }

    public int getTreePrintThresholdNano() {
        return treePrintThresholdMills * MILLS_TO_NANO;
    }

    public void setTreePrintThresholdMills(int treePrintThreshold) {
        this.treePrintThresholdMills = treePrintThreshold;
    }

    public int getTreeFoldPrintThresholdNano() {
        return treeFoldPrintThresholdMills * MILLS_TO_NANO;
    }

    public void setTreeFoldPrintThresholdMills(int treeFoldPrintThreshold) {
        this.treeFoldPrintThresholdMills = treeFoldPrintThreshold;
    }
}
