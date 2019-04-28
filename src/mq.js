import Vue from "vue";
import VueMq from "vue-mq";

Vue.use(VueMq, {
	breakpoints: {
		sm: 500,
		md: 650,
		lg: Infinity
	}
});
