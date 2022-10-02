package antifraud.mapper;

import antifraud.model.User;
import antifraud.model.UserDto;

public class UserManualMapper {

    public static User userDtoToUser(UserDto userDto) {
        return User.builder().
                name(userDto.getName()).
                username(userDto.getUsername()).
                password(userDto.getPassword()).
                build();
    }
}
