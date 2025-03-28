package com.kitty.blog.controller.common;

import com.kitty.blog.dto.category.TreeDto;
import com.kitty.blog.model.category.Category;
import com.kitty.blog.service.CategoryService;

import com.kitty.blog.utils.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "分类管理", description = "管理博客文章分类")
@RestController
@RequestMapping("/api/category")
@CrossOrigin(origins = "*", maxAge = 3600)

public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 创建分类
     * @param category
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "创建分类")
    @PostMapping("/public/create")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "创建成功"),
                            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Boolean>> create(
            @RequestBody Category category) {
        // TODO: create category
        ResponseEntity<Boolean> response = categoryService.create(category);
        return Response.createResponse(response,
                HttpStatus.OK, "创建成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 更新分类
     * @param category
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "更新分类")
    @PutMapping("/public/update")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "更新成功"),
                            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Boolean>> update(
            @RequestBody Category category) {
        // TODO: update category
        ResponseEntity<Boolean> response = categoryService.update(category);
        return Response.createResponse(response,
                HttpStatus.OK, "更新成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 删除分类及其子分类
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_CATEGORY_MANAGER) " +
            "or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "删除分类及其子分类")
    @DeleteMapping("/admin/delete/sub/{id}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "删除成功"),
                           @ApiResponse(responseCode = "404", description = "分类不存在")})
    public ResponseEntity<Response<Boolean>> deleteWithSubCategories(
            @PathVariable("id") Integer id) {
        // TODO: delete category and sub categories
        ResponseEntity<Boolean> response = categoryService.deleteWithSubCategories(id);
        return Response.createResponse(response, 
                HttpStatus.OK, "删除成功", 
                HttpStatus.NOT_FOUND, "分类不存在");
    }

    /**
     * 删除叶子分类
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_CATEGORY_MANAGER) " +
            "or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "删除叶子分类")
    @DeleteMapping("/admin/delete/leaf/{id}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "删除成功"),
                           @ApiResponse(responseCode =  "404", description = "分类不存在")})
    public ResponseEntity<Response<Boolean>> deleteLeafCategory(
            @PathVariable("id") Integer id) {
        // TODO: delete leaf category
        ResponseEntity<Boolean> response = categoryService.deleteLeafCategory(id);
        return Response.createResponse(response, 
                HttpStatus.OK, "删除成功", 
                HttpStatus.NOT_FOUND, "分类不存在");
    }

    /**
     * 根据名称查询分类
     * @param name
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "根据名称查询分类")
    @GetMapping("/public/find/name/{name}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
                           @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Category>> findByName(
            @PathVariable("name") String name) {
        // TODO: find category by name
        ResponseEntity<Category> response = categoryService.findByName(name);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 根据父分类ID查询子分类
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "根据父分类ID查询子分类")
    @GetMapping("/public/find/parent/{id}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
                            @ApiResponse(responseCode = "404", description = "分类不存在")})
    public ResponseEntity<Response<List<TreeDto>>> findByParentId(
            @PathVariable("id") Integer id) {
        // TODO: find categories by parent id
        ResponseEntity<List<TreeDto>> response= categoryService.findByParentId(id);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "分类不存在");
    }

    /**
     * 根据父分类ID查询所有子分类（包含子孙）
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "根据父分类ID查询所有子分类（包含子孙）")
    @GetMapping("/public/find/descendants/{id}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
                            @ApiResponse(responseCode = "404", description = "分类不存在")})
    public ResponseEntity<Response<List<TreeDto>>> findDescendantsByParentId(
            @PathVariable("id") Integer id) {
        // TODO: find categories by parent id
        ResponseEntity<List<TreeDto>> response= categoryService.findDescendantsByParentId(id);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "分类不存在");
    }

    /**
     * 保存分类
     * @param category
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "保存分类")
    @PostMapping("/public/save")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "保存成功"),
                            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Category>> save(
            @RequestBody Category category) {
        // TODO: save category
        ResponseEntity<Category> response = categoryService.save(category);
        return Response.createResponse(response,
                HttpStatus.OK, "保存成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 根据ID查询分类
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_CATEGORY_MANAGER) " +
            "or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据ID查询分类")
    @GetMapping("/admin/find/id/{id}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
                            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Category>> findById(
            @PathVariable("id") Integer id) {
        // TODO: find category by id
        ResponseEntity<Category> response = categoryService.findById(id);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 查询所有分类
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "查询所有分类")
    @GetMapping("/public/find/all")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
                            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<List<Category>>> findAll() {
        // TODO: find all categories
        ResponseEntity<List<Category>> response = categoryService.findAll();
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 根据ID删除分类
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_CATEGORY_MANAGER) " +
            "or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据ID删除分类")
    @DeleteMapping("/admin/delete/id/{id}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "删除成功"),
                            @ApiResponse(responseCode = "404", description = "分类不存在")})
    public ResponseEntity<Response<Boolean>> deleteById(
            @PathVariable("id") Integer id) {
        // TODO: delete category by id
        ResponseEntity<Boolean> response = categoryService.deleteById(id);
        return Response.createResponse(response,
                HttpStatus.OK, "删除成功",
                HttpStatus.NOT_FOUND, "分类不存在");
    }

    /**
     * 查询分类数量
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "查询分类数量")
    @GetMapping("/public/count")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
                            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Long>> count() {
        // TODO: count categories
        ResponseEntity<Long> response = categoryService.count();
        return Response.createResponse(response,
                HttpStatus.OK,"查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR,"服务器繁忙");
    }

    /**
     * 检查分类是否存在
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "检查分类是否存在")
    @GetMapping("/public/exists/{id}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
                            @ApiResponse(responseCode = "404", description = "分类不存在")})
    public ResponseEntity<Response<Boolean>> existsById(
            @PathVariable("id") Integer id) {
        // TODO: check category exists
        ResponseEntity<Boolean> response = categoryService.existsById(id);
        return Response.createResponse(response,
                HttpStatus.OK,"查询成功", 
                HttpStatus.INTERNAL_SERVER_ERROR,"分类不存在");
    }
}
