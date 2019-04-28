<template>
	<v-container>
		<StartEndPicker
			title="Create new Schedule Block"
			@picked="recordScheduleBlockData"
		/>
		<v-flex xs12 sm6 d-flex>
			<v-select :items="fiscalYears" label="Fiscal Year"></v-select>
		</v-flex>
	</v-container>
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
			}
		};
	},
	methods: {
		reportDates() {
			// disallow repeat schedule blocks
			if (
				!this.$store.getters.scheduleBlockExists(this.scheduleBlockData.name)
			) {
				this.$store.commit("initialize", this.scheduleBlockData);
				this.$router.push("/scheduleblock/constraints");
			}
		},
		recordScheduleBlockData(newData) {
			this.scheduleBlockData = newData;
		}
	}
};
</script>
