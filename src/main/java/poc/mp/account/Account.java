package poc.mp.account;

import java.util.Objects;

public class Account {
    private String id;
    private String type;
    private String userId;

    public Account(String type, String userId) {
        this.type = type;
        this.userId = userId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Account id(String id) {
        this.id = id;
        return this;
    }

    public Account type(String type) {
        this.type = type;
        return this;
    }

    public Account userId(String userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Account)) {
            return false;
        }
        Account account = (Account) o;
        return Objects.equals(id, account.id) && Objects.equals(type, account.type)
                && Objects.equals(userId, account.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, userId);
    }

    @Override
    public String toString() {
        return "{" + " id='" + getId() + "'" + ", type='" + getType() + "'" + ", userId='" + getUserId() + "'" + "}";
    }

    public Account() {
    }

}