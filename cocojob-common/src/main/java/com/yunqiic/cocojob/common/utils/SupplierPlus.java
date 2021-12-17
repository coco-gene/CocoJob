package com.yunqiic.cocojob.common.utils;

/**
 * Represents a supplier of results.
 *
 * <p>There is no requirement that a new or distinct result be returned each
 * time the supplier is invoked.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #get()}.
 *
 * @param <T> the type of results supplied by this supplier
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@FunctionalInterface
public interface SupplierPlus<T> {

    /**
     * Gets a result.
     * @return a result
     * @throws Exception allow to throw Exception
     */
    T get() throws Exception;
}
