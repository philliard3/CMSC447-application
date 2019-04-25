<template>
	<div class="container">
		<v-container grid-list-md text-xs-center>
			<v-layout row wrap>
				<v-flex xs2></v-flex>
				<v-flex xs3>
					<v-btn color="success" @click="loadFile"
						>Load Calendar from File</v-btn
					>
				</v-flex>
				<v-flex xs2></v-flex>
				<v-flex xs3>
					<v-btn color="info" to="/createfiscalyear">New Calendar</v-btn>
				</v-flex>
				<v-flex xs2></v-flex>
			</v-layout>
		</v-container>
	</div>
</template>

<script>
/*
 * Component to initialize the data using a new fiscal year.
 */

export default {
	name: "InitializePickers",
	data() {},
	methods: {
		// This function loads a file from local storage.
		// In the electron build, it should call to the main process.
		// In the webapp build, it should use the browser's file upload API.
		async loadFile() {
			// console.log(process.env);
		}
	}
};
</script>

<style>
.container {
	padding: 20px;
}
</style>
