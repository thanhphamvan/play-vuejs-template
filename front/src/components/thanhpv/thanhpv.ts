import Vue from 'vue';
import Component from 'vue-class-component';
import axios, {AxiosResponse} from 'axios';

interface TodoItem {
  title: string;
  completed?: boolean;
}

@Component({
  template: require('./thanhpv.html')
})
export class ThanhPVComponent extends Vue {

  protected axios;
  private url = 'http://localhost:9000/todos';
  items: TodoItem[] = [];

  constructor() {
    super();
    this.axios = axios;
  }

  mounted() {
    this.$nextTick(() => {
        this.loadTodo();
      }
    );
  }

  private loadTodo() {
    if (!this.items.length) {
      this.axios.get(this.url).then(
        (response: AxiosResponse) => {
          this.items = response.data;
        },
        error => {
          console.log(error);
        }
      );
    }
  }

}
