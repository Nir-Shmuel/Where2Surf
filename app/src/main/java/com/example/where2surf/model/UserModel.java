package com.example.where2surf.model;

public class UserModel {

    public static final UserModel instance = new UserModel();

    public void updateUserDetails(String firstName, String lastName, UserModel.Listener<Boolean> listener) {
        UserFirebase.updateUserDetails(firstName, lastName, listener);
    }

    public void setUserImageUrl(String url, UserModel.Listener<Boolean> listener) {
        UserFirebase.setUserImageUrl(url, listener);
    }

    public interface Listener<T> {
        void onComplete(T data);
    }

    public interface CompleteListener {
        void onComplete();
    }

    public void addUser(User user, Listener<Boolean> listener) {
        UserFirebase.addUser(user, listener);
    }

    public void getCurrentUserDetails(Listener<User> listener) {
        UserFirebase.getCurrentUserDetails(listener);
    }

    public String getCurrentUserId() {
        return UserFirebase.getCurrentUserId();
    }

    public void signIn(String email, String password, Listener<Boolean> listener) {
        UserFirebase.signIn(email, password, listener);
    }

    public void signUp(String email, String password, Listener<Boolean> listener) {
        UserFirebase.singUp(email, password, listener);
    }

    public void signOut() {
        UserFirebase.signOut();
    }

    public boolean isLoggedIn() {
        return UserFirebase.isSignedIn();
    }

}
