import Vue from 'vue';
import Component from 'vue-class-component';
import axios, {AxiosResponse} from 'axios';

import {LoginComponent} from '../login';

interface TodoItem {
  title: string;
  completed?: boolean;
}

@Component({
  template: require('./thanhpv.html'),
  components: {
    'login-form': LoginComponent
  }
})
export class ThanhPVComponent extends Vue {

  protected axios;

  constructor() {
    super();
    this.axios = axios;
  }

}
