<template>
	<div class="container">
		<StartEndPicker
			title="Create new Schedule Block"
			@picked="recordScheduleBlockData"
		/>
		<StartEndPicker
			title="Create new Fiscal Year"
			@picked="recordFiscalYearData"
		/>
		<div>
			<v-btn @click="reportDates" color="success">Submit</v-btn>
		</div>
	</div>
</template>

<script>
import StartEndPicker from "./StartEndPicker.vue";

/*
 * Component to create a new fiscal year, which requires a new schedule block.
 */

export default {
	name: "FiscalYearPickers",
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
			this.$store.commit("initialize", {
				scheduleBlockData: {
					startDate: this.scheduleBlockData.startDate,
					endDate: this.scheduleBlockData.endDate
				},
				fiscalYearData: {
					startDate: this.fiscalYearData.startDate,
					endDate: this.fiscalYearData.endDate
				}
			});
			// reroute once the changes are committed
			this.$router.push("/");
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
