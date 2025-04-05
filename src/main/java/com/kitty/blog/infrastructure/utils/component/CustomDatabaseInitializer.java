package com.kitty.blog.utils.component;

import com.kitty.blog.repository.PermissionRepository;
import com.kitty.blog.repository.RolePermissionRepository;
import com.kitty.blog.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class CustomDatabaseInitializer implements CommandLineRunner {

    private final DataSource dataSource;
    private final ResourceLoader resourceLoader;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    public CustomDatabaseInitializer(DataSource dataSource, ResourceLoader resourceLoader) {
        this.dataSource = dataSource;
        this.resourceLoader = resourceLoader;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        if (permissionRepository.count() == 0){
            executeSqlFile("classpath:sql/init/permission_init.sql");
        }
        if (roleRepository.count() == 0){
            executeSqlFile("classpath:sql/init/role_init.sql");
        }
        if (rolePermissionRepository.count() == 0){
            executeSqlFile("classpath:sql/init/role_permission_init.sql");
        }
    }

    private void executeSqlFile(String filePath) throws Exception {
        Resource resource = resourceLoader.getResource(filePath);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
             Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement()) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            String sql = sb.toString();
            stmt.execute(sql);
        }
    }
}
