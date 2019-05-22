<template>
	<div>
		<v-container>
			<v-card>
				<v-card-text>
					<div class="display-1 font-weight-light" id="header-row-1">
						Fiscal Year: {{ currentFiscalYear ? currentFiscalYear.name : "" }}
					</div>
					<FiscalYearSelector></FiscalYearSelector>
				</v-card-text>
			</v-card>
		</v-container>
		<LocationList></LocationList>
	</div>
</template>

<script>
import FiscalYearSelector from "./FiscalYearSelector";
import LocationList from "./LocationList";

export default {
	components: {
		LocationList,
		FiscalYearSelector
	},
	data() {
		return {};
	},
	computed: {
		fiscalYears() {
			return this.$store.getters.fiscalYears;
		},
		roles() {
			return this.$store.getters.roles;
		},
		currentFiscalYear() {
			return this.$store.getters.currentFiscalYear;
		}
	},
	created() {
		if (this.$store.getters.currentFiscalYear === null) {
			this.$router.push("/fiscalyear/create");
		}
	},
	methods: {}
};
</script>
