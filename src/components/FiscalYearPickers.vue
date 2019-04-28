<template>
	<v-container>
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
	</v-container>
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
			// disallow repeat names on fiscal years and schedule blocks
			if (
				!this.$store.getters.fiscalYearExists(this.fiscalYearData.name) &&
				!this.$store.getters.scheduleBlockExists(this.scheduleBlockData.name)
			) {
				const currentScheduleBlock = this.$store.getters.currentScheduleBlock;
				if (
					currentScheduleBlock === null ||
					currentScheduleBlock.sbID !== "empty"
				) {
					// initialize vuex store data if there are no fiscal years
					this.$store.commit("initialize", {
						scheduleBlockData: { ...this.scheduleBlockData },
						fiscalYearData: { ...this.fiscalYearData }
					});
					// reroute once the changes are committed
					this.$router.push("/fiscalyear/constraints");
				} else {
					// make a new fiscal year if the store has been initialized
					this.$store.commit("addFiscalYear", {
						scheduleBlockData: { ...this.scheduleBlockData },
						fiscalYearData: { ...this.fiscalYearData }
					});
					// reroute once the changes are committed
					this.$router.push("/fiscalyear/constraints");
				}
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
