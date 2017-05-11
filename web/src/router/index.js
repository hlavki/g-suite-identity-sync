import Vue from 'vue'
import Router from 'vue-router'
import SignIn from '@/components/SignIn'
import About from '@/components/About'
import CreateAccount from '@/components/CreateAccount'
import Home from '@/components/Home'
import NotFound from '@/components/NotFound'

Vue.use(Router)

export default new Router({
  // mode: 'history',
  // base: '/xit',
  routes: [
    {
      path: '/',
      name: 'Home',
      meta: { auth: true },
      component: Home
    },
    {
      path: '/sign-in',
      name: 'SignIn',
      meta: { auth: false },
      component: SignIn
    },
    {
      path: '/create-account',
      name: 'CreateAccount',
      meta: { auth: true },
      component: CreateAccount
    },
    {
      path: '/about',
      name: 'About',
      meta: { auth: true },
      component: About
    },
    {
      path: '*',
      name: 'NotFound',
      meta: { auth: false },
      component: NotFound
    }
  ]
})
