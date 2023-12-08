package ru.sidey383.minecraftauth.module.password;

import net.kyori.adventure.text.Component;
import ru.sidey383.minecraftauth.CallBack;
import ru.sidey383.minecraftauth.core.AuthorizationModule;
import ru.sidey383.minecraftauth.core.AuthorizationStatus;
import ru.sidey383.minecraftauth.core.RegistrationStatus;
import ru.sidey383.minecraftauth.database.AuthorizationDatabase;
import ru.sidey383.minecraftauth.user.User;

import java.util.Optional;

public class PasswordAuthorizationModule implements AuthorizationModule {

    private AuthorizationDatabase database;

    @Override
    public void registerUser(User user, CallBack<RegistrationStatus> callback) {
        var st = new PasswordRegistrationStatus(callback);
        user.sendMessage(Component.text("Write password"));
    }

    private void registerDialog(User user, PasswordRegistrationStatus status) {
        if (status.getPassword().isPresent()) {
            String password1 = status.getPassword().get();
            user.sendMessage(Component.text("Repeat the password"));
            user.acceptMessage((password2) -> {
                if (password2 == null)
                    return;
                if (password1.equals(password2)) {
                    database.setRecord(user, new PasswordRecord(password1));
                    status.answer(RegistrationStatus.Registered);
                } else {
                    status.setPassword(null);
                    registerDialog(user, status);
                }
            });
        } else {
            user.sendMessage(Component.text("Write new password"));
            user.acceptMessage((password) -> {
                if (password == null)
                    return;
                status.setPassword(password);
                registerDialog(user, status);
            });
        }
    }

    @Override
    public void loginUser(User user, CallBack<AuthorizationStatus> callback) {
        var opt = database.getRecord(user, PasswordAuthorizationModule.class);
        if (opt.isPresent()) {
            user.sendMessage(Component.text("Write password"));
            user.acceptMessage((password) -> {
                if (password == null)
                    return;
                if (opt.get().getData().equals(password)) {
                    callback.apply(AuthorizationStatus.Authorized);
                } else {
                    callback.apply(AuthorizationStatus.NotAuthorized);
                }
            });
        } else {
            callback.apply(AuthorizationStatus.Authorized);
        }

    }

    @Override
    public void abort(User user) {}

    @Override
    public boolean isRegistered(User user) {
        return database.getRecord(user, PasswordAuthorizationModule.class).isPresent();
    }

    @Override
    public void setDatabase(AuthorizationDatabase database) {
        this.database = database;
    }

    private static class PasswordRegistrationStatus {

        private String password;

        private final CallBack<RegistrationStatus> statusCallBack;

        private boolean isAborted = false;

        private PasswordRegistrationStatus(CallBack<RegistrationStatus> statusCallBack) {
            this.statusCallBack = statusCallBack;
        }

        public Optional<String> getPassword() {
            return Optional.ofNullable(password);
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void answer(RegistrationStatus status) {
            statusCallBack.apply(status);
        }

    }

}
