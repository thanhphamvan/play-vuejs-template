import Vue from 'vue';
import Component from 'vue-class-component';
import axios, {AxiosResponse} from 'axios';
import {EventsManager, vueMain} from '../../main';
import {HomeComponent} from "../home/home";

interface LoginForm {
  email: string;
  password: string;
}

@Component({
  template: require('./Login.html')
})
export class LoginComponent extends Vue {

  protected email: string;
  protected password: string;

  protected fullName: string;
  protected suEmail: string;
  protected suPassword: string;

  private isAuthenticated: boolean;

  protected axios;

  constructor() {
    super();
    this.axios = axios;
    this.email = this.password = this.fullName = this.suPassword = this.suEmail = null;
    this.isAuthenticated = false;
  }

  success = (email: string) => {
    EventsManager.emit("logged_in", "/information");
    vueMain.$router.addRoutes([{
      path: "/information",
      component: HomeComponent
    }]);
    vueMain.$router.push("/information")
  };

  private login() {
    console.log('Login Clicked!');
    this.axios({
      method: 'post',
      url: 'http://localhost:9000/account/login',
      data: {
        email: this.email,
        password: this.password
      }
    })
      .then(
        (response) => {
          console.log(response);
          this.success(this.email);
        }
      )
      .catch(
        (error) => {
          console.log(error);
        }
      )
  }

  private signup() {
    this.axios({
      method: 'post',
      url: 'http://localhost:9000/account/signup',
      data: {
        fullName: this.fullName,
        email: this.suEmail,
        password: this.suPassword
      }
    })
      .then(
        (response) => {
          console.log(response);
          this.success(this.suEmail);
        }
      )
      .catch(
        (error) => {
          console.log(error);
        }
      )
  }
}
