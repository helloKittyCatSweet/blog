package com.kitty.blog.domain.repository;

import org.aspectj.lang.annotation.Aspect;

import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@NoRepositoryBean
/*
  内置方法：通过继承 JpaRepository，你可以直接使用以下方法：
  save(S entity)：保存实体。
  findById(ID id)：根据主键查找实体。
  findAll()：查找所有实体。
  deleteById(ID id)：根据主键删除实体。
  count()：返回实体的总数。
  existsById(ID id)：检查实体是否存在。
 */
@Component
public interface BaseRepository<T,ID> extends JpaRepository<T,ID> {
    // 可添加通用方法
}
