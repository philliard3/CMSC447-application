import Vue from "vue";
import VueMq from "vue-mq";

Vue.use(VueMq, {
	breakpoints: {
		sm: 450,
		md: 850,
		lg: Infinity
	}
});
