package com.mojh.dailybudget.expenditure.controller;

import com.mojh.dailybudget.auth.domain.LoginMember;
import com.mojh.dailybudget.common.web.ApiResponse;
import com.mojh.dailybudget.expenditure.dto.request.ExpenditureCreateRequest;
import com.mojh.dailybudget.expenditure.dto.request.ExpenditureListRetrieveRequest;
import com.mojh.dailybudget.expenditure.dto.response.ExpenditureListResponse;
import com.mojh.dailybudget.expenditure.dto.request.ExpenditureUpdateRequest;
import com.mojh.dailybudget.expenditure.dto.response.ExpenditureResponse;
import com.mojh.dailybudget.expenditure.service.ExpenditureService;
import com.mojh.dailybudget.member.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/expenditures")
public class ExpenditureController {

    private final ExpenditureService expenditureService;

    public ExpenditureController(final ExpenditureService expenditureService) {
        this.expenditureService = expenditureService;
    }

    @PostMapping
    public ResponseEntity createExpenditure(@RequestBody @Valid final ExpenditureCreateRequest request,
                                            @LoginMember final Member member,
                                            HttpServletRequest httpServletRequest) {
        Long id = expenditureService.createExpenditure(request, member);
        URI location = URI.create(String.format("%s/%d", httpServletRequest.getRequestURI(), id));

        return ResponseEntity.created(location).build();
    }

    @GetMapping("{expenditureId}")
    public ApiResponse<ExpenditureResponse> retrieveExpenditure(@LoginMember final Member member,
                                                                @PathVariable final Long expenditureId) {
        ExpenditureResponse response = expenditureService.retrieveExpenditure(member, expenditureId);
        return ApiResponse.succeed(response);
    }

    @GetMapping
    public ApiResponse<ExpenditureListResponse> retrieveExpenditureList(
            @ModelAttribute @Valid final ExpenditureListRetrieveRequest request, @LoginMember final Member member) {
        ExpenditureListResponse response = expenditureService.retrieveExpenditureList(request, member);
        return ApiResponse.succeed(response);
    }

    @PatchMapping("/{expenditureId}")
    public ApiResponse<Boolean> updateExpenditure(@RequestBody @Valid final ExpenditureUpdateRequest request,
                                                  @LoginMember final Member member,
                                                  @PathVariable final Long expenditureId) {
        Boolean updated = expenditureService.updateExpenditure(request, member, expenditureId);
        return ApiResponse.succeed(updated);
    }

    @DeleteMapping("/{expenditureId}")
    public ResponseEntity deleteExpenditure(@LoginMember final Member member,
                                            @PathVariable final Long expenditureId) {
        expenditureService.deleteExpenditure(member, expenditureId);
        return ResponseEntity.noContent().build();
    }

}
