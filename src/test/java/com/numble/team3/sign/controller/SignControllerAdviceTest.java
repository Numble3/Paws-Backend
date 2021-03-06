package com.numble.team3.sign.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numble.team3.exception.account.AccountNotFoundException;
import com.numble.team3.exception.account.AccountWithdrawalException;
import com.numble.team3.exception.sign.SignInFailureException;
import com.numble.team3.exception.sign.TokenFailureException;
import com.numble.team3.factory.dto.SignDtoFactory;
import com.numble.team3.sign.application.SignService;
import com.numble.team3.sign.application.advice.SignRestControllerAdvice;
import com.numble.team3.sign.application.request.SignInDto;
import com.numble.team3.sign.application.request.SignUpDto;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("SignRestControllerAdvice ?????????")
public class SignControllerAdviceTest {

  @Mock
  SignService signService;

  @InjectMocks
  SignController signController;

  ObjectMapper objectMapper = new ObjectMapper();
  MockMvc mockMvc;

  @BeforeEach
  void beforeEach() {
    mockMvc = MockMvcBuilders
      .standaloneSetup(signController)
      .setControllerAdvice(new SignRestControllerAdvice())
      .addFilter(new CharacterEncodingFilter("UTF-8", true))
      .alwaysDo(print())
      .build();
  }

  @Test
  void signUp_email_??????_??????_?????????() throws Exception {
    // given
    SignUpDto dto = SignDtoFactory.createSignUpDto(null, "1234", "nickname");

    // when
    ResultActions result = mockMvc.perform(
      post("/api/sign-up")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)));

    // then
    result
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("???????????? ??????????????????."));
  }

  @Test
  void signUp_email_??????_??????_?????????() throws Exception {
    // given
    SignUpDto dto = SignDtoFactory.createSignUpDto("????????? ?????? ??????", "1234", "nickname");

    // when
    ResultActions result = mockMvc.perform(
      post("/api/sign-up")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)));

    // then
    result
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("????????? ???????????? ??????????????????."));
  }

  @Test
  void signUp_nickname_??????_??????_?????????() throws Exception {
    // given
    SignUpDto dto = SignDtoFactory.createSignUpDto("test@email.com", "1234", null);

    // when
    ResultActions result = mockMvc.perform(
      post("/api/sign-up")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)));

    // then
    result
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("???????????? ??????????????????."));
  }

  @Test
  void signUp_password_??????_??????_?????????() throws Exception {
    // given
    SignUpDto dto = SignDtoFactory.createSignUpDto("test@email.com", null, "?????????");

    // when
    ResultActions result = mockMvc.perform(
      post("/api/sign-up")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)));

    // then
    result
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("??????????????? ??????????????????."));
  }

  @Test
  void signIn_email_??????_??????_?????????() throws Exception {
    // given
    SignInDto dto = SignDtoFactory.createSignInDto(null, "1234");

    // when
    ResultActions result = mockMvc.perform(
      post("/api/sign-in")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)));

    // then
    result
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("???????????? ??????????????????."));
  }

  @Test
  void signIn_password_??????_??????_?????????() throws Exception {
    // given
    SignInDto dto = SignDtoFactory.createSignInDto("test@email.com", null);

    // when
    ResultActions result = mockMvc.perform(
      post("/api/sign-in")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)));

    // then
    result
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("??????????????? ??????????????????."));
  }

  @Test
  void signIn_??????_??????_??????_?????????() throws Exception {
    // given
    SignInDto dto = SignDtoFactory.createSignInDto("test@email.com", "1234");

    given(signService.signIn(any(SignInDto.class))).willThrow(new SignInFailureException());

    // when
    ResultActions result = mockMvc.perform(
      post("/api/sign-in")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)));

    // then
    result
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.message").value("???????????? ??????????????????."));
  }

  @Test
  void signIn_?????????_??????_??????_?????????() throws Exception {
    // given
    SignInDto dto = SignDtoFactory.createSignInDto("test@email.com", "1234");

    given(signService.signIn(any(SignInDto.class))).willThrow(new AccountWithdrawalException());

    // when
    ResultActions result = mockMvc.perform(
      post("/api/sign-in")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)));

    // then
    result
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("?????? ???????????? ??? ???????????????."));
  }

  @Test
  void logout_??????_??????_??????_??????_?????????() throws Exception {
    // given

    // when
    ResultActions result = mockMvc.perform(
      get("/api/logout"));

    // then
    result
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Authorization ????????? ?????????????????????."));
  }

  @Test
  void logout_??????_??????_??????_?????????() throws Exception {
    // given
    willThrow(new TokenFailureException()).given(signService).logout(anyString());

    // when
    ResultActions result = mockMvc.perform(
      get("/api/logout").header("Authorization", "Bearer accessToken"));

    // then
    result
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.message").value("???????????? ?????? ???????????????."));
  }

  @Test
  void createAccessTokenByRefreshToken_??????_??????_??????_??????_?????????() throws Exception {
    // given

    // when
    ResultActions result = mockMvc.perform(
      get("/api/refresh-token"));

    // then
    result
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Authorization ????????? ?????????????????????."));
  }

  @Test
  void createAccessTokenByRefreshToken_??????_??????_??????_?????????() throws Exception {
    // given
    given(signService.createAccessTokenByRefreshToken(anyString())).willThrow(new TokenFailureException());

    // when
    ResultActions result = mockMvc.perform(
      get("/api/refresh-token").header("Authorization", "Bearer accessToken"));

    // then
    result
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.message").value("???????????? ?????? ???????????????."));
  }

  @Test
  void accountWithdrawal_??????_??????_??????_??????_?????????() throws Exception {
    // given

    // when
    ResultActions result = mockMvc.perform(
      delete("/api/withdrawal"));

    // then
    result
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Authorization ????????? ?????????????????????."));
  }

  @Test
  void accountWithdrawal_??????_??????_??????_?????????() throws Exception {
    // given
    willThrow(new TokenFailureException()).given(signService).withdrawal(anyString());

    // when
    ResultActions result = mockMvc.perform(
      delete("/api/withdrawal")
        .header("Authorization", "Bearer accessToken"));

    // then
    result
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.message").value("???????????? ?????? ???????????????."));
  }

  @Test
  void accountWithdrawal_??????_??????_??????_?????????() throws Exception {
    // given
    willThrow(new AccountNotFoundException()).given(signService).withdrawal(anyString());

    // when
    ResultActions result = mockMvc.perform(
      delete("/api/withdrawal").header("Authorization", "Bearer accessToken"));

    // then
    result
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("???????????? ?????? ??????????????????."));
  }
}

