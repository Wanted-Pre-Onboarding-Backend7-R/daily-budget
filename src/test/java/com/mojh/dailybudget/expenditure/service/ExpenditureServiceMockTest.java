package com.mojh.dailybudget.expenditure.service;

import com.mojh.dailybudget.category.domain.Category;
import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.category.service.CategorySerivce;
import com.mojh.dailybudget.common.exception.DailyBudgetAppException;
import com.mojh.dailybudget.common.exception.ErrorCode;
import com.mojh.dailybudget.expenditure.domain.Expenditure;
import com.mojh.dailybudget.expenditure.dto.ExpenditureUpdateRequest;
import com.mojh.dailybudget.expenditure.repository.ExpenditureRepository;
import com.mojh.dailybudget.member.domain.Member;
import com.mojh.dailybudget.member.fixture.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("지출 서비스 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ExpenditureServiceMockTest {

    @Mock
    ExpenditureRepository expenditureRepository;

    @Mock
    CategorySerivce categorySerivce;

    @InjectMocks
    ExpenditureService expenditureService;

    Member member;
    Category category;
    Expenditure oldExpenditure;

    @BeforeEach
    void setup() {
        member = MemberFixture.MEMBER1();
        category = new Category(CategoryType.FOOD);
        oldExpenditure = Expenditure.builder()
                                    .member(member)
                                    .category(category)
                                    .amount(20000L)
                                    .memo("BBQ 치킨")
                                    .excludeFromTotal(false)
                                    .expenditureAt(LocalDateTime.of(2023, 11, 20, 20, 30))
                                    .build();

        ReflectionTestUtils.setField(oldExpenditure, "id", 1L);
    }

    private Expenditure captureExpenditure(Expenditure expenditure) {
        return Expenditure.builder()
                          .category(expenditure.getCategory())
                          .amount(expenditure.getAmount())
                          .memo(expenditure.getMemo())
                          .excludeFromTotal(expenditure.getExcludeFromTotal())
                          .expenditureAt(expenditure.getExpenditureAt())
                          .build();
    }

    @Test
    @DisplayName("지출 정보를 수정할 때 수정 요청한 필드만 변경한다.")
    void updateExpenditure_patch_true() {
        // given: 카테고리, 금액, 메모 변경 요청하도록 설정, 카테고리는 기존 값과 동일
        Long expenditureId = oldExpenditure.getId();
        Category capturedCategory = oldExpenditure.getCategory();
        boolean capturedExcludeFromTotal = oldExpenditure.getExcludeFromTotal();
        LocalDateTime capturedExpenditureAt = oldExpenditure.getExpenditureAt();
        ExpenditureUpdateRequest requet = ExpenditureUpdateRequest.builder()
                                                                  .category(CategoryType.FOOD)
                                                                  .amount(25000L)
                                                                  .memo("BBQ 치킨과 치즈볼")
                                                                  .build();
        given(categorySerivce.findByCategoryType(requet.getCategory())).willReturn(category);
        given(expenditureRepository.findById(expenditureId)).willReturn(Optional.of(oldExpenditure));

        // when
        boolean result = expenditureService.updateExpenditure(requet, member, expenditureId);

        // then: category는 기존의 값과 같았고, 지출 일시와 합계 제외는 수정 요청을 하지 않아서 변경되지 않는다.
        // 수정 요청한 금액과 메모만 변경됨.
        assertAll(
                () -> assertThat(result).isTrue(),
                () -> assertThat(oldExpenditure.getCategory()).isEqualTo(capturedCategory),
                () -> assertThat(oldExpenditure.getAmount()).isEqualTo(requet.getAmount()),
                () -> assertThat(oldExpenditure.getMemo()).isEqualTo(requet.getMemo()),
                () -> assertThat(oldExpenditure.getExcludeFromTotal()).isEqualTo(capturedExcludeFromTotal),
                () -> assertThat(oldExpenditure.getExpenditureAt()).isEqualTo(capturedExpenditureAt),
                () -> verify(categorySerivce).findByCategoryType(requet.getCategory()),
                () -> verify(expenditureRepository).findById(expenditureId)
        );
    }

    @Test
    @DisplayName("지출 정보를 수정할 때 변경할 필드가 없으면 요청이 실패한 것은 아니나 patch 결과는 false.")
    void updateExpenditure_patch_false() {
        // given: 카테고리 수정 요청을 했지만 기존의 값과 같을 때 변경할 필드 없음
        Long expenditureId = oldExpenditure.getId();
        Expenditure captured = captureExpenditure(oldExpenditure);
        ExpenditureUpdateRequest requet = ExpenditureUpdateRequest.builder()
                                                                  .category(CategoryType.FOOD)
                                                                  .build();
        given(categorySerivce.findByCategoryType(requet.getCategory())).willReturn(category);
        given(expenditureRepository.findById(expenditureId)).willReturn(Optional.of(oldExpenditure));

        // when
        boolean result = expenditureService.updateExpenditure(requet, member, expenditureId);

        // then: 카테고리는 기존 값과 변경 요청값이 동일하고, 이외의 필드는 수정 요청을 하지 않아 변경한 필드가 없음
        assertAll(
                () -> assertThat(result).isFalse(),
                () -> assertThat(oldExpenditure.getCategory()).isEqualTo(captured.getCategory()),
                () -> assertThat(oldExpenditure.getAmount()).isEqualTo(captured.getAmount()),
                () -> assertThat(oldExpenditure.getMemo()).isEqualTo(captured.getMemo()),
                () -> assertThat(oldExpenditure.getExcludeFromTotal()).isEqualTo(captured.getExcludeFromTotal()),
                () -> assertThat(oldExpenditure.getExpenditureAt()).isEqualTo(captured.getExpenditureAt()),
                () -> verify(categorySerivce).findByCategoryType(requet.getCategory()),
                () -> verify(expenditureRepository).findById(expenditureId)
        );
    }

    @Test
    @DisplayName("내가 작성하지 않은 다른 사람의 지출 정보를 수정할 때 EXPENDITURE_MEMBER_MISMATCH 예외가 발생한다.")
    void updateExpenditure_throw_expenditureMemberMismatch() {
        // given:
        Long expenditureId = oldExpenditure.getId();
        Member otherMember = MemberFixture.MEMBER2();
        ExpenditureUpdateRequest requet = ExpenditureUpdateRequest.builder()
                                                                  .category(CategoryType.FOOD)
                                                                  .amount(30000L)
                                                                  .build();
        given(categorySerivce.findByCategoryType(requet.getCategory())).willReturn(category);
        given(expenditureRepository.findById(expenditureId)).willReturn(Optional.of(oldExpenditure));

        // when
        DailyBudgetAppException ex = assertThrows(DailyBudgetAppException.class, () -> {
            expenditureService.updateExpenditure(requet, otherMember, expenditureId);
        });

        // then
        assertAll(
                () -> verify(categorySerivce).findByCategoryType(requet.getCategory()),
                () -> verify(expenditureRepository).findById(expenditureId),
                () -> assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EXPENDITURE_MEMBER_MISMATCH)
        );
    }
}