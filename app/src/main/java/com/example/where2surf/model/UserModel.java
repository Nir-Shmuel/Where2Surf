package com.example.where2surf.model;

public class UserModel {

    public static final UserModel instance = new UserModel();

    public interface Listener<T>{
        void onComplete(T data);
    }
    public interface CompleteListener {
        void onComplete();
    }

    public void addUser(User user, Listener<Boolean> listener) {
        UserFirebase.addUser(user, listener);
    }

    public void getCurrentUser(Listener<User> listener){
        UserFirebase.getCurrentUser(listener);
    }

}
