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
					<v-btn
						color="success"
						type="files"
						@click="$refs.fileUploadField.click()"
						>Load Calendar from File</v-btn
					>
					<input
						id="fileUploadField"
						ref="fileUploadField"
						type="file"
						@change="loadFile"
					/>
					<p />
					<v-btn color="info" to="/fiscalyear/create">New Calendar</v-btn>
				</v-container>
				<v-container id="loading-info" class="title font-weight-medium" v-else>
					<v-container>You've already started on a calendar.</v-container>
					<v-btn color="info" @click="clearStore">New Fiscal Year</v-btn>
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
		// In both builds, this uses the browser's file upload API.
		async loadFile() {
			const files = this.$refs.fileUploadField.files;
			const fileOfInterest = files[files.length - 1];
			if (
				fileOfInterest.name.substring(
					fileOfInterest.name.length - 5,
					fileOfInterest.name.length
				) !== ".json"
			) {
				return "The file was of incorrect file type";
			}

			const reader = new FileReader();
			reader.onload = () => {
				const fileJSON = reader.result;
				this.$store.commit("insertLoadedState", {
					loadedState: JSON.parse(fileJSON),
					sourceFile: fileOfInterest.name
				});
				this.$router.push("/manage");
			};
			reader.readAsText(files[files.length - 1]);
		},

		clearStore() {
			this.$store.commit("clearStore");
			this.$router.push("/fiscalyear/create");
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
#fileUploadField {
	display: none;
}
</style>
