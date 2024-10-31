package com.zlx.deployment.bean;


import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class User {
    private String name;

    private String beginName;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Long index;
}
