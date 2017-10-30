package models;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Model;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;

public class AccountRepository {

    private final EbeanServer ebeanServer;

    @Inject
    public AccountRepository(EbeanConfig ebeanConfig) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
    }

    public Account findByEmail(String email) {
        return ebeanServer.find(Account.class)
                .where()
                .eq("email", email.toLowerCase())
                .findUnique();
    }

    public Account findByEmailAndPassword(String email, String password) {
        return ebeanServer.find(Account.class)
                .where()
                .eq("email", email.toLowerCase())
                .eq("password", Account.getSha512(password))
                .findUnique();
    }

    public Long deleteByEmail(String email) {
        try {
            final Optional<Account> accountOptional = Optional.ofNullable(
                    ebeanServer.find(Account.class).where().eq("email", email.toLowerCase()).findUnique()
            );
            accountOptional.ifPresent(Model::delete);
            return accountOptional.map(acc -> acc.id).get();
        } catch (Exception e) {
            return Long.parseLong("-1");
        }
    }

}
