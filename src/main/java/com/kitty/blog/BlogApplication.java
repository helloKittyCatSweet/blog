package com.kitty.blog;

import com.kitty.blog.infrastructure.security.filter.JwtAuthenticationFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling // 启用定时任务支持
@EnableTransactionManagement
@EnableJpaRepositories
@EnableJpaAuditing
@EnableEnversRepositories
@ComponentScan(basePackages = {
		"com.kitty.blog.infrastructure",  // 先扫描基础设施层
		"com.kitty.blog.common",          // 再扫描公共组件
		"com.kitty.blog.domain",          // 然后是领域层
		"com.kitty.blog.application",     // 应用层
		"com.kitty.blog.interfaces"       // 最后是接口层
})
public class BlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

}
