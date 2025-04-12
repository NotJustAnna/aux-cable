package net.notjustanna.services;

import io.micronaut.core.type.Argument;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Singleton;
import net.notjustanna.models.internal.Account;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Singleton
public class AccountService {
    private final ObjectMapper mapper;
    private final List<Account> accounts;

    public AccountService(ObjectMapper mapper) {
        this.mapper = mapper;
        this.accounts = new ArrayList<>();
        this.read();
    }

    public List<Account> all() {
        return accounts;
    }

    public Account getById(String id) {
        return accounts.stream()
            .filter(account -> account.id().equals(id))
            .findFirst()
            .orElse(null);
    }

    public void save(String token, String id, String name, String imageUrl) {
        Account account = new Account(id, name, imageUrl, token);
        accounts.removeIf(a -> a.id().equals(id));
        accounts.add(account);
        accounts.sort(Comparator.comparing(Account::id));
        this.write();
    }

    public void update(String id, String name, String imageUrl) {
        Account account = getById(id);
        if (account == null) {
            return;
        }
        if (account.name().equals(name) && account.imageUrl().equals(imageUrl)) {
            return;
        }
        accounts.remove(account);
        accounts.add(new Account(id, name, imageUrl, account.token()));
        accounts.sort(Comparator.comparing(Account::id));
        this.write();
    }

    public boolean deleteById(String id) {
        Account account = getById(id);
        if (account == null) {
            return false;
        }
        accounts.remove(account);
        this.write();
        return true;
    }

    private void read() {
        File file = new File("remember_me.json");
        if (!file.exists() || file.length() == 0) {
            return;
        }

        try (InputStream is = new FileInputStream(file)) {
            List<Account> loaded = mapper.readValue(is, Argument.listOf(Account.class));

            accounts.clear();
            accounts.addAll(loaded);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void write() {
        File file = new File("remember_me.json");

        try (OutputStream os = new FileOutputStream(file)) {
            mapper.writeValue(os, accounts);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
