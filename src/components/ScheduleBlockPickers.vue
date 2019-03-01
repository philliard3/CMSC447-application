<template>
	<StartEndPicker
		title="Create new Schedule Block"
		@picked="recordScheduleBlockData"
	/>
</template>

<script>
import StartEndPicker from "./StartEndPicker.vue";

export default {
	name: "ScheduleBlockPickers",
	components: { StartEndPicker },
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
