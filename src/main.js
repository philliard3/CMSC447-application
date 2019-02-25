import "babel-polyfill";
import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import "./plugins/vuetify";
import "vuetify/dist/vuetify.min.css";
import Vuetify from "vuetify/lib";

Vue.use(Vuetify, {
	primary: "#4caf50",
	secondary: "#2196f3",
	accent: "#3f51b5",
	error: "#f44336",
	warning: "#ffc107",
	info: "#607d8b",
	success: "#8bc34a"
});

Vue.config.productionTip = false;

new Vue({
	router,
	store,
	render: h => h(App)
}).$mount("#app");
