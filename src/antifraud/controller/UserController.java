package antifraud.controller;

import antifraud.mapper.UserManualMapper;
import antifraud.model.User;
import antifraud.model.UserDto;
import antifraud.model.request.ChangeRoleRequest;
import antifraud.model.request.UserStatusRequest;
import antifraud.model.response.UserStatusResponse;
import antifraud.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    User createUser(@Valid @RequestBody UserDto userDto) {
//        User userToCreate = mapper.userDtoToUser(userDto);

        User userToCreate = UserManualMapper.userDtoToUser(userDto);

        log.info("UserDto = {}", userDto);
        log.info("User = {}", userToCreate);

        return userService.register(userToCreate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR')")
    @PutMapping("/role")
    User changeRole(@Valid @RequestBody ChangeRoleRequest changeRoleRequest) {
        return userService.changeRole(changeRoleRequest);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR')")
    @PutMapping("/access")
    UserStatusResponse changeAccess(@Valid @RequestBody UserStatusRequest userStatusRequest) {
        return userService.changeAccess(userStatusRequest);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'SUPPORT')")
    @GetMapping("/list")
    List<User> listUsers() {
        return userService.listUsers();
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR')")
    @DeleteMapping("/user/{username}")
    Map<String, String> delete(@PathVariable String username) {
        if (userService.delete(username)) {
            return Map.of(
                    "username", username,
                    "status", "Deleted successfully!"
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/user/")
    void delete() {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
}
