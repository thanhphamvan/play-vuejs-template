import Vue from 'vue';
import {Component, Watch} from 'vue-property-decorator';
import {EventListener, EventsManager} from "../../main";
import {Link} from './link';
import {Logger} from '../../util/log';

@Component({
  template: require('./navbar.html')
})
export class NavbarComponent extends Vue implements EventListener {

  protected logger: Logger;

  inverted: boolean = true; // default value

  object: { default: string } = {default: 'Default object property!'}; // objects as default values don't need to be wrapped into functions

  links: Link[] = [
    new Link('Login', '/')
  ];

  @Watch('$route.path')
  pathChanged() {
    this.logger.info('Changed current path to: ' + this.$route.path);
  }

  constructor() {
    super();
    EventsManager.on("logged_in", this);
  }

  mounted() {
    if (!this.logger) this.logger = new Logger();
    this.$nextTick(() => this.logger.info(this.object.default));
  }

  actionPerformer(event, parameter) {
    if (event === 'logged_in') {
      this.links.push(
        new Link('Information', '/information')
      )
    }
  }
}
