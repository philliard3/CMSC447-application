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
					<v-flex xs11 sm1></v-flex>
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
					<v-flex xs11 sm1></v-flex>
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
					<v-flex xs11 sm4>
						<v-select
							label="Location"
							:items="this.$store.getters.locations"
							v-model="newShiftData.location"
						></v-select>
					</v-flex>
					<v-flex xs11 sm1></v-flex>
					<v-flex xs11 sm4>
						<v-combobox
							v-model="newShiftData.startDays"
							:items="[
								'Sunday',
								'Monday',
								'Tuesday',
								'Wednesday',
								'Thursday',
								'Friday',
								'Saturday'
							]"
							label="Starting Days"
							chips
							clearable
							solo
							multiple
						>
							<template v-slot:selection="data">
								<v-chip
									:selected="data.selected"
									close
									@input="removeStartDay(data.item)"
								>
									<strong>{{ data.item }}</strong
									>&nbsp;
								</v-chip>
							</template>
						</v-combobox>
					</v-flex>
					<v-flex xs11 sm1></v-flex>
					<v-flex xs11 sm3>
						<v-combobox
							v-model="newShiftData.tags"
							:items="this.$store.getters.tags"
							label="Tags"
							chips
							clearable
							solo
							multiple
						>
							<template v-slot:selection="data">
								<v-chip
									:selected="data.selected"
									close
									@input="removeTag(data.item)"
								>
									<strong>{{ data.item }}</strong
									>&nbsp;
								</v-chip>
							</template>
						</v-combobox>
					</v-flex>
				</v-layout>
				<v-layout>
					<v-flex xs11 sm4>
						<v-combobox
							v-model="newAssignmentData.permittedRoles"
							:items="roles.map(role => role.name)"
							label="Roles"
							chips
							clearable
							solo
							multiple
						>
							<template v-slot:selection="data">
								<v-chip
									:selected="data.selected"
									close
									@input="removeRole(data.item)"
								>
									<strong>{{ data.item }}</strong
									>&nbsp;
								</v-chip>
							</template>
						</v-combobox>
					</v-flex>
					<v-flex xs11 sm1></v-flex>
					<v-flex xs11 sm2>
						<v-text-field
							label="Min. Employees"
							v-model="newAssignmentData.min"
						></v-text-field>
					</v-flex>
					<v-flex xs11 sm1></v-flex>
					<v-flex xs11 sm2>
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
				<v-layout v-if="!newShiftData.roles || !newShiftData.roles.length">
					<v-flex>
						<v-container>
							<v-layout>
								<v-flex class="title"
									>Please click New Assignment to add to the list.</v-flex
								>
							</v-layout>
						</v-container>
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
					<v-flex xs11 sm5 class="title font-weight-light">
						Roles:
						{{
							role.permittedRoles.length
								? role.permittedRoles.join(", ")
								: "Nobody"
						}}
					</v-flex>
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

				<v-layout>
					<v-flex>
						<v-container v-for="shift in shifts" :key="shift.shiftID">
							<v-layout>
								<v-flex class="title" sm2>{{ shift.name }}</v-flex>
								<v-flex class="subheading" sm2>{{
									moment(shift.startTime, "HH:mm").format("hh:mm a")
								}}</v-flex>
								<v-flex class="subheading" sm2>{{ shift.location }}</v-flex>
								<v-flex sm2>
									<v-layout
										v-for="(roleRestriction, index) in shift.roles"
										:key="
											String(index) +
												'_' +
												roleRestriction.permittedRoles.join(',') +
												'_' +
												String(roleRestriction.min) +
												'_' +
												String(roleRestriction.max)
										"
									>
										<v-flex>
											<v-card-text>
												{{
													roleRestriction.min !== roleRestriction.max
														? `${roleRestriction.min}-${roleRestriction.max}`
														: roleRestriction.min
												}}
												&nbsp;
												{{
													roleRestriction.permittedRoles.length
														? roleRestriction.permittedRoles
																.map(
																	r =>
																		roles.filter(role => role.roleID === r)[0]
																			.name
																)
																.join(", ")
														: "Nobody"
												}}
											</v-card-text>
										</v-flex>
									</v-layout>
								</v-flex>
								<v-flex v-if="shift.tags && shift.tags.length > 0">
									<v-chip v-for="tag in shift.tags" :key="tag">
										{{ tag }}
									</v-chip>
								</v-flex>
								<v-flex>
									<v-btn color="error" @click="removeShift(shift)">
										<v-icon>remove_circle</v-icon>&nbsp;Remove
									</v-btn>
								</v-flex>
							</v-layout>
						</v-container>
					</v-flex>
				</v-layout>
			</v-card-text>
		</v-card>
	</v-container>
</template>

<script>
import moment from "moment";

export default {
	data() {
		return {
			menu1: false,
			menu2: false,
			newAssignmentData: {
				min: 0,
				max: 1,
				permittedRoles: []
			},
			newShiftData: {
				name: "",
				roles: [],
				startTime: null,
				endTime: null,
				location: "",
				duration: 0,
				startDays: [],
				tags: []
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
	watch: {
		"newShiftData.tags": function(newTags) {
			// make sure it only adds new tags
			if (newTags.length > 0) {
				const tagToAdd = newTags[newTags.length - 1];
				this.$store.commit("addTag", tagToAdd);
			}
		},

		"newShiftData.startDays": function(newDays) {
			const daysOfWeek = [
				"Sunday",
				"Monday",
				"Tuesday",
				"Wednesday",
				"Thursday",
				"Friday",
				"Saturday"
			];

			// trim out any nonexistent days
			if (newDays.some(day => !daysOfWeek.includes(day))) {
				this.newShiftData.startDays = this.newShiftData.startDays.filter(day =>
					daysOfWeek.includes(day)
				);
			}
		}
	},
	methods: {
		moment,
		createShift() {
			const newShiftData = {
				shiftID: new Date().getTime() % Math.pow(2, 31),
				...this.newShiftData
			};

			// trim out any nonexistent days
			newShiftData.startDays = newShiftData.startDays.filter(day =>
				[
					"Sunday",
					"Monday",
					"Tuesday",
					"Wednesday",
					"Thursday",
					"Friday",
					"Saturday"
				].includes(day)
			);

			if (newShiftData.roles.length <= 0) {
				return;
			}

			if (newShiftData.name.length <= 0) {
				return;
			}

			if (!newShiftData.startTime || !newShiftData.endTime) {
				return;
			}

			if (newShiftData.startDays.length <= 0) {
				return;
			}

			if (!newShiftData.location) {
				return;
			}

			if (
				moment(newShiftData.startTime, "hh:mm").isAfter(
					moment(newShiftData.endTime, "hh:mm")
				)
			) {
				return;
			}

			newShiftData.duration = moment(newShiftData.endTime, "hh:mm").diff(
				moment(newShiftData.startTime, "hh:mm"),
				"minutes"
			);

			newShiftData.roles.forEach(r => {
				r.permittedRoles = r.permittedRoles.map(
					roleName =>
						this.roles.filter(role => role.name === roleName)[0].roleID
				);
			});

			const currentScheduleBlock = this.$store.getters.currentScheduleBlock;

			if (currentScheduleBlock) {
				this.$store.commit("addShift", {
					shiftData: newShiftData,
					scheduleBlock: currentScheduleBlock
				});

				// clear shift data
				this.newShiftData = {
					name: "",
					roles: [],
					startTime: null,
					endTime: null,
					location: "",
					duration: 0,
					startDays: [],
					tags: []
				};

				this.menu1 = false;
				this.menu2 = false;
			}
		},
		removeStartDay(day) {
			this.newShiftData.startDays.splice(
				this.newShiftData.startDays.indexOf(day),
				1
			);
			// re-assign to assure reactivity
			this.newShiftData.startDays = [...this.newShiftData.startDays];
		},
		removeRole(role) {
			this.newAssignmentData.permittedRoles.splice(
				this.newAssignmentData.permittedRoles.indexOf(role),
				1
			);
			// re-assign to assure reactivity
			this.newAssignmentData.permittedRoles = [
				...this.newAssignmentData.permittedRoles
			];
		},
		removeTag(tag) {
			this.newShiftData.tags.splice(this.newShiftData.tags.indexOf(tag), 1);
			this.newShiftData.tags = [...this.newShiftData.tags];
		},
		addAssignment() {
			const newAssignmentData = {
				min: 0,
				max: 1,
				permittedRoles: []
			};
			// validate number input
			const min = Number(this.newAssignmentData.min);
			const max = Number(this.newAssignmentData.max);
			if (min >= 0 && max >= 0 && min <= max) {
				this.newShiftData.roles.push({
					permittedRoles: this.newAssignmentData.permittedRoles,
					min,
					max
				});
				this.newAssignmentData = { ...newAssignmentData };
			}
		},
		removeAssignment(index) {
			this.newShiftData.roles.splice(index, 1);
		},
		removeShift(shift) {
			const currentScheduleBlock = this.$store.getters.currentScheduleBlock;
			this.$store.commit("removeShift", {
				shiftData: { ...shift },
				scheduleBlockData: currentScheduleBlock
			});
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
