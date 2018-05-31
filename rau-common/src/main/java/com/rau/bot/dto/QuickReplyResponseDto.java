package com.rau.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuickReplyResponseDto {

    private String text;

    private List<QuickReplyDto> quickReplyDtoList;

    public QuickReplyResponseDto(String text) {
        this.text = text;
    }
}
