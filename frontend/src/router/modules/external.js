import { ROLES } from '@/constants/role-constants';

// 获取环境变量或使用默认值
const KIBANA_URL = import.meta.env.VITE_KIBANA_URL || 'http://localhost:5601';
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const externalRoutes = [
  {
    path: '/kibana-dashboard',
    name: 'KibanaDashboard',
    component: {
      beforeRouteEnter(to, from, next) {
        window.open(`${KIBANA_URL}/app/dashboards#`, '_blank');
        next(false);
      }
    },
    meta: {
      roles: [ROLES.SYSTEM_ADMINISTRATOR]
    }
  },
  {
    path: '/kibana-indices',
    name: 'KibanaIndices',
    component: {
      beforeRouteEnter(to, from, next) {
        window.open(`${KIBANA_URL}/app/management/data/index_management/indices`, '_blank');
        next(false);
      }
    },
    meta: {
      roles: [ROLES.SYSTEM_ADMINISTRATOR]
    }
  },
  {
    path: '/kibana-dataviews',
    name: 'KibanaDataViews',
    component: {
      beforeRouteEnter(to, from, next) {
        window.open(`${KIBANA_URL}/app/management/kibana/dataViews`, '_blank');
        next(false);
      }
    },
    meta: {
      roles: [ROLES.SYSTEM_ADMINISTRATOR]
    }
  },
  {
    path: '/swagger-api',
    name:'SwaggerApi',
    component: {
      beforeRouteEnter(to, from, next) {
        window.open(`${API_URL}/swagger-ui/index.html`, '_blank');
        next(false);
      }
    },
    meta: {
      roles: [ROLES.SYSTEM_ADMINISTRATOR]
    }
  }
];

export default externalRoutes;