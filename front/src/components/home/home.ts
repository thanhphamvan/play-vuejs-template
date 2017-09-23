import Vue from 'vue';
import Component from 'vue-class-component';

import './home.scss';

interface Dependency {
  package: string;
  repo: string;
}

interface Author {
  name: string;
  origin: string;
  position: string;
}

@Component({
  template: require('./home.html')
})
export class HomeComponent extends Vue {

  taiTT: Author = {
    name: 'To Tan Tai',
    origin: 'k60.hus.edu',
    position: 'Front-End Developer'
  };

  authors: Author[] = [
    {
      name: 'Thanh Pham Van',
      origin: 'k60.hus.edu',
      position: 'Back-End Developer'
    }
//    , this.taiTT
  ];

  main: Dependency = {
    package: 'play-scala-vue-typescript-webpack',
    repo: 'https://github.com/thanhphamvan/play-scala-vue-typescript-webpack'
  };

  dependencies: Dependency[] = [
    {
      package: 'vue-webpack-typescript',
      repo: 'https://github.com/ducksoupdev/vue-webpack-typescript'
    },
    {
      package: 'play-vue-webpack',
      repo: 'https://github.com/gbogard/play-vue-webpack'
    }
  ];

  mode: string = process.env.ENV;

}
