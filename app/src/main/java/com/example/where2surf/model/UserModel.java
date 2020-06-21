package com.example.where2surf.model;

import androidx.lifecycle.LiveData;

public class UserModel {

    public static final UserModel instance = new UserModel();

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

    public void signIn(String email, String password, Listener<Boolean> listener){
        UserFirebase.signIn(email,password,listener);
    }
    public void signUp(String email, String password, Listener<Boolean> listener){
        UserFirebase.singUp(email,password,listener);
    }
    public void signOut(){
        UserFirebase.signOut();
    }
    public boolean isSignedIn() {
        return UserFirebase.isSignedIn();
    }

}
