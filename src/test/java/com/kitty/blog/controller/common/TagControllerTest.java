package com.kitty.blog.controller.common;

import com.kitty.blog.application.controller.common.TagController;
import com.kitty.blog.domain.model.tag.Tag;
import com.kitty.blog.domain.service.tag.TagService;
import com.kitty.blog.infrastructure.utils.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class TagControllerTest {

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTag_Success() {
        // 准备测试数据
        Tag tag = new Tag();
        tag.setName("测试标签");

        // Mock Service层返回
        when(tagService.create(any(Tag.class)))
                .thenReturn(new ResponseEntity<>(true, HttpStatus.CREATED));

        // 执行测试
        ResponseEntity<Response<Boolean>> response = tagController.create(tag);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getData());
        assertEquals("创建成功", response.getBody().getMessage());
    }

    @Test
    void createTag_NameExists() {
        // 准备测试数据
        Tag tag = new Tag();
        tag.setName("已存在的标签");

        // Mock Service层返回
        when(tagService.create(any(Tag.class)))
                .thenReturn(new ResponseEntity<>(false, HttpStatus.CONFLICT));

        // 执行测试
        ResponseEntity<Response<Boolean>> response = tagController.create(tag);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("标签名称已存在", response.getBody().getMessage());
    }

    @Test
    void updateTag_Success() {
        // 准备测试数据
        Tag tag = new Tag();
        tag.setTagId(1);
        tag.setName("更新后的标签");

        // Mock Service层返回
        when(tagService.update(any(Tag.class)))
                .thenReturn(new ResponseEntity<>(true, HttpStatus.OK));

        // 执行测试
        ResponseEntity<Response<Boolean>> response = tagController.update(tag);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getData());
        assertEquals("更新成功", response.getBody().getMessage());
    }
}