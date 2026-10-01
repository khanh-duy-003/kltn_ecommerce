package com.pk.core.business.web;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Lớp cha cho Rest controller ở pk-api: mỗi method tự try/catch, thành công gọi
 * restSuccessHandle.handleSuccess(...), lỗi gọi restErrorHandle.handleException(...) - phỏng theo
 * AbstractRest của boxchatSocket. Dùng @Autowired field injection (thay vì constructor Lombok) vì
 * đây là lớp cha được kế thừa - không thể dùng @RequiredArgsConstructor cho field của lớp cha khi
 * lớp con cũng cần @RequiredArgsConstructor cho field riêng của nó.
 */
public abstract class AbstractRest {

    @Autowired
    protected RestSuccessHandle restSuccessHandle;

    @Autowired
    protected RestErrorHandle restErrorHandle;
}
