<template>
	<v-layout>
		<v-flex>
			<v-select
				:items="fiscalYears.map(fy => fy.name)"
				v-model="selectedFiscalYear"
				label="Fiscal Year to Edit"
			></v-select>
		</v-flex>
	</v-layout>
</template>

<script>
export default {
	name: "FiscalYearSelector",
	data() {
		return {};
	},
	computed: {
		selectedFiscalYear: {
			get() {
				try {
					return this.$store.getters.currentFiscalYear.name;
				} catch (err) {
					return null;
				}
			},
			set(newValue) {
				this.$store.commit("setCurrentFiscalYear", {
					fiscalYearData: { name: newValue }
				});
				return this.$store.getters.currentFiscalYear.name;
			}
		},
		fiscalYears() {
			return this.$store.getters.fiscalYears;
		}
	}
};
</script>
