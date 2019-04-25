<template>
	<div id="app">
		<v-app>
			<v-toolbar>
				<v-toolbar-items>
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
					<v-btn flat to="/manageemployees">
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
