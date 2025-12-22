package com.example.model1.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果封装类
 */
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 当前页码
     */
    private Integer currentPage;
    
    /**
     * 每页数量
     */
    private Integer pageSize;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 总页数
     */
    private Integer totalPages;
    
    /**
     * 数据列表
     */
    private List<T> records;
    
    public PageResult() {}
    
    public PageResult(Integer currentPage, Integer pageSize, Long total, List<T> records) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.total = total;
        this.records = records;
        if (pageSize != null && pageSize > 0) {
            this.totalPages = (int) ((total + pageSize - 1) / pageSize);
        }
    }
    
    // 静态构造方法
    public static <T> PageResult<T> of(Integer currentPage, Integer pageSize, Long total, List<T> records) {
        return new PageResult<>(currentPage, pageSize, total, records);
    }
    
    // Getter 和 Setter 方法
    public Integer getCurrentPage() {
        return currentPage;
    }
    
    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    
    public Long getTotal() {
        return total;
    }
    
    public void setTotal(Long total) {
        this.total = total;
    }
    
    public Integer getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
    
    public List<T> getRecords() {
        return records;
    }
    
    public void setRecords(List<T> records) {
        this.records = records;
    }
    
    @Override
    public String toString() {
        return "PageResult{" +
                "currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", total=" + total +
                ", totalPages=" + totalPages +
                ", records=" + records +
                '}';
    }
}