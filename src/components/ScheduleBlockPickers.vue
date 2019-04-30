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
						<v-flex xs12 sm6 d-flex>
							<v-select
								label="Fiscal Year"
								v-model="selectedFiscalYear"
								:items="fiscalYears"
								:rules="[v => !!v || 'Fiscal Year is required']"
								required
							></v-select>
						</v-flex>
					</v-container>
					<v-btn @click="reportDates" color="success">Submit</v-btn>
				</v-card-text>
			</v-card>
		</v-container>
	</v-form>
</template>

<script>
import StartEndPicker from "./StartEndPicker.vue";

export default {
	name: "ScheduleBlockPickers",
	components: { StartEndPicker },
	computed: {
		fiscalYears() {
			const yearNames = this.$store.getters.fiscalYears
				.filter(fy => Boolean(fy.name))
				.map(fy => fy.name);
			return yearNames;
		}
	},
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
	methods: {
		reportDates() {
			// disallow repeat schedule blocks or schedule blocks without a home
			if (
				!this.$store.getters.scheduleBlockExists(this.scheduleBlockData.name) &&
				this.selectedFiscalYear
			) {
				this.$store.commit("addScheduleBlock", {
					scheduleBlockData: this.scheduleBlockData,
					fiscalYear: this.selectedFiscalYear
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
