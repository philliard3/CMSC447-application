<template>
	<div class="container">
		<StartEndPicker
			title="Create new Fiscal Year"
			@picked="recordFiscalYearData"
		/>
		<StartEndPicker
			title="Create new Schedule Block"
			@picked="recordScheduleBlockData"
		/>
		<div>
			<v-btn @click="reportDates" color="success">Submit</v-btn>
		</div>
	</div>
</template>

<script>
import StartEndPicker from "./StartEndPicker.vue";

/*
 * Component to initialize the data using a new fiscal year.
 */

export default {
	name: "InitializePickers",
	components: { StartEndPicker },
	data() {
		const currentDate = new Date();
		return {
			scheduleBlockData: {
				startDate: currentDate,
				endDate: currentDate
			},
			fiscalYearData: {
				startDate: currentDate,
				endDate: currentDate
			}
		};
	},
	methods: {
		reportDates() {
			if (
				!this.$store.getters.fiscalYearExists(this.fiscalYearData.name) &&
				!this.$store.getters.scheduleBlockExists(this.scheduleBlockData.name)
			) {
				this.$store.commit("initialize", {
					scheduleBlockData: { ...this.scheduleBlockData },
					fiscalYearData: { ...this.fiscalYearData }
				});
				// reroute once the changes are committed
				this.$router.push("/");
			}
		},
		recordScheduleBlockData(newData) {
			this.scheduleBlockData = newData;
		},
		recordFiscalYearData(newData) {
			this.fiscalYearData = newData;
		}
	}
};
</script>

<style>
.container {
	padding: 20px;
}
</style>
