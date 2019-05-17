<template>
	<v-form>
		<v-container>
			<v-card>
				<v-card-text>
					<StartEndPicker
						title="Create new Schedule Block"
						@picked="recordScheduleBlockData"
					/>
					<v-container>
						<v-layout>
							<v-flex xs12 sm6 d-flex>
								<FiscalYearSelector></FiscalYearSelector>
							</v-flex>
						</v-layout>
					</v-container>
					<v-btn @click="reportDates" color="success">Submit</v-btn>
				</v-card-text>
			</v-card>
		</v-container>
	</v-form>
</template>

<script>
import StartEndPicker from "./StartEndPicker.vue";
import FiscalYearSelector from "./FiscalYearSelector";

export default {
	name: "ScheduleBlockPickers",
	components: { StartEndPicker, FiscalYearSelector },
	data() {
		const currentDate = new Date();
		return {
			scheduleBlockData: {
				startDate: currentDate,
				endDate: currentDate
			},
			selectedFiscalYear: null
		};
	},
	created() {
		if (this.$store.getters.currentFiscalYear === null) {
			this.$router.push("/fiscalyear/create");
		}
	},
	methods: {
		reportDates() {
			// disallow repeat schedule blocks or schedule blocks without a home
			const sbID = new Date().getTime() % Math.pow(2, 32);
			if (
				this.$store.getters.scheduleBlockExists(sbID) === false &&
				this.$store.getters.scheduleBlockExistsWithName(
					this.scheduleBlockData.name,
					this.$store.getters.currentFiscalYear.name
				) === false &&
				this.$store.getters.currentFiscalYear &&
				this.scheduleBlockData.name
			) {
				this.$store.commit("addScheduleBlock", {
					scheduleBlockData: { sbID, ...this.scheduleBlockData },
					fiscalYear: this.$store.getters.currentFiscalYear.fyID
				});
				this.$router.push("/scheduleblock/constraints");
			}
		},
		recordScheduleBlockData(newData) {
			this.scheduleBlockData = newData;
		}
	}
};
</script>
