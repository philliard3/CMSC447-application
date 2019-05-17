<template>
	<v-layout>
		<v-flex>
			<v-select
				:items="scheduleBlocks.map(sb => sb.name)"
				v-model="selectedScheduleBlock"
				label="Schedule Block to edit"
			></v-select>
		</v-flex>
	</v-layout>
</template>
<script>
export default {
	name: "ScheduleBlockSelector",
	data() {
		return {};
	},
	computed: {
		selectedScheduleBlock: {
			get() {
				try {
					return this.$store.getters.currentScheduleBlock.name;
				} catch (err) {
					return null;
				}
			},
			set(newValue) {
				this.$store.commit("setCurrentScheduleBlock", {
					fiscalYearData: { name: this.selectedFiscalYear },
					scheduleBlockData: { name: newValue }
				});
				return this.$store.getters.currentScheduleBlock.name;
			}
		},
		scheduleBlocks() {
			return this.$store.getters.scheduleBlocks;
		}
	}
};
</script>
