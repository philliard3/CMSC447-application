<template>
	<v-form v-model="valid">
		<v-container class="display-2 font-weight-light">
			{{ employeeData.name }}
			<v-text-field v-model="employeeData.name" name="Name"></v-text-field>
			<RoleTable v-bind:roleIDs="roleIDs" v-on:input="roleIDs = $event" />
			<v-card>
				<v-card-text>
					<div class="display-1 font-weight-light">Shift Preferences</div>
					<v-container
						fluid
						v-for="(shift, shiftIndex) in $store.getters.currentScheduleBlock
							.shifts"
						:key="shift.name"
						class="headline font-weight-medium"
					>
						{{ shift.name }} (Starts at {{ shift.startTime }})
						<v-layout row wrap>
							<v-flex
								v-for="(dayList, dayListIndex) in shift.startDays.reduce(
									(dayList, day, i) => {
										if (i % 2) {
											dayList[dayList.length - 1].push(day);
										} else {
											dayList.push([day]);
										}
										return dayList.slice();
									},
									[]
								)"
								:key="dayListIndex"
								xs12
								sm4
								md4
							>
								<v-checkbox
									v-for="day in dayList"
									v-model="shifts[shiftIndex].selected"
									:key="day"
									:label="day"
									:value="day"
									hide-details
								></v-checkbox>
							</v-flex>
						</v-layout>
					</v-container>
				</v-card-text>
			</v-card>

			<v-btn @click="save" color="info">Save</v-btn>
		</v-container>
	</v-form>
</template>

<script>
import RoleTable from "./RoleTable";

export default {
	name: "EditEmployee",
	components: {
		RoleTable
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
				employee.preferedShifts ||
				this.$store.getters.currentScheduleBlock.shifts.map(shift => {
					const selected = employee.preferedShifts
						? employee.preferedShifts.filter(s => s.name === shift.name)
						: null;
					return {
						selected: selected && selected.length > 0 ? selected[0] : [],
						...shift
					};
				})
		};
	},
	methods: {
		save() {
			/*
			const dataToCommit = {
				roles: this.roleIDs,
				preferedShifts: this.shifts.map(shift => ({
					name: shift.name,
					selectedDays: shift.selected
				})),
				...this.employeeData
			};
			console.log(dataToCommit);
			*/
			// this.$store.commit("updateEmployee", dataToCommit);
		}
	}
};
</script>
