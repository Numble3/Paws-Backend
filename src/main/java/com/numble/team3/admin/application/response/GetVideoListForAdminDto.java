package com.numble.team3.admin.application.response;

import com.numble.team3.video.domain.Video;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class GetVideoListForAdminDto {
  @Schema(description = "내용")
  private List<GetVideoSimpleForAdminDto> videos;

  @Schema(description = "총 합계 크기")
  private Long totalCount;

  @Schema(description = "페이지 크기")
  private int size;

  @Schema(description = "총 페이지 번호")
  private int totalPage;

  @Schema(description = "현재 페이지 번호")
  private int nowPage;

  private GetVideoListForAdminDto(
      List<GetVideoSimpleForAdminDto> contents,
      Long totalSize,
      int size,
      int totalPage,
      int nowPage) {
    this.videos = contents;
    this.totalCount = totalSize;
    this.size = size;
    this.totalPage = totalPage;
    this.nowPage = nowPage;
  }

  public static GetVideoListForAdminDto fromEntities(Page<Video> contents) {
    return GetVideoListForAdminDto.builder()
        .videos(
            contents.getContent().stream()
                .map(GetVideoSimpleForAdminDto::fromEntity)
                .collect(Collectors.toList()))
        .size(contents.getSize())
        .nowPage(contents.getNumber() + 1)
        .totalCount(contents.getTotalElements())
        .totalPage(contents.getTotalPages())
        .build();
  }
}
