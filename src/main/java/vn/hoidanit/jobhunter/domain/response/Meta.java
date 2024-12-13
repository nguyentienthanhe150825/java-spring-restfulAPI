package vn.hoidanit.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Meta {
    private int page;     // Trang hiện tại
    private int pageSize; // Số lượng bản ghi
    private int pages;    // Tổng số trang
    private long total;    // Tổng số phần tử
}
