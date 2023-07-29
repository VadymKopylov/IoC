package com.kopylov.ioc.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserService {

    private MailService mailService;
    private String user;
}
