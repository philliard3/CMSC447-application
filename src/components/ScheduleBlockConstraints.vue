<template>
	<v-form v-model="valid">
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
					<v-layout>
						<v-flex>
							<v-select
								:items="fiscalYears.map(fy => fy.name)"
								v-model="selectedFiscalYear"
								label="Fiscal Year to Edit"
							></v-select>
						</v-flex>
					</v-layout>
					<v-layout>
						<v-flex>
							<v-select
								:items="scheduleBlocks.map(sb => sb.name)"
								v-model="selectedScheduleBlock"
								label="Schedule Block to edit"
							></v-select>
						</v-flex>
					</v-layout>
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
					<!-- This feature is too dangerous and labor-intensive, and is therefore not considered a priority.
          <v-layout>
            <v-flex>
              <v-btn color="error" flat outline>Make all blocks in all Fiscal Years match this setup</v-btn>
            </v-flex>
          </v-layout>
          -->
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
