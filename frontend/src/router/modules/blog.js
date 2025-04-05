import {
  BLOG_HOME_PATH,
  BLOG_POSTS_PATH,
  BLOG_POST_DETAIL_PATH,
  BLOG_CATEGORIES_PATH,
  BLOG_TAGS_PATH,
  BLOG_ABOUT_PATH
} from '@/constants/routes-constants.js'

export default {
  path: BLOG_HOME_PATH,
  component: () => import('@/views/layout/BlogLayout.vue'),
  children: [
    {
      path: '',
      name: 'Home',
      component: () => import('@/views/blog/Home.vue'),
      meta: {
        title: '首页',
        public: true
      }
    },
    {
      path: BLOG_POSTS_PATH.slice(1),
      name: 'Posts',
      component: () => import('@/views/blog/PostList.vue'),
      meta: {
        title: '文章列表',
        public: true
      }
    },
    {
      path: BLOG_POST_DETAIL_PATH.slice(1),
      name: 'PostDetail',
      component: () => import('@/views/blog/PostDetail.vue'),
      meta: {
        title: '文章详情',
        public: true
      }
    },
    {
      path: BLOG_CATEGORIES_PATH.slice(1),
      name: 'Categories',
      component: () => import('@/views/blog/Categories.vue'),
      meta: {
        title: '分类',
        public: true
      }
    },
    {
      path: BLOG_TAGS_PATH.slice(1),
      name: 'Tags',
      component: () => import('@/views/blog/Tags.vue'),
      meta: {
        title: '标签',
        public: true
      }
    },
    {
      path: BLOG_ABOUT_PATH.slice(1),
      name: 'About',
      component: () => import('@/views/blog/About.vue'),
      meta: {
        title: '关于',
        public: true
      }
    }
  ]
}