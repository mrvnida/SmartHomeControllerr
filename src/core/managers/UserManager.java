package core.managers;

import core.User;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private final List<User> registeredUsers;

    public UserManager() {
        this.registeredUsers = new ArrayList<>();
    }

    public void registerUser(User user) {
        registeredUsers.add(user);
        System.out.println("User " + user.getName() + " registered");
    }

    public void removeUser(User user) {
        registeredUsers.remove(user);
        System.out.println("User " + user.getName() + " removed");
    }

    public List<User> getRegisteredUsers() {
        return new ArrayList<>(registeredUsers);
    }
} 