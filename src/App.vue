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
							<v-list-tile to="/">
								<v-icon>home</v-icon>
								<v-list-tile-title>Start</v-list-tile-title>
							</v-list-tile>
							<v-list-tile to="/createfiscalyear">
								<v-icon>library_add</v-icon>
								<v-list-tile-title>New FY</v-list-tile-title>
							</v-list-tile>
							<v-list-tile to="/fiscalyearconstraints">
								<v-icon>ballot</v-icon>
								<v-list-tile-title>FY Constraints</v-list-tile-title>
							</v-list-tile>
							<v-list-tile to="/createscheduleblock">
								<v-icon>note_add</v-icon>
								<v-list-tile-title>New Schedule Block</v-list-tile-title>
							</v-list-tile>
							<v-list-tile to="/manage">
								<v-icon>people</v-icon>
								<v-list-tile-title>Manage Employees</v-list-tile-title>
							</v-list-tile>
							<v-list-tile to="/calendar">
								<v-icon>calendar_today</v-icon>
								<v-list-tile-title>Calendar</v-list-tile-title>
							</v-list-tile>
						</v-list>
					</v-menu>
				</v-toolbar-items>
				<v-toolbar-items v-if="$mq === 'md' || $mq === 'lg'">
					<v-btn flat @click="lastPage">
						<v-icon size="18px">arrow_back_ios</v-icon>
					</v-btn>
					<v-btn flat to="/"> <v-icon dark>home</v-icon>Start </v-btn>
					<v-btn flat to="/createfiscalyear">
						<v-icon dark>library_add</v-icon>New Fiscal Year
					</v-btn>
					<v-btn flat to="/fiscalyearconstraints">
						<v-icon dark>ballot</v-icon>FY Constraints
					</v-btn>
					<v-btn flat to="/createscheduleblock">
						<v-icon dark>note_add</v-icon>New Schedule Block
					</v-btn>
					<v-btn flat to="/schedlueblockconstraints">
						<v-icon dark>ballot</v-icon>Schedule Block Constraints
					</v-btn>
					<v-btn flat to="/manage">
						<v-icon dark>people</v-icon>Manage Employees
					</v-btn>
					<v-btn flat to="/calendar">
						<v-icon dark>calendar_today</v-icon>Calendar View
					</v-btn>
				</v-toolbar-items>
			</v-toolbar>
			<router-view />
		</v-app>
	</div>
</template>

<script>
export default {
	name: "App",
	data() {
		return { electron: Boolean(process.env.browser) };
	},
	methods: {
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
