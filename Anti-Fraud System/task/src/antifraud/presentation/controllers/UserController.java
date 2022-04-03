package antifraud.presentation.controllers;

import antifraud.business.User;
import antifraud.presentation.requests.ChangeRoleRequest;
import antifraud.presentation.requests.LockUnlockRequest;
import antifraud.presentation.requests.Operation;
import antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("user")
    public ResponseEntity<User> createUser(@RequestBody @Valid User user){
        User newUser = userService.createUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("list")
    public List<User> getAllUsers(){
        return userService.getaAllUsers();
    }

    @DeleteMapping("user/{username}")
    public Map<String, String> deleteUser(@PathVariable String username){
        userService.deleteUser(username);
        return Map.of("username", username, "status", "Deleted successfully!");
    }

    @PutMapping("role")
    public User assignRole(@RequestBody @Valid ChangeRoleRequest request){
       return userService.changeRole(request.getUsername(), request.getRole());
    }

    @PutMapping("access")
    public  Map<String, String> lockOrUnlock(@RequestBody @Valid LockUnlockRequest request){
        if(request.getOperation().equals(Operation.LOCK.name())) {
            userService.lockUser(request.getUsername());
            return Map.of("status", "User " + request.getUsername() + " locked!");
        }
        if(request.getOperation().equals(Operation.UNLOCK.name())) {
            userService.unlockUser(request.getUsername());
            return Map.of("status", "User " + request.getUsername() + " unlocked!");
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "wrong operation");
    }
}
