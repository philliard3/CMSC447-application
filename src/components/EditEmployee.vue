<template>
	<v-form v-model="valid">
		<v-container class="display-2 font-weight-light">
			{{ employeeData.name }}
			<v-container>
				<v-card>
					<v-card-text></v-card-text>
					<v-layout xs12>
						<v-flex xs5>
							<v-container>
								<v-text-field
									v-model="employeeData.name"
									label="Name"
									name="Name"
								></v-text-field>
							</v-container>
						</v-flex>
						<v-flex xs1></v-flex>
						<v-flex xs5>
							<v-container>
								<v-text-field
									v-model="employeeData.email"
									label="Email"
									name="Email"
								></v-text-field>
							</v-container>
						</v-flex>
					</v-layout>
				</v-card>
			</v-container>

			<RoleTable :roleIDs="roleIDs" @input="roleIDs = $event" />

			<ShiftSelection :shifts="shifts" @input="$emit('input', shifts)" />

			<WorkPreferenceTable :dayName="dayName" :preferredDays="preferredDays" />

			<v-btn @click="save" color="info">Save</v-btn>
		</v-container>
	</v-form>
</template>

<script>
import RoleTable from "./RoleTable";
import ShiftSelection from "./ShiftSelection";
import WorkPreferenceTable from "./WorkPreferenceTable";

const holidays = [{ name: "Christmas", selected: false, preferred: false }];

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
		const employee = this.$store.getters.employees.filter(
			e => e.employeeID === Number(this.$route.params.employeeID)
		)[0];
		return {
			valid: false,
			employeeData: { ...employee },
			roleIDs: employee.roles.map(role => role.roleID),
			shifts:
				employee.shifts ||
				this.$store.getters.currentScheduleBlock.shifts.map(shift => {
					const selected = employee.shifts
						? employee.shifts.filter(s => s.name === shift.name)
						: null;
					return {
						selected: selected && selected.length > 0 ? selected[0] : [],
						...shift
					};
				}),
			preferredDays: [...holidays],
			dayName: ""
		};
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
