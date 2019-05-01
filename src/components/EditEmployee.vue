<template>
	<v-form v-model="valid">
		<v-container class="display-2 font-weight-light">
			{{ employeeData.name }}
			<v-text-field v-model="employeeData.name" name="Name"></v-text-field>

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

export default {
	name: "EditEmployee",
	components: {
		RoleTable,
		ShiftSelection,
		WorkPreferenceTable
	},
	data() {
		const employee = this.$store.getters.employees.filter(
			e => e.employeeID === this.$route.params.employeeID
		)[0];
		return {
			valid: false,
			employeeData: { ...employee },
			roleIDs: employee.roles.map(role => role.roleID),
			shifts:
				employee.preferredShifts ||
				this.$store.getters.currentScheduleBlock.shifts.map(shift => {
					const selected = employee.preferredShifts
						? employee.preferredShifts.filter(s => s.name === shift.name)
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
