package com.shinhantime.tweione.User.Jwt.dto;

import com.shinhantime.tweione.User.repository.UserEntity;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String name;

    static public UserDto toDto(UserEntity userEntity){
        return UserDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .name(userEntity.getName()).build();
    }

    public UserEntity toEntity(){
        return UserEntity.builder()
                .id(id)
                .username(username)
                .name(name)
                .build();
    }

}
