package com.example.ptitweb.controller;

import com.example.ptitweb.entity.Admin;
import com.example.ptitweb.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/ptit/admins")
    public List<Admin> fetchAdminList() {
        return adminService.fetchAdminList();
    }

    @DeleteMapping("/ptit/admins/{aid}")
    public String deleteUserById(@PathVariable("aid") Long aid){
        adminService.deleteAdminById(aid);
        return "Admin deleted Successfully";
    }

    @PutMapping("/ptit/update/admins/{aid}")
    public Admin updateAdmin(@PathVariable("aid") Long aid, @RequestBody Admin admin) {
        return adminService.updateAdmin(aid, admin);
    }

    @PostMapping("/ptit/register/admins")
    public ResponseEntity<String> createUser(@RequestBody Admin admin) {
        if (adminService.isUsernameExists(admin.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
        }

        if (adminService.isEmailExists(admin.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email doesn't exist");
        }

        if (!isValidUsername(admin.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid username");
        }

        if (!isValidPassword(admin.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password");
        }

        try {
            Admin createdAdmin = adminService.saveAdmin(admin);
            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to sign up");
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

    @PostMapping("/ptit/login/admin")
    public ResponseEntity<String> loginAdmin(@RequestBody Admin admin) {
        boolean isAuthenticated = adminService.authenticateAdmin(admin.getEmail(), admin.getPassword());
        if (isAuthenticated) {
            return ResponseEntity.ok("Admin login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email or password is incorrect");
        }
    }
}

