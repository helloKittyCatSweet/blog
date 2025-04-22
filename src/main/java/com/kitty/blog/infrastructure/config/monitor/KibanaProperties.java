package com.kitty.blog.infrastructure.config.monitor;

import lombok.Value;

@Value
public class KibanaProperties {
    String url;
    String username;
    String password;
}