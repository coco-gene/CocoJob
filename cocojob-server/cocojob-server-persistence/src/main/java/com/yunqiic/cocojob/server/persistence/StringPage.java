package com.yunqiic.cocojob.server.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文本分页
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StringPage {
    /**
     * 当前页数
     */
    private long index;
    /**
     * 总页数
     */
    private long totalPages;
    /**
     * 文本数据
     */
    private String data;

    public static StringPage simple(String data) {
        StringPage sp = new StringPage();
        sp.index = 0;
        sp.totalPages = 1;
        sp.data = data;
        return sp;
    }
}
