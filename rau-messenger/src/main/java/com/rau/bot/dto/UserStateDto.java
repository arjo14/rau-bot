package com.rau.bot.dto;

import com.rau.bot.entity.user.User;
import com.rau.bot.enums.UserState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStateDto {

    private User user;
    private UserState userState;
}
