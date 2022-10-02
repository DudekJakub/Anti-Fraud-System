package antifraud.mapper;

import antifraud.model.User;
import antifraud.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

//@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {

    User userDtoToUser(UserDto userDto);
}
