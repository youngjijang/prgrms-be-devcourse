package com.example.kdtspringjpa.order.exception;

import lombok.NoArgsConstructor;

import java.sql.SQLException;

@NoArgsConstructor
public class NotFoundException extends SQLException {
    public NotFoundException(String s) {
        super(s);
    }
}
