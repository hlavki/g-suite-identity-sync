import Vue from "vue";
import Router from "vue-router";
import SignIn from '@/views/SignIn';
import About from '@/views/About';
import CreateAccount from '@/views/CreateAccount';
import Home from '@/views/Home';
import NotFound from '@/views/NotFound';
import AuthProgress from '@/views/AuthProgress';
import Settings from '@/views/Settings';

Vue.use(Router);

export default new Router({
  // mode: "history",
  base: process.env.BASE_URL,
  routes: [
    {
      path: "/",
      name: "Home",
      meta: { auth: true },
      component: Home
    },
    {
      path: "/sign-in",
      name: "SignIn",
      meta: { auth: false },
      component: SignIn
    },
    {
      path: "/settings",
      name: "Settings",
      meta: { auth: true },
      component: Settings
    },
    {
      path: "/create-account",
      name: "CreateAccount",
      meta: { auth: true },
      component: CreateAccount
    },
    {
      path: "/about",
      name: "About",
      meta: { auth: true },
      component: About
    },
    {
      path: "/auth-wait",
      name: "AuthProgress",
      meta: { auth: false },
      component: AuthProgress
    },
    {
      path: "*",
      name: "NotFound",
      meta: { auth: false },
      component: NotFound
    }
  ]
});
