package antifraud.service;

import antifraud.exception.AdminAlreadyExistsException;
import antifraud.exception.AdminChangeAccessException;
import antifraud.exception.AdminChangeRoleException;
import antifraud.exception.RoleAlreadySetException;
import antifraud.model.Role;
import antifraud.model.Status;
import antifraud.model.User;
import antifraud.model.UserDetailsImpl;
import antifraud.model.request.ChangeRoleRequest;
import antifraud.model.request.UserStatusRequest;
import antifraud.model.response.UserStatusResponse;
import antifraud.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsernameIgnoreCase(username);

        return user.map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }

    @Transactional
    public Optional<User> register(User user) {
        if (userRepository.count() == 0) {
            user.setRole(Role.ADMINISTRATOR);
            user.setAccountNonLocked(true);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return Optional.of(userRepository.save(user));
        }

        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            return Optional.empty();
        }

        user.setRole(Role.MERCHANT);
        user.setAccountNonLocked(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return Optional.of(userRepository.save(user));
    }

    public List<User> listUsers() {
        return userRepository.findAll(
                Sort.sort(User.class).by(User::getId).ascending()
        );
    }

    @Transactional
    public boolean delete(String username) {
        return userRepository.deleteByUsernameIgnoreCase(username) == 1;
    }

    public User changeRole(ChangeRoleRequest changeRoleRequest) {
        User user = userRepository.findByUsernameIgnoreCase(changeRoleRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User " + changeRoleRequest.getUsername() + " not found"));

        Role newRole = Role.valueOf(changeRoleRequest.getRole());

        if (user.getRole() == newRole) {
            throw new RoleAlreadySetException();
        } else if (newRole == Role.ADMINISTRATOR) {
            throw new AdminAlreadyExistsException();
        }

        user.setRole(newRole);
        return userRepository.save(user);
    }

    public UserStatusResponse changeAccess(UserStatusRequest userStatusRequest) {
        User user = userRepository.findByUsernameIgnoreCase(userStatusRequest.getUsername())
               .orElseThrow(() -> new UsernameNotFoundException("User " + userStatusRequest.getUsername() + " not found"));

        if (user.getRole() == Role.ADMINISTRATOR) {
            throw new AdminChangeAccessException();
        }

        Status operation = userStatusRequest.getOperation();

        user.setAccountNonLocked(operation == Status.UNLOCK);
        userRepository.save(user);

        return new UserStatusResponse("User " + user.getUsername() + " " + operation.toString().toLowerCase() + "ed!");
    }
}