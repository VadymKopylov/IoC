package com.kopylov.ioc.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentService {

    private MailService mailService;
    private String paymentType;
}
