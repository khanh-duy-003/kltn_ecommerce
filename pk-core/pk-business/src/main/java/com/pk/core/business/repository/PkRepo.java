package com.pk.core.business.repository;

import org.springframework.data.repository.NoRepositoryBean;
import vn.com.unit.sparwings.spring.data.repository.BatchReadableRepository;
import vn.com.unit.sparwings.spring.data.repository.BatchWritableRepository;
import vn.com.unit.sparwings.spring.data.repository.ScannableRepository;

import java.io.Serializable;

/**
 * Repository gốc của module: tương đương MirageRepository (thư viện đã đánh dấu @Deprecated)
 * nhưng do mình khai báo để không dính cảnh báo. Mọi repository trong gói này extends interface này.
 */
@NoRepositoryBean
public interface PkRepo<E, ID extends Serializable> extends
        ScannableRepository<E, ID>,
        BatchReadableRepository<E, ID>,
        BatchWritableRepository<E, ID> {
}
