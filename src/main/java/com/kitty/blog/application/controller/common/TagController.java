package com.kitty.blog.application.controller.common;

import com.kitty.blog.common.annotation.LogUserActivity;
import com.kitty.blog.common.constant.Compare;
import com.kitty.blog.domain.service.tag.TagService;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "标签管理")
@RestController
@RequestMapping("/api/tag")
@CrossOrigin
@Slf4j
public class TagController {

        @Autowired
        private TagService tagService;

        /**
         * 创建标签
         * 切记不要传id
         *
         * @param tag
         * @return
         */
        @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
        @Operation(summary = "创建标签")
        @PostMapping("/public/create")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "创建成功"),
                        @ApiResponse(responseCode = "409", description = "标签名称已存在"),
                        @ApiResponse(responseCode = "500", description = "标签名称不合规")
        })
        @LogUserActivity("创建标签")
        public ResponseEntity<Response<Boolean>> create(@RequestBody com.kitty.blog.domain.model.tag.Tag tag) {
                ResponseEntity<Boolean> entity = tagService.create(tag);
                return switch (entity.getStatusCode().value()) {
                        case 409 -> Response.error(HttpStatus.CONFLICT, "标签名称已存在");
                        case 500 -> Response.error(HttpStatus.BAD_REQUEST, "标签名称不合规");
                        case 201 -> Response.ok(true, "创建成功");
                        default -> Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "创建失败");
                };
        }

        /**
         * 更新标签
         *
         * @param tag
         * @return
         */
        @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
        @Operation(summary = "更新标签")
        @PutMapping("/public/update")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "更新成功"),
                        @ApiResponse(responseCode = "500", description = "更新失败")
        })
        @LogUserActivity("更新标签")
        public ResponseEntity<Response<Boolean>> update(@RequestBody com.kitty.blog.domain.model.tag.Tag tag) {
                ResponseEntity<Boolean> entity = tagService.update(tag);
                return Response.createResponse(entity,
                                HttpStatus.OK, "更新成功",
                                HttpStatus.INTERNAL_SERVER_ERROR, "更新失败");
        }

        /**
         * 根据名称查询标签
         *
         * @param name
         * @return
         */
        @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
        @Operation(summary = "根据名称查询标签")
        @GetMapping("/public/find/name/{name}")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查询成功"),
                        @ApiResponse(responseCode = "500", description = "查询失败")
        })
        public ResponseEntity<Response<com.kitty.blog.domain.model.tag.Tag>> findByName(
                        @PathVariable("name") String name) {
                ResponseEntity<com.kitty.blog.domain.model.tag.Tag> entity = tagService.findByName(name);
                return Response.createResponse(entity,
                                HttpStatus.OK, "查询成功",
                                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
        }

        /**
         * 根据权重查询标签
         *
         * @param weight
         * @return
         */
        @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
        @Operation(summary = "根据权重查询标签")
        @GetMapping("/public/find/weight/{weight}/{compare}")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查询成功"),
                        @ApiResponse(responseCode = "500", description = "查询失败")
        })
        public ResponseEntity<Response<Page<com.kitty.blog.domain.model.tag.Tag>>> findTagsByWeight(
                        @PathVariable("weight") Integer weight,
                        @PathVariable("compare") Compare compare,
                        @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                        @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                        @RequestParam(value = "sorts", defaultValue = "useCount", required = false) String[] sorts) {
                Page<com.kitty.blog.domain.model.tag.Tag> entity = tagService.findTagsByWeight(weight, compare, page,
                                size, sorts);
                return Response.createResponse(ResponseEntity.ok(entity),
                                HttpStatus.OK, "查询成功",
                                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
        }

        /**
         * 保存标签
         *
         * @param tag
         * @return
         */
        @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
        @Operation(summary = "保存标签")
        @PostMapping("/public/save")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "保存成功"),
                        @ApiResponse(responseCode = "500", description = "保存失败")
        })
        public ResponseEntity<Response<com.kitty.blog.domain.model.tag.Tag>> save(
                        @RequestBody com.kitty.blog.domain.model.tag.Tag tag) {
                ResponseEntity<com.kitty.blog.domain.model.tag.Tag> entity = tagService.save(tag);
                return Response.createResponse(entity,
                                HttpStatus.OK, "保存成功",
                                HttpStatus.INTERNAL_SERVER_ERROR, "保存失败");
        }

        /**
         * 根据ID查询标签
         *
         * @param tagId
         * @return
         */
        @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_TAG_MANAGER) or " +
                        "hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
        @Operation(summary = "根据ID查询标签")
        @GetMapping("/admin/find/id/{tagId}")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查询成功"),
                        @ApiResponse(responseCode = "500", description = "查询失败")
        })
        public ResponseEntity<Response<com.kitty.blog.domain.model.tag.Tag>> findById(
                        @PathVariable("tagId") Integer tagId) {
                ResponseEntity<com.kitty.blog.domain.model.tag.Tag> entity = tagService.findById(tagId);
                return Response.createResponse(entity,
                                HttpStatus.OK, "查询成功",
                                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
        }

        /**
         * 查询所有标签
         *
         * @return
         */
        // 前台展示界面
        @Operation(summary = "查询所有标签")
        @GetMapping("/public/find/all")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查询成功"),
                        @ApiResponse(responseCode = "500", description = "查询失败")
        })
        public ResponseEntity<Response<Page<com.kitty.blog.domain.model.tag.Tag>>> findAll(
                        @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                        @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                        @RequestParam(value = "sorts", defaultValue = "useCount,desc", required = false) String[] sorts) {
                log.info("TagController.findAll:sorts={}", sorts);
                Page<com.kitty.blog.domain.model.tag.Tag> entity = tagService.findAll(page, size, sorts);
                return Response.createResponse(ResponseEntity.ok(entity),
                                HttpStatus.OK, "查询成功",
                                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
        }

        /**
         * 根据ID删除标签
         *
         * @param tagId
         * @return
         */
        @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_TAG_MANAGER) or " +
                        "hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
        @Operation(summary = "根据ID删除标签")
        @DeleteMapping("/admin/delete/id/{tagId}")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "删除成功"),
                        @ApiResponse(responseCode = "500", description = "删除失败")
        })
        @LogUserActivity("删除标签")
        public ResponseEntity<Response<Boolean>> deleteById(@PathVariable("tagId") Integer tagId) {
                ResponseEntity<Boolean> entity = tagService.deleteById(tagId);
                return Response.createResponse(entity,
                                HttpStatus.OK, "删除成功",
                                HttpStatus.INTERNAL_SERVER_ERROR, "删除失败");
        }

        /**
         * 查询标签数量
         *
         * @return
         */
        @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
        @Operation(summary = "查询标签数量")
        @GetMapping("/public/count")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查询成功"),
                        @ApiResponse(responseCode = "404", description = "查询失败")
        })
        public ResponseEntity<Response<Long>> count() {
                ResponseEntity<Long> entity = tagService.count();
                return Response.createResponse(entity,
                                HttpStatus.OK, "查询成功",
                                HttpStatus.NOT_FOUND, "查询失败");
        }

        /**
         * 根据ID查询标签是否存在
         *
         * @param tagId
         * @return
         */
        @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
        @Operation(summary = "根据ID查询标签是否存在")
        @GetMapping("/public/exists/id/{tagId}")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查询成功"),
                        @ApiResponse(responseCode = "404", description = "查询失败")
        })
        public ResponseEntity<Response<Boolean>> existsById(Integer tagId) {
                ResponseEntity<Boolean> entity = tagService.existsById(tagId);
                return Response.createResponse(entity,
                                HttpStatus.OK, "查询成功",
                                HttpStatus.NOT_FOUND, "查询失败");
        }

        @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
        @Operation(summary = "搜索标签")
        @GetMapping("/public/find/combined")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "搜索成功"),
                        @ApiResponse(responseCode = "500", description = "搜索失败")
        })
        public ResponseEntity<Response<Page<com.kitty.blog.domain.model.tag.Tag>>> findByCombined(
                        @RequestParam(required = false) String name,
                        @RequestParam(required = false) Integer weight,
                        @RequestParam(required = false, defaultValue = "GREATER_THAN") String operator,
                        @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                        @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                        @RequestParam(value = "sorts", defaultValue = "useCount", required = false) String[] sorts) {
                Page<com.kitty.blog.domain.model.tag.Tag> entity = tagService.findByCombined(name, weight, operator,
                                page, size, sorts);
                return Response.createResponse(ResponseEntity.ok(entity),
                                HttpStatus.OK, "搜索成功",
                                HttpStatus.INTERNAL_SERVER_ERROR, "搜索失败");
        }

}
