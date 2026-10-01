package com.pk.core.business.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.pk.core.common.web.BaseRes;

@Service
public class RestSuccessHandleImpl implements RestSuccessHandle {

    @Override
    public BaseRes handleSuccess(Object data) {
        return new BaseRes(HttpStatus.OK.value(), HttpStatus.OK.name(), "Thành công", data);
    }
}
