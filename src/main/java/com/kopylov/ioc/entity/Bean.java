package com.kopylov.ioc.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class Bean {

    private final String id;
    private final Object value;
}
