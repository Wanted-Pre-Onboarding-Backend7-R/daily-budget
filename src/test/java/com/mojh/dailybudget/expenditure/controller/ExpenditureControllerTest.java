package com.mojh.dailybudget.expenditure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojh.dailybudget.auth.JwtFixture;
import com.mojh.dailybudget.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static com.mojh.dailybudget.common.exception.ErrorCode.EXPENDITURE_MEMBER_MISMATCH;
import static com.mojh.dailybudget.common.exception.ErrorCode.EXPENDITURE_NOT_FOUND;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql("/db/member-expenditure.sql")
@DisplayName("지출 API 통합 테스트")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ExpenditureControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private final String url = "/api/expenditures";
    private final String deleteURL = url + "/{expenditureId}";

    @Test
    @DisplayName("지출 삭제 성공")
    void deleteExpenditure() throws Exception {
        // given
        Long expenditrueId = 1L;
        String accessToken = JwtFixture.ACCESS_TOKEN_MEMBER1;

        // when, then
        mockMvc.perform(delete(deleteURL, expenditrueId)
                       .header(AUTHORIZATION, accessToken))
               .andDo(print())
               .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("존재하지 않은 지출 정보를 삭제할 때 EXPENDITURE_NOT_FOUND 예외가 발생한다.")
    void deleteExpenditure_throw_expenditureNotFound() throws Exception {
        // given
        Long expenditrueId = 2342345L;
        String accessToken = JwtFixture.ACCESS_TOKEN_MEMBER1;
        ErrorCode expected = EXPENDITURE_NOT_FOUND;

        // when, then
        mockMvc.perform(delete(deleteURL, expenditrueId)
                       .header(AUTHORIZATION, accessToken))
               .andDo(print())
               .andExpect(status().isNotFound())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.response").doesNotExist())
               .andExpect(jsonPath("$.error.code").value(expected.getCode()))
               .andExpect(jsonPath("$.error.message").value(expected.getMessage()));
    }

    @Test
    @DisplayName("내가 작성하지 않은 다른 유저의 지출 정보를 삭제할 때 EXPENDITURE_MEMBER_MISMATCH 예외가 발생한다.")
    void deleteExpenditure_throw_expenditureMemberMismatch() throws Exception {
        // given: member1이 만든 지출 정보를 member2가 삭제하도록 설정
        Long expenditrueId = 1L;
        String accessTokenMember2 = JwtFixture.ACCESS_TOKEN_MEMBER2;
        ErrorCode expected = EXPENDITURE_MEMBER_MISMATCH;

        // when, then
        mockMvc.perform(delete(deleteURL, expenditrueId)
                       .header(AUTHORIZATION, accessTokenMember2))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.response").doesNotExist())
               .andExpect(jsonPath("$.error.code").value(expected.getCode()))
               .andExpect(jsonPath("$.error.message").value(expected.getMessage()));
    }

}