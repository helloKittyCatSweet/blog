import request from "@/utils/request";
import { postPrefix } from "@/constants/api-constants.js";

// 更新文章
export const update = (data) => request.put(`${postPrefix}/public/update`, data);

// 点赞文章
export const likePost = ({ postId, count }) =>
  request.post(`${postPrefix}/public/like`, { postId, count });

// 收藏文章
export const collectPost = ({ postId, count }) =>
  request.post(`${postPrefix}/public/collect`, { postId, count });

// 上传文件
export const uploadAttachment = async (file, postId) => {
  const formData = new FormData();
  formData.append("file", file);
  if (postId) {
    formData.append("postId", parseInt(postId));
  }

  try {
    const response = await request.post(
      `${postPrefix}/public/upload/attachment`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );
    console.log("上传响应:", response);
    return response;
  } catch (error) {
    console.error("上传错误:", error.response || error);
    throw error;
  }
};

// 上传文章封面
export const uploadPostCover = async (file, postId) => {
  const formData = new FormData();
  formData.append("file", file);
  if (postId) {
    formData.append("postId", parseInt(postId));
  }

  try {
    const response = await request.post(`${postPrefix}/public/upload/cover`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    console.log("上传响应:", response);
    return response;
  } catch (error) {
    console.error("上传错误:", error.response || error);
    throw error;
  }
};

export const deleteAttachment = (attachmentId) =>
  request.delete(`${postPrefix}/public/delete/attachment/${attachmentId}`);

// 创建文章
export const create = (data) => request.post(`${postPrefix}/public/create`, data);

// 添加文章版本
export const addVersion = ({ postId, content, userId }) =>
  request.post(`${postPrefix}/public/add/version`, {
    postId: parseInt(postId),
    content,
    userId: parseInt(userId),
  });

// 添加文章标签
export const addTag = (data) => request.post(`${postPrefix}/public/add/tag`, data);

// 添加文章分类
export const addCategory = (data) =>
  request.post(`${postPrefix}/public/add/category`, data);

// 根据标签查询文章列表
export const findByTag = (tag, { page = 0, size = 10, sort = ["createdAt,desc"] } = {}) =>
  request.get(`${postPrefix}/public/find/tag/${tag}`, {
    params: { page, size, sort }
  });

// 根据标签列表查询文章列表
export const findByTags = (tags, { page = 0, size = 10, sorts = ["createdAt", "desc"] } = {}) =>
  request.post(`${postPrefix}/public/find/tags`, {tags ,page, size, sorts });

// 根据分类查询文章列表
export const findByCategory = (category, { page = 0, size = 10, sort = "createdAt,desc" } = {}) =>
  request.get(`${postPrefix}/public/find/category/${category}`, {
    params: { page, size, sort }
  });

// 根据用户名和发布状态查询文章列表
export const findByUserList = (userId, { page = 0, size = 10, sort = "createdAt,desc" } = {}) =>
  request.get(`${postPrefix}/public/find/${userId}/list`, {
    params: { page, size, sort }
  });

  // 根据用户名和发布状态查询文章列表
export const findByUserListNoPage = (userId) =>
  request.get(`${postPrefix}/public/find/${userId}/list-all`, );

// 根据用户名查找文章列表
export const findByUserId = (userId, { page = 0, size = 10, sort = "createdAt,desc" } = {}) =>
  request.get(`${postPrefix}/public/find/user/${userId}`, {
    params: { page, size, sort }
  });

// 根据标题关键字查询文章列表
export const findByKeysInTitle = async (keyword, { page = 0, size = 10, sort = ["createdAt,desc"] } = {}) => {
  const url = `${postPrefix}/public/find/title/${keyword}`;
  return request.get(url, {
    params: { page, size, sort }
  });
};

// 根据内容关键字查询文章列表
export const findByKeysInContent = (keyword, { page = 0, size = 10, sort = ["createdAt,desc"] } = {}) =>
  request.get(`${postPrefix}/public/find/content/${keyword}`, {
    params: { page, size, sort }
  });

// 获取文章最新版本号
export const getLatestVersion = (postId) =>
  request.get(`${postPrefix}/admin/getLatestVersion/${postId}`);

// 根据文章id查询文章详情
export const findById = (postId) => request.get(`${postPrefix}/public/find/id/${postId}`);

// 查询所有文章
export const findAll = ({ page = 0, size = 10, sorts = "createdAt,desc" } = {}) =>
  request.get(`${postPrefix}/public/find/all`, {
    params: { page, size, sorts }
  });

// 查询所有文章（无分页）
export const findAllNoPage = () => request.get(`${postPrefix}/public/find/all/no-paging`);

// 根据id查询文章是否存在
export const existById = (postId) =>
  request.get(`${postPrefix}/admin/exists/id/${postId}`);

// 查询文章总数
export const count = () => request.get(`${postPrefix}/admin/count`);

// 删除文章版本
export const deleteVersion = (data) =>
  request.delete(`${postPrefix}/public/delete/version`, { data });

// 删除文章标签
export const deleteTag = (data) =>
  request.delete(`${postPrefix}/public/delete/tag`, data);

// 删除文章分类
export const deleteCategory = (data) =>
  request.delete(`${postPrefix}/public/delete/category`, data);

// 根据id删除文章
export const deleteById = (postId) =>
  request.delete(`${postPrefix}/public/delete/id/${postId}`);

// 搜索文章
export const searchPosts = (criteria, { page = 0, size = 10, sort = ["createdAt,desc"] } = {}) =>
  request.post(`${postPrefix}/public/search`, { ...criteria, page, size, sort });

// 根据用户id查询附件列表
export const findAttachmentsByUserId = ({ page = 0, size = 10, sorts = "createdTime,desc" } = {}) =>
  request.get(`${postPrefix}/public/find/attachments`, {
    params: { page, size, sorts  }
  });

// 生成文章摘要
export const generateSummary = (content) =>
  request.post(
    `${postPrefix}/public/summary`,
    { content },
    {
      headers: {
        "Content-Type": "application/json",
      },
    }
  );

// 更新文章分类
export const updateCategory = (postId, categoryId) =>
  request.put(`${postPrefix}/public/update/category/${postId}/${categoryId}`);

// 更新文章标签
export const updateTags = (postId, tags) =>
  request.put(`${postPrefix}/public/update/tags/${postId}`, tags);

// 查看文章
export const addViews = (postId) =>
  request.put(`${postPrefix}/public/add/views/${postId}`);

// 临时创建一个文章
export const savePost = (data) => request.post(`${postPrefix}/public/save`, data);