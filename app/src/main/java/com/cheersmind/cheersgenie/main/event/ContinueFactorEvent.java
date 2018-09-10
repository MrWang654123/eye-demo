package com.cheersmind.cheersgenie.main.event;

import com.cheersmind.cheersgenie.main.entity.FactorInfoEntity;

/**
 * Created by Administrator on 2018/6/8.
 */

public class ContinueFactorEvent {

    private FactorInfoEntity factorInfoEntity;

    public ContinueFactorEvent(FactorInfoEntity factorInfoEntity){
        setFactorInfoEntity(factorInfoEntity);
    }

    public FactorInfoEntity getFactorInfoEntity() {
        return factorInfoEntity;
    }

    public void setFactorInfoEntity(FactorInfoEntity factorInfoEntity) {
        this.factorInfoEntity = factorInfoEntity;
    }
}
