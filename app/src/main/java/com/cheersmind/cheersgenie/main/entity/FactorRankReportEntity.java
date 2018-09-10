package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * Created by Administrator on 2018/2/4.
 */

public class FactorRankReportEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "rank")
    private int rank;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
