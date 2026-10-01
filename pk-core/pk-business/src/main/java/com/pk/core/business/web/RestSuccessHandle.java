package com.pk.core.business.web;

import com.pk.core.common.web.BaseRes;

/** Dựng BaseRes cho response thành công. Xem AbstractRest. */
public interface RestSuccessHandle {

    BaseRes handleSuccess(Object data);
}
