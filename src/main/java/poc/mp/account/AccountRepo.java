package poc.mp.account;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccountRepo {
    private ConcurrentMap<String, Account> repo = new ConcurrentHashMap<>();
    private AtomicInteger idGenerator = new AtomicInteger(0);

    private String getNextId() {
        return String.format("%04d", idGenerator.incrementAndGet());
    }

    public String add(Account account) {
        String id = getNextId();
        account.setId(id);
        repo.put(id, account);
        return id;
    }

    public Account getById(String id) {
        return repo.get(id);
    }

    public List<Account> getByType(String type) {
        List<Account> accounts = repo.values().stream().filter(account -> account.getType().equals(type))
                .collect(Collectors.toList());
        return accounts;
    }

    public List<Account> getAll() {
        List<Account> accounts = new ArrayList<>();
        accounts.addAll(repo.values());
        return accounts;
    }
}