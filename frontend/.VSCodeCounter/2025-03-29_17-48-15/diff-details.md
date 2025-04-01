# Diff Details

Date : 2025-03-29 17:48:15

Directory d:\\examples\\blog\\frontend\\src

Total : 71 files,  1505 codes, 397 comments, 389 blanks, all 2291 lines

[Summary](results.md) / [Details](details.md) / [Diff Summary](diff.md) / Diff Details

## Files
| filename | language | code | comment | blank | total |
| :--- | :--- | ---: | ---: | ---: | ---: |
| [src/App.vue](/src/App.vue) | Vue | 28 | 3 | 5 | 36 |
| [src/api/common/category.js](/src/api/common/category.js) | JavaScript | 19 | 12 | 13 | 44 |
| [src/api/common/tag.js](/src/api/common/tag.js) | JavaScript | 12 | 9 | 10 | 31 |
| [src/api/post/authorAnalytics.js](/src/api/post/authorAnalytics.js) | JavaScript | 4 | 1 | 2 | 7 |
| [src/api/post/comment.js](/src/api/post/comment.js) | JavaScript | 14 | 10 | 11 | 35 |
| [src/api/post/export.js](/src/api/post/export.js) | JavaScript | 6 | 2 | 3 | 11 |
| [src/api/post/favorite.js](/src/api/post/favorite.js) | JavaScript | 17 | 11 | 12 | 40 |
| [src/api/post/post.js](/src/api/post/post.js) | JavaScript | 38 | 23 | 26 | 87 |
| [src/api/post/postVersion.js](/src/api/post/postVersion.js) | JavaScript | 13 | 7 | 8 | 28 |
| [src/api/post/recommendation.js](/src/api/post/recommendation.js) | JavaScript | 8 | 2 | 3 | 13 |
| [src/api/post/report.js](/src/api/post/report.js) | JavaScript | 19 | 12 | 13 | 44 |
| [src/api/user/message.js](/src/api/user/message.js) | JavaScript | 23 | 13 | 14 | 50 |
| [src/api/user/permission.js](/src/api/user/permission.js) | JavaScript | 15 | 8 | 9 | 32 |
| [src/api/user/role.js](/src/api/user/role.js) | JavaScript | 16 | 11 | 12 | 39 |
| [src/api/user/rolePermission.js](/src/api/user/rolePermission.js) | JavaScript | 17 | 9 | 10 | 36 |
| [src/api/user/security/captcha.js](/src/api/user/security/captcha.js) | JavaScript | 30 | 6 | 4 | 40 |
| [src/api/user/security/emailVerification.js](/src/api/user/security/emailVerification.js) | JavaScript | 13 | 2 | 3 | 18 |
| [src/api/user/user.js](/src/api/user/user.js) | JavaScript | 31 | 16 | 18 | 65 |
| [src/api/user/userActivity.js](/src/api/user/userActivity.js) | JavaScript | 21 | 11 | 12 | 44 |
| [src/api/user/userRole.js](/src/api/user/userRole.js) | JavaScript | 13 | 7 | 8 | 28 |
| [src/api/user/userSetting.js](/src/api/user/userSetting.js) | JavaScript | 10 | 7 | 8 | 25 |
| [src/assets/main.scss](/src/assets/main.scss) | SCSS | 16 | 1 | 3 | 20 |
| [src/components/BreadCrumb.vue](/src/components/BreadCrumb.vue) | Vue | 64 | 6 | 11 | 81 |
| [src/components/PageContainer.vue](/src/components/PageContainer.vue) | Vue | 204 | 13 | 36 | 253 |
| [src/constants/api-constants.js](/src/constants/api-constants.js) | JavaScript | 17 | 3 | 3 | 23 |
| [src/constants/role-constants.js](/src/constants/role-constants.js) | JavaScript | 20 | 1 | 1 | 22 |
| [src/constants/routes-constants.js](/src/constants/routes-constants.js) | JavaScript | 3 | 70 | 18 | 91 |
| [src/constants/routes/admin.js](/src/constants/routes/admin.js) | JavaScript | 19 | 5 | 5 | 29 |
| [src/constants/routes/base.js](/src/constants/routes/base.js) | JavaScript | 3 | 1 | 0 | 4 |
| [src/constants/routes/user.js](/src/constants/routes/user.js) | JavaScript | 15 | 4 | 4 | 23 |
| [src/directives/permission.js](/src/directives/permission.js) | JavaScript | 15 | 1 | 3 | 19 |
| [src/main.js](/src/main.js) | JavaScript | 15 | 1 | 13 | 29 |
| [src/router/index.js](/src/router/index.js) | JavaScript | 93 | 32 | 14 | 139 |
| [src/router/modules/admin.js](/src/router/modules/admin.js) | JavaScript | 215 | 19 | 2 | 236 |
| [src/router/modules/auth.js](/src/router/modules/auth.js) | JavaScript | 10 | 1 | 1 | 12 |
| [src/router/modules/user.js](/src/router/modules/user.js) | JavaScript | 160 | 16 | 1 | 177 |
| [src/stores/index.js](/src/stores/index.js) | JavaScript | 6 | 0 | 4 | 10 |
| [src/stores/modules/user.js](/src/stores/modules/user.js) | JavaScript | 75 | 6 | 6 | 87 |
| [src/utils/date.js](/src/utils/date.js) | JavaScript | 18 | 11 | 6 | 35 |
| [src/utils/format.js](/src/utils/format.js) | JavaScript | 2 | 0 | 2 | 4 |
| [src/utils/jwt.js](/src/utils/jwt.js) | JavaScript | 15 | 0 | 1 | 16 |
| [src/utils/request.js](/src/utils/request.js) | JavaScript | 42 | 5 | 8 | 55 |
| [src/views/comment/CommentManage.vue](/src/views/comment/CommentManage.vue) | Vue | -243 | -17 | -37 | -297 |
| [src/views/comment/admin/CommentManage.vue](/src/views/comment/admin/CommentManage.vue) | Vue | 7 | 0 | 3 | 10 |
| [src/views/comment/user/CommentManage.vue](/src/views/comment/user/CommentManage.vue) | Vue | 243 | 17 | 37 | 297 |
| [src/views/favorite/FavoriteManage.vue](/src/views/favorite/FavoriteManage.vue) | Vue | -5 | 0 | -3 | -8 |
| [src/views/favorite/admin/FavoriteManage.vue](/src/views/favorite/admin/FavoriteManage.vue) | Vue | 7 | 0 | 3 | 10 |
| [src/views/favorite/user/FavoriteManage.vue](/src/views/favorite/user/FavoriteManage.vue) | Vue | 5 | 0 | 3 | 8 |
| [src/views/layout/ControlLayout.vue](/src/views/layout/ControlLayout.vue) | Vue | 470 | 65 | 56 | 591 |
| [src/views/layout/LayoutContainer.vue](/src/views/layout/LayoutContainer.vue) | Vue | -420 | -46 | -40 | -506 |
| [src/views/layout/PostLayout.vue](/src/views/layout/PostLayout.vue) | Vue | -5 | 0 | -2 | -7 |
| [src/views/layout/user/PostLayout.vue](/src/views/layout/user/PostLayout.vue) | Vue | 5 | 0 | 2 | 7 |
| [src/views/like/admin/LikeManage.vue](/src/views/like/admin/LikeManage.vue) | Vue | 7 | 0 | 3 | 10 |
| [src/views/message/MessageManage.vue](/src/views/message/MessageManage.vue) | Vue | -5 | 0 | -3 | -8 |
| [src/views/message/admin/MessageManage.vue](/src/views/message/admin/MessageManage.vue) | Vue | 7 | 0 | 3 | 10 |
| [src/views/message/user/MessageManage.vue](/src/views/message/user/MessageManage.vue) | Vue | 5 | 0 | 3 | 8 |
| [src/views/post/PostAttachmentManage.vue](/src/views/post/PostAttachmentManage.vue) | Vue | -5 | 0 | -3 | -8 |
| [src/views/post/PostEdit.vue](/src/views/post/PostEdit.vue) | Vue | -5 | 0 | -3 | -8 |
| [src/views/post/PostList.vue](/src/views/post/PostList.vue) | Vue | -5 | 0 | -3 | -8 |
| [src/views/post/PostVersionManage.vue](/src/views/post/PostVersionManage.vue) | Vue | -5 | 0 | -3 | -8 |
| [src/views/post/admin/PostManage.vue](/src/views/post/admin/PostManage.vue) | Vue | 7 | 0 | 3 | 10 |
| [src/views/post/admin/PostVersionManage.vue](/src/views/post/admin/PostVersionManage.vue) | Vue | 7 | 0 | 3 | 10 |
| [src/views/post/user/CommentManage.vue](/src/views/post/user/CommentManage.vue) | Vue | 5 | 0 | 3 | 8 |
| [src/views/post/user/PostAttachmentManage.vue](/src/views/post/user/PostAttachmentManage.vue) | Vue | 5 | 0 | 3 | 8 |
| [src/views/post/user/PostEdit.vue](/src/views/post/user/PostEdit.vue) | Vue | 5 | 0 | 3 | 8 |
| [src/views/post/user/PostList.vue](/src/views/post/user/PostList.vue) | Vue | 5 | 0 | 3 | 8 |
| [src/views/post/user/PostVersionManage.vue](/src/views/post/user/PostVersionManage.vue) | Vue | 5 | 0 | 3 | 8 |
| [src/views/report/ReportManage.vue](/src/views/report/ReportManage.vue) | Vue | -5 | 0 | -3 | -8 |
| [src/views/report/user/ReportManage.vue](/src/views/report/user/ReportManage.vue) | Vue | 5 | 0 | 3 | 8 |
| [src/views/user/admin/UserManage.vue](/src/views/user/admin/UserManage.vue) | Vue | 7 | 0 | 3 | 10 |
| [src/views/user/admin/UserSettingManage.vue](/src/views/user/admin/UserSettingManage.vue) | Vue | 7 | 0 | 3 | 10 |

[Summary](results.md) / [Details](details.md) / [Diff Summary](diff.md) / Diff Details