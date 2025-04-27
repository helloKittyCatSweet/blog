import { ROLES } from '@/constants/role-constants';

const externalRoutes = [
  {
    path: '/kibana-dashboard',
    name: 'KibanaDashboard',
    component: {
      beforeRouteEnter(to, from, next) {
        window.open('http://localhost:5601/app/dashboards#', '_blank');
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
        window.open('http://localhost:5601/app/management/data/index_management/indices', '_blank');
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
        window.open('http://localhost:5601/app/management/kibana/dataViews', '_blank');
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
        window.open('http://localhost:8080/swagger-ui/index.html', '_blank');
        next(false);
      }
    },
    meta: {
      roles: [ROLES.SYSTEM_ADMINISTRATOR]
    }
  }
];

export default externalRoutes;