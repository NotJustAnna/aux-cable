package net.notjustanna.controllers;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.annotation.*;
import net.notjustanna.exceptions.Exceptions;
import net.notjustanna.models.http.AccountModel;
import net.notjustanna.models.internal.Account;
import net.notjustanna.services.AccountService;

import java.util.List;

@Controller("/api/accounts")
public class AccountController {
    @NonNull
    private final AccountService accountService;

    public AccountController(@NonNull AccountService accountService) {
        this.accountService = accountService;
    }

    @NonNull
    @Get
    @Produces("application/json")
    public List<AccountModel> all() {
        return accountService.all().stream().map(Account::toModel).toList();
    }

    @NonNull
    @Get("/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public AccountModel get(@Body @NonNull String id) {
        Account account = accountService.getById(id);
        if (account == null) {
            return Exceptions.noSuchAccount();
        }
        return account.toModel();
    }

    @NonNull
    @Delete("/{id}")
    @Produces("application/json")
    public boolean delete(@NonNull String id) {
        return accountService.deleteById(id);
    }
}
