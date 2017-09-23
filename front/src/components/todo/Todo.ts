import * as Vue from "vue";
import Component from "vue-class-component";
import axios, {AxiosResponse} from "axios";

interface TodoItem {
  _id: {
    $oid: string
  };
  title: string;
  completed?: boolean;
}

@Component({
  name: 'todo-list',
  template: require('./todo.html')
})
export class TodoComponent extends Vue {

  todoItems: TodoItem[];

  private api_url = 'localhost:9000/todos';
  protected axios;

  mounted() {
    this.$nextTick(
      () => {
        this.loadItems();
      }
    )
  }

  private loadItems() {
    if (!this.todoItems.length) {
      this.axios.get(this.api_url).then((response: AxiosResponse) => {
        this.todoItems = response.data;
      }, (error) => {
        console.error(error);
      })
    }
  }
}
