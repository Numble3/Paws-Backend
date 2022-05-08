package com.numble.team3.like.controller;

import com.numble.team3.account.annotation.LoginUser;
import com.numble.team3.account.resolver.UserInfo;
import com.numble.team3.like.annotation.AddLikeSwagger;
import com.numble.team3.like.annotation.DeleteLikeSwagger;
import com.numble.team3.like.annotation.GetAllLikesSwagger;
import com.numble.team3.like.annotation.GetLikesByCategorySwagger;
import com.numble.team3.like.annotation.GetRankByDaySwagger;
import com.numble.team3.like.application.LikeService;
import com.numble.team3.like.application.response.GetAllLikeListDto;
import com.numble.team3.like.application.response.GetVideoRankDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Api(tags = {"관심영상 조회, 랭킹, 좋아요 추가, 좋아요 삭제"})
public class LikeController {

  private final LikeService likeService;

  @AddLikeSwagger
  @PostMapping(value = "/likes/add", produces = "application/json")
  public ResponseEntity addLike(
    @ApiIgnore @LoginUser UserInfo userInfo,
    @ApiParam(value = "비디오 id", required = true) @RequestParam(name = "id") Long videoId) {
      likeService.addLike(userInfo, videoId);
      return new ResponseEntity(HttpStatus.CREATED);
  }

  @DeleteLikeSwagger
  @DeleteMapping(value = "/likes/delete", produces = "application/json")
  public ResponseEntity deleteLike(@ApiParam(value = "좋아요 id", required = true) @RequestParam(name = "id") Long likeId) {
    likeService.deleteLike(likeId);
    return new ResponseEntity(HttpStatus.OK);
  }

  @GetAllLikesSwagger
  @GetMapping(value = "/likes/all", produces = "application/json")
  public ResponseEntity<GetAllLikeListDto> getLikesHierarchy(@ApiIgnore @LoginUser UserInfo userInfo) {
    return ResponseEntity.ok(likeService.getLikesHierarchy(userInfo));
  }

  @GetLikesByCategorySwagger
  @GetMapping(value = "/likes", produces = "application/json")
  public ResponseEntity getLikesByCategory(
    @ApiIgnore @LoginUser UserInfo userInfo,
    @ApiParam(value = "카테고리 이름", required = true) @RequestParam(name = "category") String categoryName,
    @ApiParam(value = "like id, 2페이지 이후 조회를 위한 값", required = false) @RequestParam(required = false, name = "id") Long likeId,
    @ApiParam(value = "페이지 크기", required = true) @RequestParam(name = "size") int size) {
      return ResponseEntity.ok(likeService.getLikesByCategory(userInfo, categoryName, likeId, size));
  }

  @GetRankByDaySwagger
  @GetMapping(value = "/likes/rank/day", produces = "application/json")
  public ResponseEntity<Map> getRankByDay() {
    return new ResponseEntity(new HashMap<String, List<GetVideoRankDto>>() {
      {
        put("ranking", likeService.getRank("day"));
      }
    }, HttpStatus.OK);
  }
}