package com.mojh.dailybudget.expenditure.controller;

import com.mojh.dailybudget.auth.domain.LoginMember;
import com.mojh.dailybudget.budget.dto.BudgetPutRequest;
import com.mojh.dailybudget.expenditure.dto.ExpenditureCreateRequest;
import com.mojh.dailybudget.expenditure.service.ExpenditureService;
import com.mojh.dailybudget.member.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
                                            @LoginMember final Member member) {
        Long id = expenditureService.createExpenditure(request, member);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{id}")
                                                  .buildAndExpand(id)
                                                  .toUri();

        return ResponseEntity.created(location)
                             .build();
    }


}
