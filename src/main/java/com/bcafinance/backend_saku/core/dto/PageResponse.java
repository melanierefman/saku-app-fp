package com.bcafinance.backend_saku.core.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean isFirst;
    private boolean isLast;

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }

    public static <T, R> PageResponse<R> of(Page<T> page, java.util.function.Function<T, R> mapper) {
        return PageResponse.<R>builder()
                .content(page.getContent().stream().map(mapper).toList())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }

    public static <T> PageResponse<T> ofList(List<T> allItems, int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        int totalElements = allItems.size();
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        int fromIndex = Math.min(page * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<T> content = allItems.subList(fromIndex, toIndex);

        return PageResponse.<T>builder()
                .content(content)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(page)
                .pageSize(size)
                .isFirst(page == 0)
                .isLast(totalPages == 0 || page >= totalPages - 1)
                .build();
    }
}
