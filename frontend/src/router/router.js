import Main from '@/views/Main.vue';

// 不作为Main组件的子页面展示的页面单独写，如下
export const loginRouter = {
        path: '/login',
        name: 'login',
        meta: {
            title: '登录',
            noAuth: true
        },
        component: () => import('@/views/login.vue')
};

export const registerRouter = {
        path: '/register',
        name: 'register',
        meta: {
            title: '注册',
            noAuth: true
        },
        component: () => import('@/views/register.vue')
};


export const loanApplyRouter = {
        path: '/loan/apply',
        name: 'loan_apply',
        meta: {
            title: '融资申请-医伴金服',
            noAuth: true
        },
        component: () => import('@/views/loan/apply.vue')
};

export const page404 = {
        path: '/*',
        name: 'error-404',
        meta: {
            title: '404-页面不存在',
            noAuth: true
        },
        component: () => import('@/views/error-page/404.vue')
};

export const page403 = {
        path: '/403',
        meta: {
            title: '403-权限不足',
            noAuth: true
        },
        name: 'error-403',
        component: () => import('@//views/error-page/403.vue')
};

export const page500 = {
        path: '/500',
        meta: {
            title: '500-服务端错误',
            noAuth: true
        },
        name: 'error-500',
        component: () => import('@/views/error-page/500.vue')
};

export const preview = {
        path: '/preview',
        name: 'preview',
        component: () => import('@/views/form/article-publish/preview.vue')
};

export const locking = {
        path: '/locking',
        name: 'locking',
        component: () => import('@/views/main-components/lockscreen/components/locking-page.vue')
};

// 作为Main组件的子页面展示但是不在左侧菜单显示的路由写在otherRouter里
export const otherRouter = {
    path: '/',
    name: 'otherRouter',
    redirect: '/home',
    component: Main,
    children: [
        { path: 'home', title: {i18n: 'home'}, name: 'home_index', component: () => import('@/views/home/home.vue') },
{ path: 'goods/:goods_id', title: '商品详情', name: 'goods-info', component: () => import('@/views/basic-data/goods-info.vue') }, // 商品详情
{ path: 'factory/:factory_id', title: '生产企业详情', name: 'factory-info', component: () => import('@/views/basic-data/factory-info.vue') }, // 生产企业详情
{ path: 'supplier/:supplier_id', title: '供应商详情', name: 'supplier-info', component: () => import('@/views/basic-data/supplier-info.vue') }, // 供应商详情
{ path: 'ownspace', title: '个人中心', name: 'ownspace_index', component: () => import('@/views/own-space/own-space.vue') },
{ path: 'order/:order_id', title: '订单详情', name: 'order-info', component: () => import('@/views/advanced-router/component/order-info.vue') }, // 用于展示动态路由
{ path: 'shopping', title: '购物详情', name: 'shopping', component: () => import('@/views/advanced-router/component/shopping-info.vue') }, // 用于展示带参路由
{ path: 'message', title: '消息中心', name: 'message_index', component: () => import('@/views/message/message.vue') },
]
};

// 作为Main组件的子页面展示并且在左侧菜单显示的路由写在appRouter里
export const appRouter = [
    {
        path: '/basic-data',
        icon: 'cube',
        name: 'basic-data',
        title: '基本资料',
        component: Main,
        children: [
            { path: 'goods', title: '商品档案', name: 'basic_data_good', component: () => import('@/views/basic-data/good.vue') },
            { path: 'factory', title: '生产企业', name: 'basic_data_factory', component: () => import('@/views/basic-data/factory.vue') },
            { path: 'supplier', title: '供应商', name: 'basic_data_supplier', component: () => import('@/views/basic-data/supplier.vue') },
            { path: 'customer', title: '客户档案', name: 'basic_data_customer', component: () => import('@/views/basic-data/customer.vue') },
            { path: 'buyer', title: '采购员', name: 'basic_data_buyer', component: () => import('@/views/basic-data/buyer.vue') },
            { path: 'sale', title: '销售员', name: 'basic_data_sale', component: () => import('@/views/basic-data/sale.vue') },
            { path: 'price', title: '价格设置', name: 'basic_data_price', component: () => import('@/views/basic-data/price.vue') },
            { path: 'file', title: '档案管理', name: 'basic_data_file', component: () => import('@/views/basic-data/file.vue') },
            { path: 'ship', title: '运输公司档案', name: 'basic_data_ship', component: () => import('@/views/basic-data/ship.vue') }
        ]
    },
{
    path: '/buy',
    icon: 'bag',
    name: 'buy',
    title: '采购',
    component: Main,
    children: [
        { path: 'order', title: '采购制单', name: 'buy_order', component: () => import('@/views/buy/buy-order.vue') },
        { path: 'review', title: '采购单审核', name: 'buy_order_review', component: () => import('@/views/buy/buy-order-list.vue') },
        { path: 'receive', title: '采购收货', name: 'buy_receive', component: () => import('@/views/buy/buy-receive.vue') },
    ]
},
{
    path: '/sell',
    icon: 'ios-calculator',
    name: 'sell',
    title: '销售',
    component: Main,
    children: [
        { path: 'order', title: '销售制单', name: 'sell_order', component: () => import('@/views/sell/make.vue') },
        { path: 'quality-review', title: '销售出库质量审核', name: 'sell-quality-review', component: () => import('@/views/sell/sell-quality-review.vue') },
        { path: 'list', title: '销售订单列表', name: 'sell_list', component: () => import('@/views/sell/sell-order-list.vue') },
    ]
},
{
    path: '/warehouse',
    icon: 'social-buffer',
    name: 'warehouse',
    title: '库存',
    component: Main,
    children: [
        { path: 'setting', title: '仓库点设置', name: 'wh_setting', component: () => import('@/views/warehouse/setting.vue') },
        { path: 'overview', title: '库存报表', name: 'wh_overview', component: () => import('@/views/warehouse/overview.vue') },
    ]
},
{
    path: '/access',
        icon: 'key',
    name: 'access',
    title: '权限管理',
    component: Main,
    children: [
        { path: 'index', title: '权限管理', name: 'access_index', component: () => import('@/views/access/access.vue') }
    ]
},
{
    path: '/access-test',
        icon: 'lock-combination',
    title: '权限测试页',
    name: 'accesstest',
    access: 0,
    component: Main,
    children: [
        { path: 'index', title: '权限测试页', name: 'accesstest_index', access: 0, component: () => import('@/views/access/access-test.vue') }
    ]
},
{
    path: '/international',
        icon: 'earth',
    title: {i18n: 'international'},
    name: 'international',
        component: Main,
    children: [
    { path: 'index', title: {i18n: 'international'}, name: 'international_index', component: () => import('@/views/international/international.vue') }
]
},
{
    path: '/component',
        icon: 'social-buffer',
    name: 'component',
    title: '组件',
    component: Main,
    children: [
    {
        path: 'text-editor',
        icon: 'compose',
        name: 'text-editor',
        title: '富文本编辑器',
        component: () => import('@/views/my-components/text-editor/text-editor.vue')
},
    {
        path: 'md-editor',
            icon: 'pound',
        name: 'md-editor',
        title: 'Markdown编辑器',
        component: () => import('@/views/my-components/markdown-editor/markdown-editor.vue')
    },
    {
        path: 'image-editor',
            icon: 'crop',
        name: 'image-editor',
        title: '图片预览编辑',
        component: () => import('@/views/my-components/image-editor/image-editor.vue')
    },
    {
        path: 'draggable-list',
            icon: 'arrow-move',
        name: 'draggable-list',
        title: '可拖拽列表',
        component: () => import('@/views/my-components/draggable-list/draggable-list.vue')
    },
    {
        path: 'area-linkage',
            icon: 'ios-more',
        name: 'area-linkage',
        title: '城市级联',
        component: () => import('@/views/my-components/area-linkage/area-linkage.vue')
    },
    {
        path: 'file-upload',
            icon: 'android-upload',
        name: 'file-upload',
        title: '文件上传',
        component: () => import('@/views/my-components/file-upload/file-upload.vue')
    },
    {
        path: 'count-to',
            icon: 'arrow-graph-up-right',
        name: 'count-to',
        title: '数字渐变',
        // component: () => import('@/views/my-components/count-to/count-to.vue')
        component: () => import('@/views/my-components/count-to/count-to.vue')
    },
    {
        path: 'split-pane-page',
            icon: 'ios-pause',
        name: 'split-pane-page',
        title: 'split-pane',
        component: () => import('@/views/my-components/split-pane/split-pane-page.vue')
    }
]
},
{
    path: '/form',
        icon: 'android-checkbox',
    name: 'form',
    title: '表单编辑',
    component: Main,
    children: [
    { path: 'artical-publish', title: '文章发布', name: 'artical-publish', icon: 'compose', component: () => import('@/views/form/article-publish/article-publish.vue') },
    { path: 'workflow', title: '工作流', name: 'workflow', icon: 'arrow-swap', component: () => import('@/views/form/work-flow/work-flow.vue') }

]
},
// {
//     path: '/charts',
//     icon: 'ios-analytics',
//     name: 'charts',
//     title: '图表',
//     component: Main,
//     children: [
//         { path: 'pie', title: '饼状图', name: 'pie', icon: 'ios-pie', component: resolve => { require('@/views/access/access.vue') },
//         { path: 'histogram', title: '柱状图', name: 'histogram', icon: 'stats-bars', component: resolve => { require('@/views/access/access.vue') }

//     ]
// },
{
    path: '/tables',
        icon: 'ios-grid-view',
    name: 'tables',
    title: '表格',
    component: Main,
    children: [
    { path: 'dragableTable', title: '可拖拽排序', name: 'dragable-table', icon: 'arrow-move', component: () => import('@/views/tables/dragable-table.vue') },
    { path: 'editableTable', title: '可编辑表格', name: 'editable-table', icon: 'edit', component: () => import('@/views/tables/editable-table.vue') },
    { path: 'searchableTable', title: '可搜索表格', name: 'searchable-table', icon: 'search', component: () => import('@/views/tables/searchable-table.vue') },
    { path: 'exportableTable', title: '表格导出数据', name: 'exportable-table', icon: 'code-download', component: () => import('@/views/tables/exportable-table.vue') },
    { path: 'table2image', title: '表格转图片', name: 'table-to-image', icon: 'images', component: () => import('@/views/tables/table-to-image.vue') }
]
},
{
    path: '/advanced-router',
        icon: 'ios-infinite',
    name: 'advanced-router',
    title: '高级路由',
    component: Main,
    children: [
    { path: 'mutative-router', title: '动态路由', name: 'mutative-router', icon: 'link', component: () => import('@/views/advanced-router/mutative-router.vue') },
    { path: 'argument-page', title: '带参页面', name: 'argument-page', icon: 'android-send', component: () => import('@/views/advanced-router/argument-page.vue') }
]
},
{
    path: '/error-page',
        icon: 'android-sad',
    title: '错误页面',
    name: 'errorpage',
    component: Main,
    children: [
    { path: 'index', title: '错误页面', name: 'errorpage_index', component: () => import('@/views/error-page/error-page.vue') }
]
}
];

// 所有上面定义的路由都要写在下面的routers里
export const routers = [
        loginRouter,
        registerRouter,
        loanApplyRouter,
        otherRouter,
        preview,
        locking,
        ...appRouter,
        page500,
        page403,
        page404
];

