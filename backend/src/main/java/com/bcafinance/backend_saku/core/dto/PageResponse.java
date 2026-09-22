package com.bcafinance.backend_saku.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Daftar data pada halaman saat ini")
    private List<T> content;

    @Schema(description = "Total seluruh jumlah data yang ditemukan", example = "50")
    private long totalElements;

    @Schema(description = "Total jumlah halaman yang tersedia", example = "5")
    private int totalPages;

    @Schema(description = "Indeks halaman saat ini (0-based)", example = "0")
    private int currentPage;

    @Schema(description = "Jumlah data per halaman", example = "10")
    private int pageSize;

    @Schema(description = "Apakah ini halaman pertama", example = "true")
    private boolean isFirst;

    @Schema(description = "Apakah ini halaman terakhir", example = "false")
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
