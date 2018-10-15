import App from '../App'

const home = r => require.ensure([], () => r(require('../page/home/home')), 'home')
const msite = r => require.ensure([], () => r(require('../page/msite/msite')), 'msite')
const search = r => require.ensure([], () => r(require('../page/search/search')), 'search')
const shop = r => require.ensure([], () => r(require('../page/shop/shop')), 'shop')
const login = r => require.ensure([], () => r(require('../page/login/login')), 'login')
const profile = r => require.ensure([], () => r(require('../page/profile/profile')), 'profile')
const recommend = r => require.ensure([], () => r(require('../page/recommend/recommend')), 'recommend')

export default [{
    path: '/',
    component: App, //Top router for index.html
    children: [ //Second router for App.vue
        // default page
        {
            path: '',
            redirect: '/home'
        },
        {
            path: '/home',
            component: home
        },
        //planned page
        {
            path: '/msite',
            component: msite,
            meta: { keepAlive: true },
        },
        //search page
        {
            path: '/search/:geohash',
            component: search
        },
        //recommend page
        {
            path: '/recommend',
            component: recommend
        },
        //shop page
        {
            path: '/shop',
            component: shop,
        },
        //login page
        {
            path: '/login',
            component: login
        },
        //profile page
        {
            path: '/profile',
            component: profile
        },
    ]
}]
