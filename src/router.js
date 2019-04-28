import Vue from "vue";
import Router from "vue-router";
import Start from "./views/Start.vue";

Vue.use(Router);

export default new Router({
	mode: "history",
	base: process.env.BASE_URL,
	routes: [
		{
			path: "/",
			name: "start",
			component: Start
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
			path: "/scheduleblock/create",
			name: "createscheduleblock",
			component: () =>
				import(/* webpackChunkName: "createscheduleblock" */ "./views/CreateScheduleBlock.vue")
		},
		{
			path: "/scheduleblock/constraints",
			name: "scheduleblockconstraints",
			component: () =>
				import(/* webpackChunkName: "createscheduleblock" */ "./views/CreateScheduleBlock.vue")
		},
		{
			path: "/fiscalyear/create",
			name: "createfiscalyear",
			component: () =>
				import(/* webpackChunkName: "createfiscalyear" */ "./views/CreateFiscalYear.vue")
		},
		{
			path: "/fiscalyear/constraints",
			name: "fiscalyearconstraints",
			component: () =>
				import(/* webpackChunkName: "createfiscalyear" */ "./views/CreateFiscalYear.vue")
		},
		{
			path: "/manage",
			name: "manage",
			component: () =>
				import(/* webpackChunkName: "manage" */ "./views/ManagePage.vue")
		},
		{
			/** ManagePage used as a placeholder **/
			path: "/manage/roles",
			name: "manageroles",
			component: () =>
				import(/* webpackChunkName: "manage" */ "./views/ManagePage.vue")
		},
		{
			/** ManagePage used as a placeholder **/
			path: "/manage/employees",
			name: "manageemployees",
			component: () =>
				import(/* webpackChunkName: "manage" */ "./views/ManagePage.vue")
		},
		{
			path: "/calendar",
			name: "calendar",
			component: () =>
				import(/* webpackChunkName: "createfiscalyear" */ "./views/CalendarView.vue")
		}
	]
});
