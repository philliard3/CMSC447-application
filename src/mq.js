import Vue from "vue";
import VueMq from "vue-mq";

Vue.use(VueMq, {
	breakpoints: {
		xs: 450,
		sm: 850,
		md: 1000,
		lg: Infinity
	}
});
