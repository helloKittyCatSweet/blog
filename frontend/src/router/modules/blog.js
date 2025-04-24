import {
  BLOG_HOME_PATH,
  BLOG_POSTS_PATH,
  BLOG_POST_DETAIL_PATH,
  BLOG_CATEGORIES_PATH,
  BLOG_TAGS_PATH,
  BLOG_ABOUT_PATH,
  BLOG_USER_DETAIL_PATH,
  BLOG_SEARCH_PATH,
  BLOG_CONTACT_PATH,
  BLOG_RSS_PATH,
} from '@/constants/routes-constants.js'
import { KeepAlive } from 'vue'

export default {
  path: BLOG_HOME_PATH,
  component: () => import('@/views/blog/BlogLayout.vue'),
  children: [
    {
      path: '',
      name: 'Home',
      component: () => import('@/views/blog/Home.vue'),
      meta: {
        title: '首页',
        public: true,
        keepAlive: true, // 添加缓存配置
      }
    },
    {
      path: BLOG_POSTS_PATH.slice(1),
      name: 'Posts',
      component: () => import('@/views/blog/PostList.vue'),
      meta: {
        title: '文章列表',
        public: true,
        keepAlive: true, // 添加缓存配置
      }
    },
    {
      path: BLOG_POST_DETAIL_PATH.slice(1),
      name: 'PostView',
      component: () => import('@/views/blog/PostView.vue'),
      meta: {
        title: '文章详情',
        public: true,
        keepAlive: true, // 添加缓存配置
      }
    },
    {
      path: BLOG_CATEGORIES_PATH.slice(1),
      name: 'Categories',
      component: () => import('@/views/blog/Categories.vue'),
      meta: {
        title: '分类',
        public: true,
        keepAlive: true, // 添加缓存配置
      }
    },
    {
      path: BLOG_TAGS_PATH.slice(1),
      name: 'Tags',
      component: () => import('@/views/blog/Tags.vue'),
      meta: {
        title: '标签',
        public: true,
        keepAlive: true, // 添加缓存配置
      }
    },
    {
      path: BLOG_ABOUT_PATH.slice(1),
      name: 'About',
      component: () => import('@/views/blog/About.vue'),
      meta: {
        title: '关于',
        public: true,
        keepAlive: true, // 添加缓存配置
      }
    },
    {
      path: BLOG_USER_DETAIL_PATH.slice(1),
      name: 'UserDetail',
      component: () => import('@/views/blog/UserDetail.vue'),
      meta: {
        title: '用户详情',
        public: true,
        keepAlive: true, // 添加缓存配置
      }
    },
    {
      path: '/rss',
      name: 'RSS',
      component: () => import('@/components/layout/Rss.vue'),
      meta: {
        title: 'RSS订阅',
        public: true
      }
    },
    {
      path: '/contact',
      name: 'Contact',
      component: () => import('@/components/layout/Contact.vue'),
      meta: {
        title: '联系我',
        public: true
      }
    }
  ]
}