package models;

import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Entity
public class Account extends Model {
    @Id
    protected Long id;

    @Column(unique = true, nullable = false)
    @Constraints.MaxLength(255)
    @Constraints.Required
    @Constraints.Email
    protected String email;

    @Column(length = 64, nullable = false)
    private byte[] password;

    @Column(nullable = false)
    protected String fullName;

    public boolean checkPassword(String password) {
        return this.password == getSha512(password);
    }

    public void setPassword(String password) {
        this.password = getSha512(password);
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public void setFullName(String name) {
        this.fullName = name;
    }

    public static byte[] getSha512(String value) {
        try {
            return MessageDigest.getInstance("SHA-512").digest(value.getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}