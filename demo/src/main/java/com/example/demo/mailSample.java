package com.example.demo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class mailSample {

    private String to; // 수신자 이메일 주소
    private String subject; // 메일 제목
    private String message; // 메일 내용
    private String from; // 발신자 이메일 주소
    private String name; // 이름
    private String bcc; // 숨은 참조자 이메일 주소

}
