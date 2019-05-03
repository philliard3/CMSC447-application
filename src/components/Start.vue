<template>
	<v-container grid-list-md text-xs-center align-center>
		<v-card>
			<v-card-text>
				<v-container
					id="loading-info"
					class="title font-weight-medium"
					v-if="currentBlock === null || currentBlock.sbID === null"
				>
					It looks like you don't have anything loaded yet. Select an option
					below to get started.
					<p />
					<v-btn color="success" @click="loadFile"
						>Load Calendar from File</v-btn
					>
					<p />
					<v-btn color="info" to="/fiscalyear/create">New Calendar</v-btn>
				</v-container>
				<v-container id="loading-info" class="title font-weight-medium" v-else>
					<v-container>You've already started on a calendar.</v-container>
					<v-btn color="info" to="/fiscalyear/create">New Fiscal Year</v-btn>
				</v-container>
			</v-card-text>
		</v-card>
	</v-container>
</template>

<script>
export default {
	name: "Start",
	data() {
		return {
			currentBlock: this.$store.getters.currentScheduleBlock
		};
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
<style>
#loading-info {
	text-align: center;
	max-width: 450px;
	margin: auto;
}
</style>
