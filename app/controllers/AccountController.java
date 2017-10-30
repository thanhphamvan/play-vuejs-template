package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Account;
import models.AccountRepository;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

public class AccountController extends Controller {

    private AccountRepository accountRepository;
    private FormFactory formFactory;

    @Inject
    public AccountController(AccountRepository accountRepo, FormFactory formFactory) {
        this.accountRepository = accountRepo;
        this.formFactory = formFactory;
    }

    public Result index() {
        return ok("It works!");
    }

//    public Result login(String username, String hash_password) {
//
//    }

    public Result signUp() {
        Form<SignUp> signUpForm = formFactory.form(SignUp.class).bindFromRequest();

        if (signUpForm.hasErrors()) {
            return badRequest(signUpForm.errorsAsJson());
        }
        SignUp newUser =  signUpForm.get();
        Account existingUser = accountRepository.findByEmail(newUser.email);
        if(existingUser != null) {
            return badRequest(buildJsonResponse("error", "User exists"));
        } else {
            Account account = new Account();
            account.setEmail(newUser.email);
            account.setPassword(newUser.password);
            account.setFullName(newUser.fullName);
            account.save();
            session().clear();
            session("username", newUser.email);

            return ok(buildJsonResponse("success", "User created successfully"));
        }

    }

    public Result login() {
        Form<Login> loginForm = formFactory.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(loginForm.errorsAsJson());
        }
        Login loggingInUser = loginForm.get();
        Account user = accountRepository.findByEmailAndPassword(loggingInUser.email, loggingInUser.password);
        if(user == null) {
            return badRequest(buildJsonResponse("error", "Incorrect email or password"));
        } else {
            session().clear();
            session("username", loggingInUser.email);

            ObjectNode wrapper = Json.newObject();
            ObjectNode msg = Json.newObject();
            msg.put("message", "Logged in successfully");
            msg.put("user", loggingInUser.email);
            wrapper.put("success", msg);
            return ok(wrapper);
        }
    }

    public Result logOut() {
        session().clear();
        return ok(buildJsonResponse("success", "Logged out successfully"));
    }

    public Result isAuthenticated() {
        if(session().get("username") == null) {
            return unauthorized();
        } else {
            ObjectNode wrapper = Json.newObject();
            ObjectNode msg = Json.newObject();
            msg.put("message", "User is logged in already");
            msg.put("user", session().get("username"));
            wrapper.put("success", msg);
            return ok(wrapper);
        }
    }


    public static class UserForm {
        @Constraints.Required
        @Constraints.Email
        public String email;
    }

    public static class SignUp extends UserForm {
        @Constraints.Required
        @Constraints.MinLength(6)
        public String password;
        @Constraints.Required
        public String fullName;
    }

    public static class Login extends UserForm {
        @Constraints.Required
        public String password;
    }

    public static ObjectNode buildJsonResponse(String type, String message) {
        ObjectNode wrapper = Json.newObject();
        ObjectNode msg = Json.newObject();
        msg.put("message", message);
        wrapper.put(type, msg);
        return wrapper;
    }
}
