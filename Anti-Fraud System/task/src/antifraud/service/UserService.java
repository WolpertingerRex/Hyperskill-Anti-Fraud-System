package antifraud.service;

import antifraud.security.Role;
import antifraud.business.User;
import antifraud.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder encoder;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private boolean exists(String username) {
        Optional<User> fromDb = repository.findUserByUsernameIgnoreCase(username);
        return fromDb.isPresent();
    }

    public User createUser(User user) {
        if (exists(user.getUsername())) throw new ResponseStatusException(HttpStatus.CONFLICT);
        user.setPassword(encoder.encode(user.getPassword()));
        if(getaAllUsers().isEmpty()) {
            user.setRole(Role.ADMINISTRATOR);
            user.setNotLocked(true);
        }

        else {
            user.setRole(Role.MERCHANT);
        }

        return repository.save(user);
    }

    public User changeRole(String username, String role){
        if (!exists(username)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, username + " not found");
        if(!role.equals(Role.SUPPORT.name()) && !role.equals(Role.MERCHANT.name())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no role " + role);
        User user = loadUserByUsername(username);
        if(user.getRole().equals(role)) throw new ResponseStatusException(HttpStatus.CONFLICT, username + " already has the role " + role);
        user.setRole(Role.valueOf(role));
       // user.setNotLocked(true);
        return repository.save(user);
    }

    public User lockUser(String username){
        if (!exists(username)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, username + " not found");
        User user = loadUserByUsername(username);
        if (user.getRole().equals(Role.ADMINISTRATOR.name())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Administrator can't be locked");
        user.setNotLocked(false);
        return repository.save(user);
    }

    public User unlockUser(String username){
        if (!exists(username)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, username + " not found");
        User user = loadUserByUsername(username);
        user.setNotLocked(true);
        return repository.save(user);
    }

    public void deleteUser(String username) {
        if (!exists(username)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        User toDelete = loadUserByUsername(username);
        repository.delete(toDelete);
    }

    public List<User> getaAllUsers() {
        Iterable<User> all = repository.findAll();
        return StreamSupport.stream(all.spliterator(), false).collect(Collectors.toList());
    }
}
