<template>
	<v-form v-model="valid">
		<v-container>
			<v-card>
				<v-card-text>
					<div class="display-1 font-weight-light" id="header-row-1">
						Schedule Block:
						{{
							currentScheduleBlock && currentFiscalYear
								? `${currentFiscalYear.name} - ${currentScheduleBlock.name}`
								: ""
						}}
					</div>
					<v-select
						:items="fiscalYears.map(fy => fy.name)"
						v-model="selectedFiscalYear"
						label="Fiscal Year to Edit"
					></v-select>
					<v-select
						:items="scheduleBlocks.map(sb => sb.name)"
						v-model="selectedScheduleBlock"
						label="Schedule Block to edit"
					></v-select>
				</v-card-text>
			</v-card>
		</v-container>
		<ManageShifts></ManageShifts>
		<RoleTable></RoleTable>
	</v-form>
</template>
<script>
import ManageShifts from "./ManageShifts";
import RoleTable from "./RoleTable";

export default {
	name: "ScheduleBlockConstraints",
	components: { ManageShifts, RoleTable },
	data() {
		return {
			valid: false
		};
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
		}
	}
};
</script>
