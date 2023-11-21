package com.mojh.dailybudget.budget.controller;

import com.mojh.dailybudget.auth.domain.LoginMember;
import com.mojh.dailybudget.budget.dto.BudgetPutRequest;
import com.mojh.dailybudget.budget.service.BudgetService;
import com.mojh.dailybudget.common.web.ApiResponse;
import com.mojh.dailybudget.common.web.PutResult;
import com.mojh.dailybudget.common.web.dto.PutResultResponse;
import com.mojh.dailybudget.member.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api")
public class BudgetController {

    private BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PutMapping("/budgets")
    public ResponseEntity<?> putBudget(@RequestBody @Valid final BudgetPutRequest request,
                                       @LoginMember Member member) {
        PutResultResponse putResultResponse = budgetService.putBudget(request, member);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{id}")
                                                  .buildAndExpand(putResultResponse.getEntityId())
                                                  .toUri();

        // create
        if (putResultResponse.getPutResult() == PutResult.CREATED) {
            return ResponseEntity.created(location)
                                 .build();
        }

        // replace
        return ResponseEntity.noContent()
                             .location(location)
                             .build();
    }

}
