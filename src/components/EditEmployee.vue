<template>
	<v-container v-if="error">
		<v-card>
			<v-card-text class="title">We couldn't find that employee.</v-card-text>
		</v-card>
	</v-container>
	<div v-else>
		<v-container>
			<v-layout>
				<v-flex class="display-2 font-weight-light">{{ name }}</v-flex>
			</v-layout>
			<v-container>
				<v-card>
					<v-card-text>
						<v-layout xs12>
							<v-flex xs5>
								<v-container>
									<v-text-field
										v-model="name"
										label="Name"
										name="Name"
									></v-text-field>
								</v-container>
							</v-flex>
							<v-flex xs1></v-flex>
							<v-flex xs5>
								<v-container>
									<v-text-field
										v-model="email"
										label="Email"
										name="Email"
									></v-text-field>
								</v-container>
							</v-flex>
						</v-layout>
					</v-card-text>
				</v-card>
			</v-container>

			<RoleTable :roleIDs="roleIDs" @input="roleIDs = $event" />

			<ShiftSelection :shifts="shifts" @input="shifts = $event" />

			<WorkPreferenceTable
				:preferredDays="preferredDays"
				@input="preferredDays = $event"
				:defaultDays="defaultDays"
			/>
			<!-- save button is redundant now that all components update the state automatically
		      <v-btn @click="save" color="info">Save</v-btn>
      -->
		</v-container>
	</div>
</template>

<script>
import moment from "moment";
import RoleTable from "./RoleTable";
import ShiftSelection from "./ShiftSelection";
import WorkPreferenceTable from "./WorkPreferenceTable";

export default {
	name: "EditEmployee",
	components: {
		RoleTable,
		ShiftSelection,
		WorkPreferenceTable
	},
	data() {
		const defaultDays = [
			{
				name: "Christmas",
				repeating: true,
				startDate: moment("12/25", "MM/DD")
					.toDate()
					.getTime(),
				endDate: moment("12/25", "MM/DD")
					.toDate()
					.getTime(),
				selected: false,
				preferred: null
			}
		];

		defaultDays.forEach(day => {
			day.dayID =
				day.name
					.split("")
					.splice(0, 5)
					.reduce((total, chr) => {
						return total + (chr.charCodeAt(0) - 48);
					}, 0) +
				(new Date().getTime() % Math.pow(2, 27));
		});
		return {
			error: false,
			defaultDays
		};
	},
	computed: {
		employeeData() {
			const filteredEmployees = this.$store.getters.employees.filter(
				e => e.employeeID === Number(this.$route.params.employeeID)
			);
			if (filteredEmployees.length === 1) {
				return { ...filteredEmployees[0] };
			} else {
				return null;
			}
		},
		email: {
			get() {
				if (this.employeeData) {
					return this.employeeData.email || "";
				}
				return null;
			},
			set(newValue) {
				const isEmail = true; // replace this with a regex match
				if (isEmail) {
					this.$store.commit("updateEmployee", {
						employeeData: {
							...this.employeeData,
							email: newValue
						}
					});
				}
			}
		},
		name: {
			get() {
				if (this.employeeData) {
					return this.employeeData.name || "";
				}
				return null;
			},
			set(newValue) {
				this.$store.commit("updateEmployee", {
					employeeData: {
						...this.employeeData,
						name: newValue
					}
				});
			}
		},
		roleIDs: {
			get() {
				return this.employeeData.roles.map(role => role.roleID);
			},
			set(newValue) {
				const roles = this.$store.getters.roles
					.filter(role => newValue.includes(role.roleID))
					.map(role => ({ roleID: role.roleID, name: role.name }));
				this.$store.commit("updateEmployee", {
					employeeData: {
						...this.employeeData,
						roles
					}
				});
			}
		},
		shifts: {
			get() {
				const employee = this.employeeData;
				let shifts = employee.shifts || [];
				shifts = shifts.concat(
					this.$store.getters.currentScheduleBlock.shifts
						.filter(shift => {
							if (shifts.some(s => s.shiftID === shift.shiftID)) {
								return false;
							}
							const permittedRoles = shift.roles.reduce(
								(arr, r) => arr.concat(r.permittedRoles),
								[]
							);
							return employee.roles.some(r =>
								permittedRoles.includes(r.roleID)
							);
						})
						.map(shift => {
							const selected = employee.shifts
								? employee.shifts.filter(s => s.name === shift.name)
								: null;
							return {
								selected: selected && selected.length > 0 ? selected[0] : [],
								hard: false,
								...shift
							};
						})
				);
				return shifts;
			},
			set(newValue) {
				this.$store.commit("updateEmployee", {
					employeeData: {
						...this.employeeData,
						shifts: [...newValue]
					}
				});
			}
		},
		preferredDays: {
			get() {
				if (this.employeeData && this.employeeData.preferredDays) {
					return this.employeeData.preferredDays;
				}
				return [...this.defaultDays];
			},
			set(newValue) {
				this.$store.commit("updateEmployee", {
					employeeData: {
						...this.employeeData,
						preferredDays: [...newValue]
					}
				});
			}
		}
	},
	created() {
		if (
			this.$store.getters.employees.filter(
				e => e.employeeID === Number(this.$route.params.employeeID)
			).length === 0
		) {
			this.error = true;
		}
	},
	methods: {
		save() {
			/*
			const dataToCommit = {
				roles: this.roleIDs,
				preferredShifts: this.shifts.map(shift => ({
					name: shift.name,
					selectedDays: shift.selected
				})),
				...this.employeeData
			};
			console.log(dataToCommit);
			**/
			// this.$store.commit("updateEmployee", dataToCommit);
		}
	}
};
</script>
