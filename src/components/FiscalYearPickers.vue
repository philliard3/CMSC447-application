<template>
	<v-form>
		<v-container>
			<v-card>
				<v-card-text>
					<StartEndPicker
						title="Create new Fiscal Year"
						@picked="recordFiscalYearData"
					/>
					<StartEndPicker
						title="Create new Schedule Block"
						@picked="recordScheduleBlockData"
					/>
					<v-btn @click="reportDates" color="success">Submit</v-btn>
				</v-card-text>
			</v-card>
		</v-container>
	</v-form>
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
			const fyID = new Date().getTime();
			const sbID = new Date().getTime();

			if (
				this.$store.getters.fiscalYearExists(fyID) === false &&
				this.$store.getters.fiscalYearExistsWithName(
					this.fiscalYearData.name
				) === false &&
				this.$store.getters.scheduleBlockExists(sbID) === false
			) {
				const currentScheduleBlock = this.$store.getters.currentScheduleBlock;
				if (currentScheduleBlock === null) {
					// initialize vuex store data if there are no fiscal years
					this.$store.commit("initialize", {
						scheduleBlockData: { sbID, ...this.scheduleBlockData },
						fiscalYearData: { fyID, ...this.fiscalYearData }
					});
					// reroute once the changes are committed
					this.$router.push("/fiscalyear/constraints");
				} else {
					// make a new fiscal year if the store has been initialized
					this.$store.commit("addFiscalYear", {
						scheduleBlockData: { sbID, ...this.scheduleBlockData },
						fiscalYearData: { fyID, ...this.fiscalYearData }
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
