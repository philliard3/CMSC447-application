<template>
	<div>
		<StartEndPicker
			title="Create new Schedule Block"
			@picked="recordScheduleBlockData"
		/>
		<v-flex xs12 sm6 d-flex>
			<v-select :items="fiscalYears" label="Select Fiscal Year" solo></v-select>
		</v-flex>
	</div>
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
			this.$store.commit("initialize", this.scheduleBlockData);
		},
		recordScheduleBlockData(newData) {
			this.scheduleBlockData = newData;
		}
	}
};
</script>
