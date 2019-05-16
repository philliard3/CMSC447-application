<template>
	<v-form v-model="valid">
		<v-container>
			<v-card>
				<v-card-text>
					<div class="display-1 font-weight-light" id="header-row-1">
						Fiscal Year: {{ currentFiscalYear ? currentFiscalYear.name : "" }}
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
		<v-container>
			<v-card>
				<v-card-text>
					<div class="display-1 font-weight-light" id="header-row-1">
						Shifts
					</div>
					<v-layout>
						<v-flex xs11 sm5>
							<v-text-field
								name="Shift Nickname"
								label="Shift Nickname"
								v-model="newShiftData.name"
							></v-text-field>
						</v-flex>
						<v-flex xs11 sm5>
							<v-menu
								ref="menu1"
								v-model="menu1"
								:close-on-content-click="false"
								:nudge-right="40"
								:return-value.sync="newShiftData.startTime"
								lazy
								transition="scale-transition"
								offset-y
								full-width
								max-width="290px"
								min-width="290px"
							>
								<template v-slot:activator="{ on }">
									<v-text-field
										v-model="newShiftData.startTime"
										label="Shift Start Time"
										prepend-icon="access_time"
										readonly
										v-on="on"
									></v-text-field>
								</template>
								<v-time-picker
									v-if="menu1"
									v-model="newShiftData.startTime"
									full-width
									@click:minute="$refs.menu1.save(newShiftData.startTime)"
								></v-time-picker>
							</v-menu>
						</v-flex>
						<v-flex xs11 sm5>
							<v-menu
								ref="menu2"
								v-model="menu2"
								:close-on-content-click="false"
								:nudge-right="40"
								:return-value.sync="newShiftData.endTime"
								lazy
								transition="scale-transition"
								offset-y
								full-width
								max-width="290px"
								min-width="290px"
							>
								<template v-slot:activator="{ on }">
									<v-text-field
										v-model="newShiftData.endTime"
										label="Shift End Time"
										prepend-icon="access_time"
										readonly
										v-on="on"
									></v-text-field>
								</template>
								<v-time-picker
									v-if="menu2"
									v-model="newShiftData.endTime"
									full-width
									@click:minute="$refs.menu2.save(newShiftData.endTime)"
								></v-time-picker>
							</v-menu>
						</v-flex>
					</v-layout>
					<div>
						<v-btn color="success" @click="createShift">
							<v-icon flat>calendar_view_day</v-icon>&nbsp;New Shift
						</v-btn>
					</div>
					<!--- shift table -->
				</v-card-text>
			</v-card>
		</v-container>
		<v-container>
			<v-card>
				<v-card-text>
					<div class="display-1 font-weight-light" id="header-row-1">
						Locations
					</div>
					<v-layout>
						<v-flex>
							<v-text-field
								v-model="newLocationData.name"
								name="Location Name"
								label="Location Name"
							></v-text-field>
						</v-flex>
						<v-flex>
							<v-btn color="success">
								<v-icon>add_location</v-icon>&nbsp;New Location
							</v-btn>
						</v-flex>
						<!--- location table -->
					</v-layout>
				</v-card-text>
			</v-card>
		</v-container>
	</v-form>
</template>

<script>
export default {
	data() {
		return {
			valid: false,
			menu1: false,
			menu2: false,
			newShiftData: {
				name: "",
				startTime: null,
				endTime: null
			},
			newLocationData: {
				name: ""
			},
			locations: []
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
		}
	},
	created() {
		if (this.$store.getters.currentFiscalYear === null) {
			this.$router.push("/fiscalyear/create");
		}
	},
	methods: {
		createShift() {
			const newShiftData = {
				shiftID: new Date().getTime(),
				...this.newShiftData
			};
			for (let value of Object.values(newShiftData)) {
				if (!value) {
					return;
				}
			}
			// console.log(newShiftData);
		},
		createLocation() {}
	}
};
</script>
