package com.numble.team3.like.annotation;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiOperation(value = "관심영상 카테고리 별 조회")
@ApiResponses(
  value = {
    @ApiResponse(
      code = 200,
      message = "관심영상 카테고리 별 조회 성공",
      examples = @Example(@ExampleProperty(mediaType = "application/json", value = "{\n\"likes\" : [ \n\t { \n\t\t \"id\" : 좋아요 id, \n\t\t \"createdAt\" : \"좋아요 누른 날짜(yyyy-MM-dd HH:mm)\", \n\t\t \"getVideoDto\" : { \n\t\t\t \"videoId\" : 영상 ID, \n\t\t\t \"thumbnailPath\" : \"썸네일 경로\", \n\t\t\t \"profileUrl\" : \"업로드 유저 프로필 사진 경로\", \n\t\t\t \"title\" : \"영상 제목\", \n\t\t\t \"nickname\" : \"비디오 업로더 닉네임\", \n\t\t\t \"view\" : 조회수, \n\t\t\t \"like\" : 좋아요, \n\t\t\t \"category\" : \"영상 카테고리\", \n\t\t\t \"videoType\" : \"영상 타입\", \n\t\t\t \"createdAt\", \"영상 업로드 날짜(yyyy-MM-dd)\" \n\t\t } \n\t } \n\t], \n \"lastLikeId\" : 다음 페이지 요청을 위한 likeId \n}"))
    ),
    @ApiResponse(
      code = 401,
      message = "관심영상 조회 실패 \t\n 1. access token이 유효하지 않음",
      examples = @Example(@ExampleProperty(mediaType = "application/json", value = "{}"))
    )
  }
)
@ApiImplicitParams(
  value = {
    @ApiImplicitParam(
      name = "Authorization",
      value = "access token",
      required = true,
      dataTypeClass = String.class,
      paramType = "header"
    ),
  }
)
public @interface GetLikeVideosByCategorySwagger {

}
