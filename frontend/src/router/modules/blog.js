export default {
  path: '/',
  component: () => import('@/views/layout/BlogLayout.vue'),
  children: [
    {
      path: '',
      name: 'Home',
      component: () => import('@/views/blog/Home.vue'),
      meta: { title: '首页' }
    },
    {
      path: 'posts',
      name: 'Posts',
      component: () => import('@/views/blog/PostList.vue'),
      meta: { title: '文章列表' }
    },
    {
      path: 'post/:id',
      name: 'PostDetail',
      component: () => import('@/views/blog/PostDetail.vue'),
      meta: { title: '文章详情' }
    },
    {
      path: 'categories',
      name: 'Categories',
      component: () => import('@/views/blog/Categories.vue'),
      meta: { title: '分类' }
    },
    {
      path: 'tags',
      name: 'Tags',
      component: () => import('@/views/blog/Tags.vue'),
      meta: { title: '标签' }
    },
    {
      path: 'about',
      name: 'About',
      component: () => import('@/views/blog/About.vue'),
      meta: { title: '关于' }
    }
  ]
}