<template>
	<div id="app">
		<v-app>
			<v-toolbar>
				<v-toolbar-items>
					<v-menu offset-y>
						<template v-slot:activator="{ on }">
							<v-icon v-on="on">menu</v-icon>
						</template>
						<v-list>
							<v-list-tile @click="lastPage">
								<v-icon>arrow_back_ios</v-icon>
								<v-list-tile-title>Last Page</v-list-tile-title>
							</v-list-tile>
							<v-list-tile
								v-for="link in navLinks"
								:key="link.url"
								:to="link.url"
							>
								<v-icon>{{ link.icon }}</v-icon>
								<v-list-tile-title>{{ link.smallText }}</v-list-tile-title>
							</v-list-tile>
						</v-list>
					</v-menu>
				</v-toolbar-items>
				<v-toolbar-items v-if="$mq === 'md' || $mq === 'lg'">
					<!-- add an extra back button in the Electron mode -->
					<v-btn v-if="electron" @click="lastPage" flat>
						<v-icon size="18px">arrow_back_ios</v-icon>
					</v-btn>
					<v-btn v-for="link in navLinks" :key="link.url" :to="link.url" flat>
						<v-icon dark>{{ link.icon }}</v-icon>
						{{ link.text }}
					</v-btn>
				</v-toolbar-items>
			</v-toolbar>
			<router-view />
			<v-container>
				<v-flex xs6>
					<v-btn @click="lastPage" color="primary">
						<v-icon dark small>arrow_back_ios</v-icon>Last Page
					</v-btn>
				</v-flex>
			</v-container>
		</v-app>
	</div>
</template>

<script>
export default {
	name: "App",
	data() {
		return {
			electron: Boolean(process.env.browser),
			navLinks: [
				{
					url: "/",
					text: "Start",
					smallText: "Start",
					icon: "home"
				},
				{
					url: "/fiscalyear/create",
					text: "New Fiscal Year",
					smallText: "New FY",
					icon: "library_add"
				},
				{
					url: "/fiscalyear/constraints",
					text: "Fiscal Year Rules",
					smallText: "FY Rules",
					icon: "ballot"
				},
				{
					url: "/scheduleblock/create",
					text: "New Schedule Block",
					smallText: "New Schedule Block",
					icon: "note_add"
				},
				{
					url: "/scheduleblock/constraints",
					text: "Schedule Block Rules",
					smallText: "Schedule Block Rules",
					icon: "ballot"
				},
				{
					url: "/manage",
					text: "Manage Employees",
					smallText: "Manage Employees",
					icon: "people"
				},
				{
					url: "/calendar",
					text: "Review & Export",
					smallText: "Review",
					icon: "calendar_today"
				}
			]
		};
	},
	methods: {
		// This is a back command, which is helpful in the Electron app
		// because the users may not be aware that they can use web-based navigation functionality
		lastPage() {
			this.$router.go(-1);
		}
	}
};
</script>

<style>
#app {
	font-family: "Avenir", Helvetica, Arial, sans-serif;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
	text-align: center;
	color: #2c3e50;
}
#nav {
	padding: 30px;
}

#nav a {
	font-weight: bold;
	color: #2c3e50;
}

#nav a.router-link-exact-active {
	color: #42b983;
}
</style>
