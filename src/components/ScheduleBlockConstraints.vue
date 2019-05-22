<template>
	<div>
		<v-container>
			<v-card>
				<v-card-text>
					<v-layout>
						<v-flex class="display-1 font-weight-light">
							Schedule Block:
							{{
								currentScheduleBlock && currentFiscalYear
									? `${currentFiscalYear.name} - ${currentScheduleBlock.name}`
									: ""
							}}
						</v-flex>
					</v-layout>
					<FiscalYearSelector />
					<ScheduleBlockSelector />
					<v-layout>
						<v-flex>
							<v-btn
								color="warning"
								flat
								outline
								@click="matchFiscalYearToScheduleBlock"
								>Make all blocks in this Fiscal Year match this setup</v-btn
							>
						</v-flex>
					</v-layout>
				</v-card-text>
			</v-card>
		</v-container>
		<ManageShifts />
		<RoleTable />
		<LocationList />
		<!-- shift patterns and how often they appear -->
		<!-- example: attending weeks per week/block -->
	</div>
</template>
<script>
import ScheduleBlockSelector from "./ScheduleBlockSelector";
import FiscalYearSelector from "./FiscalYearSelector";
import ManageShifts from "./ManageShifts";
import RoleTable from "./RoleTable";
import LocationList from "./LocationList";

export default {
	name: "ScheduleBlockConstraints",
	components: {
		ManageShifts,
		RoleTable,
		ScheduleBlockSelector,
		FiscalYearSelector,
		LocationList
	},
	data() {
		return {};
	},
	computed: {
		scheduleBlocks() {
			return this.$store.getters.scheduleBlocks;
		},
		fiscalYears() {
			return this.$store.getters.fiscalYears;
		},
		currentFiscalYear() {
			return this.$store.getters.currentFiscalYear;
		},
		currentScheduleBlock() {
			return this.$store.getters.currentScheduleBlock;
		}
	},
	created() {
		if (this.$store.getters.currentFiscalYear === null) {
			this.$router.push("/fiscalyear/create");
			return;
		} else if (this.$store.getters.currentScheduleBlock === null) {
			this.$router.push("/scheduleblock/create");
			return;
		}
	},
	methods: {
		matchFiscalYearToScheduleBlock() {
			this.$store.commit("matchFiscalYearToScheduleBlock", {
				scheduleBlockData: this.currentScheduleBlock,
				fiscalYearData: this.currentFiscalYear
			});
		}
	}
};
</script>
