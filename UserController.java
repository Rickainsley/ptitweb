package com.example.ptitweb.controller;

import com.example.ptitweb.entity.User;
import com.example.ptitweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/ptit/users")
    public List<User> fetchUserList() {
        return userService.fetchUserList();
    }

    @DeleteMapping("/ptit/users/{uid}")
    public String deleteUserById(@PathVariable("uid") Long uid){
        userService.deleteUserById(uid);
        return "User deleted Successfully";
    }

    @PutMapping("/ptit/update/users/{uid}")
    public User updateUser(@PathVariable("uid") Long uid, @RequestBody User user) {
        return userService.updateUser(uid, user);
    }

    @PostMapping("/ptit/register/users")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        if (userService.isUsernameExists(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username đã tồn tại");
        }

        if (userService.isEmailExists(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email đã tồn tại");
        }

        if (!isValidUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username không hợp lệ");
        }

        if (!isValidPassword(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password không hợp lệ");
        }

        try {
            User createdUser = userService.saveUser(user);
            return ResponseEntity.ok("Đăng ký thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể đăng ký người dùng");
        }
    }

    private boolean isValidUsername(String username) {
        if (username.length() < 8) {
            return false;
        }
        if (!username.matches("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$")) {
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]+$")) {
            return false;
        }
        return true;
    }

    @PostMapping("/ptit/login/user")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        boolean isAuthenticated = userService.authenticateUser(user.getEmail(), user.getPassword());
        if (isAuthenticated) {
            return ResponseEntity.ok("Đăng nhập thành công vào trang chủ user");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email hoặc mật khẩu không đúng");
        }
    }
}
