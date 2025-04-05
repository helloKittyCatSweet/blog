package com.kitty.blog.utils.component;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;
import com.kitty.blog.model.SystemLog;
import com.kitty.blog.repository.SystemLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DatabaseLogAppender extends AppenderBase<ILoggingEvent> {
    @Autowired
    private SystemLogRepository systemLogRepository;

    @Override
    protected void append(ILoggingEvent event) {
        SystemLog log = new SystemLog();
        log.setLevel(event.getLevel().toString());
        log.setMessage(event.getFormattedMessage());
        log.setLoggerName(event.getLoggerName());
        log.setThreadName(event.getThreadName());
        log.setCreatedAt(LocalDateTime.now());

        if (event.getThrowableProxy() != null) {
            log.setStackTrace(ThrowableProxyUtil.asString(event.getThrowableProxy()));
        }

        systemLogRepository.save(log);
    }
}