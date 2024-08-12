package com.example.netty.pojo;
import lombok.Data;

import java.util.Date;

@Data
public class User {
    private String userName;

    private String method;

    private Date date;
}
