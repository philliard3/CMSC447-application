import Vue from "vue";
import Router from "vue-router";
import Home from "./views/Home.vue";

Vue.use(Router);

export default new Router({
	mode: "history",
	base: process.env.BASE_URL,
	routes: [
		{
			path: "/",
			name: "home",
			component: Home
		},
		{
			path: "/jsx",
			name: "jsx",
			// lazy-loaded route
			component: () => import(/* webpackChunkName: "jsx" */ "./views/JSX.vue")
		},
		{
			path: "/initialize",
			name: "initialize",
			// lazy-loaded route
			component: () =>
				import(/* webpackChunkName: "initialize" */ "./views/Initialize.vue")
		},
		{
			path: "/createscheduleblock",
			name: "createscheduleblock",
			component: () =>
				import(/* webpackChunkName: "createscheduleblock" */ "./views/CreateScheduleBlock.vue")
		},
		{
			path: "/createfiscalyear",
			name: "createfiscalyear",
			component: () =>
				import(/* webpackChunkName: "createfiscalyear" */ "./views/CreateFiscalYear.vue")
		},
		{
			path: "/calendar",
			name: "calendar",
			component: () =>
				import(/* webpackChunkName: "createfiscalyear" */ "./views/CalendarView.vue")
		}
	]
});
