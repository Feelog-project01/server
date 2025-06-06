package com.app.feelog.domain.dto;

import com.app.feelog.domain.enumeration.SendMessageStatus;
import com.app.feelog.domain.vo.SendMessageVO;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@ToString
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SendMessageDTO {
    @EqualsAndHashCode.Include
    private Long id;
    private Long memberId;
    private Long receiverId;
    private SendMessageStatus sendMessageStatus;
    private String messageContent;
    private String createdDate;
    private String updatedDate;

    public SendMessageVO toVO() {
        return SendMessageVO.builder()
                .id(id)
                .memberId(memberId)
                .receiverId(receiverId)
                .sendMessageStatus(sendMessageStatus)
                .messageContent(messageContent)
                .createdDate(createdDate)
                .updatedDate(updatedDate)
                .build();
    }
}
