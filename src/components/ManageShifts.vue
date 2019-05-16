<template>
	<v-container>
		<v-card>
			<v-card-text>
				<div class="display-1 font-weight-light" id="header-row-1">Shifts</div>
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
				<v-layout>
					<v-flex xs11 sm3>
						<span v-for="role in newAssignmentData.permittedRoles" :key="role">
							{{ role }}
							<span class="remove-item" @click="removeRole(role)">&times;</span>
						</span>
					</v-flex>
					<v-flex xs11 sm1>
						<v-select
							label="+"
							:items="roles.map(role => role.name)"
							v-model="roleToAdd"
							@input="addRole"
						></v-select>
					</v-flex>
					<v-flex xs11 sm1></v-flex>
					<v-flex xs11 sm3>
						<v-text-field
							label="Min. Employees"
							v-model="newAssignmentData.min"
						></v-text-field>
					</v-flex>
					<v-flex xs11 sm1></v-flex>
					<v-flex xs11 sm3>
						<v-text-field
							label="Max. Employees"
							v-model="newAssignmentData.max"
						></v-text-field>
					</v-flex>
					<v-flex xs11 sm3>
						<v-card-text>
							<v-btn flat @click="addAssignment">
								<v-icon>add</v-icon>New Assignment
							</v-btn>
						</v-card-text>
					</v-flex>
				</v-layout>
				<v-layout
					v-for="(role, index) in newShiftData.roles"
					:key="
						String(index) +
							'_' +
							role.permittedRoles.join(',') +
							'_' +
							String(role.min) +
							'_' +
							String(role.max)
					"
				>
					<v-flex xs11 sm5 class="title font-weight-light"
						>Roles:
						{{
							role.permittedRoles.length
								? role.permittedRoles.join(", ")
								: "Nobody"
						}}</v-flex
					>
					<v-flex xs11 sm5 class="title font-weight-light"
						>Min: {{ role.min }}</v-flex
					>
					<v-flex xs11 sm5 class="title font-weight-light"
						>Max: {{ role.max }}</v-flex
					>
					<v-flex xs11 sm5>
						<v-btn color="error" @click="removeAssignment(index)">
							<v-icon>remove</v-icon>Remove
						</v-btn>
					</v-flex>
				</v-layout>
				<v-container>
					<v-btn color="success" @click="createShift">
						<v-icon flat>calendar_view_day</v-icon>&nbsp;New Shift
					</v-btn>
				</v-container>
				<!--- shift table -->
			</v-card-text>
		</v-card>
	</v-container>
</template>
<script>
export default {
	data() {
		return {
			menu1: false,
			menu2: false,
			roleToAdd: null,
			newAssignmentData: {
				min: 0,
				max: 0,
				permittedRoles: []
			},
			newShiftData: {
				name: "",
				roles: [
					{
						permittedRoles: [1, 2, 3, 4, 5],
						min: 1,
						max: 1
					}
				],
				startTime: null,
				endTime: null
			}
		};
	},
	computed: {
		shifts() {
			const currentScheduleBlock = this.$store.getters.currentScheduleBlock;
			if (currentScheduleBlock) {
				return currentScheduleBlock.shifts;
			}
			return [];
		},
		roles() {
			return this.$store.getters.roles;
		}
	},
	created() {
		if (this.$store.getters.currentScheduleBlock === null) {
			if (this.$store.getters.currentFiscalYear === null) {
				this.$router.push("/fiscalyear/create");
			} else {
				this.$router.push("/scheduleblock/create");
			}
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
		addRole(role) {
			if (!this.newAssignmentData.permittedRoles.includes(role))
				this.newAssignmentData.permittedRoles.push(role);
			this.roleToAdd = null;
		},
		removeRole(role) {
			this.newAssignmentData.permittedRoles.splice(
				this.newAssignmentData.permittedRoles.indexOf(role),
				1
			);
		},
		addAssignment() {
			const newAssignmentData = {
				min: 0,
				max: 0,
				permittedRoles: []
			};
			this.newShiftData.roles.push({ ...this.newAssignmentData });
			this.newAssignmentData = { ...newAssignmentData };
		},
		removeAssignment(index) {
			this.newShiftData.roles.splice(index, 1);
		}
	}
};
</script>
<style>
.remove-item:hover {
	background-color: #555;
	color: #fff;
}
</style>
