<template>
	<v-container grid-list-md text-xs-center align-center>
		<v-layout row wrap>
			<v-flex v-if="$mq > 'sm'" xs1></v-flex>
			<v-flex xs3>
				<v-btn color="success" @click="loadFile">Load Calendar from File</v-btn>
			</v-flex>
			<v-flex v-if="$mq <= 'sm'" xs2></v-flex>
			<v-flex xs1></v-flex>
			<v-flex xs3>
				<v-btn color="info" to="/createfiscalyear">New Calendar</v-btn>
			</v-flex>
		</v-layout>
	</v-container>
</template>

<script>
export default {
	name: "Start",
	data() {
		return {};
	},
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
