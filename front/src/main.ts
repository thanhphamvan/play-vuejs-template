import Vue from 'vue';
import VueRouter from 'vue-router';

import './sass/main.scss';

import {HomeComponent} from './components/home';
import {AboutComponent} from './components/about';
import {ListComponent} from './components/list';
import {NavbarComponent} from './components/navbar';
import {ThanhPVComponent} from './components/thanhpv';

// register the plugin
Vue.use(VueRouter);

export interface EventListener {
  actionPerformer(event, parameter);
}

export class EventsManager {
  static listeners = new Map();

  static on(event: String, listener: EventListener) {
    let list = EventsManager.listeners.get(event);
    if (!list)
      EventsManager.listeners.set(event, [listener]);
    else {
      list.push(listener)
      EventsManager.listeners.set(event, list);
    }
  }

  static emit(event, parameter) {
    let list : EventListener[] = EventsManager.listeners.get(event);
    if (list)
      list.forEach( (listener : EventListener) => listener.actionPerformer(event, parameter) );
  }
}

export let router = new VueRouter({
  routes: [
    {path: '/information', component: HomeComponent},
    {path: '/', component: ThanhPVComponent}
  ]
});

export let vueMain = new Vue({
  el: '#app-main',
  router: router,
  components: {
    'navbar': NavbarComponent
  }
});
