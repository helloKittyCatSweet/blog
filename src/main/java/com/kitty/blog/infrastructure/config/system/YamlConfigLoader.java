package com.kitty.blog.infrastructure.config.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

import java.io.IOException;
import java.util.List;

@Configuration
public class YamlConfigLoader {

    @Autowired
    private StandardEnvironment environment;

    @Bean
    public Boolean loadYamlFiles() throws IOException {  // 修改返回类型为 Boolean
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:/templates/**/*.yml");

        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        MutablePropertySources propertySources = environment.getPropertySources();

        for (Resource resource : resources) {
            List<PropertySource<?>> loadedSources = loader.load(resource.getFilename(), resource);
            loadedSources.forEach(propertySources::addLast);
        }
        return true;  // 返回任意非空值
    }
}