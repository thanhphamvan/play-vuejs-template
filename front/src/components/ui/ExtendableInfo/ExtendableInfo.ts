import Vue from 'vue';
import Component from 'vue-class-component';

import './ExtendableInfo.scss';

@Component({
  name: 'extendable-info',
  template: require('./ExtendableInfo.html'),
  props: {
    propMessage: String
  }
})
export class ExtendableInfo extends Vue {
  propMessage: string;

  isVisible: boolean = true;

  changeVisible() {
    this.isVisible = !this.isVisible;
  }

}
