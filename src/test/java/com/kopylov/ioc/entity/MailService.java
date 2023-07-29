package com.kopylov.ioc.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MailService {

    private String protocol;
    private int port;
}
