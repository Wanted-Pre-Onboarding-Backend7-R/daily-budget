package com.mojh.dailybudget.common.web.dto;

import com.mojh.dailybudget.common.web.PutResult;
import lombok.Getter;

@Getter
public class PutResultResponse {

    private PutResult putResult;

    private Long entityId;

    private PutResultResponse(PutResult putResult, Long entityId) {
        this.putResult = putResult;
        this.entityId = entityId;
    }


    public static PutResultResponse created(Long id) {
        return new PutResultResponse(PutResult.CREATED, id);
    }

    public static PutResultResponse replaced(Long id) {
        return new PutResultResponse(PutResult.REPLACED, id);
    }

}
