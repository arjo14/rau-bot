package com.rau.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationPayload {

    private Long id;

    private String message;

    private Boolean hasEndButton;
    private Boolean hasBackButton;

    private List<ButtonPayload> buttons;

}
