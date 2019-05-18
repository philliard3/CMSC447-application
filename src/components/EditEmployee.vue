<template>
	<v-container v-if="error">
		<v-card>
			<v-card-text class="title">We couldn't find that employee.</v-card-text>
		</v-card>
	</v-container>
	<v-form v-model="valid" v-else>
		<v-container class="display-2 font-weight-light">
			{{ name }}
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

			<WorkPreferenceTable :dayName="dayName" :preferredDays="preferredDays" />
			<!-- save button is redundant now that all components update the state automatically
      <v-btn @click="save" color="info">Save</v-btn>
      -->
		</v-container>
	</v-form>
</template>

<script>
import RoleTable from "./RoleTable";
import ShiftSelection from "./ShiftSelection";
import WorkPreferenceTable from "./WorkPreferenceTable";

const holidays = [
	{
		name: "Christmas",
		repeating: true,
		startDate: "12/25",
		endDate: "12/25",
		selected: false,
		preferred: false
	}
];

holidays.forEach(day => {
	day.dayID =
		day.name
			.split("")
			.splice(0, 5)
			.reduce((total, chr) => {
				return total + (chr.charCodeAt(0) - 48);
			}, 0) +
		(new Date().getTime() % Math.pow(2, 27));
});

export default {
	name: "EditEmployee",
	components: {
		RoleTable,
		ShiftSelection,
		WorkPreferenceTable
	},
	data() {
		return {
			error: false,
			valid: false,
			preferredDays: [...holidays],
			dayName: ""
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
				return (
					employee.shifts ||
					this.$store.getters.currentScheduleBlock.shifts.map(shift => {
						const selected = employee.shifts
							? employee.shifts.filter(s => s.name === shift.name)
							: null;
						return {
							selected: selected && selected.length > 0 ? selected[0] : [],
							...shift
						};
					})
				);
			},
			set(newValue) {
				this.$store.commit("updateEmployee", {
					employeeData: {
						...this.employeeData,
						shifts: [...newValue]
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
