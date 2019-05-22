<template>
	<v-container v-if="error">
		<v-card>
			<v-card-text>{{ error }}</v-card-text>
		</v-card>
	</v-container>
	<div v-else>
		<v-container>
			<v-card>
				<v-card-text>
					<v-container>
						<v-layout>
							<v-flex class="display-1 font-weight-light"
								>Employee Role: {{ roleData.name }}</v-flex
							>
						</v-layout>
						<v-layout>
							<v-flex>
								<v-text-field label="Name" v-model="name"></v-text-field>
							</v-flex>
							<v-flex>
								<v-checkbox
									label="This is a moonlighter role"
									v-model="isMoonlighter"
								></v-checkbox>
							</v-flex>
						</v-layout>
					</v-container>
				</v-card-text>
			</v-card>
		</v-container>
		<v-container>
			<v-card>
				<v-card-text>
					<v-container>
						<v-layout>
							<v-flex>
								<v-card-text class="display-1 font-weight-light"
									>Color</v-card-text
								>
							</v-flex>
						</v-layout>
						<v-layout>
							<v-flex>
								<swatches v-model="color" colors="basic" inline></swatches>
							</v-flex>
						</v-layout>
					</v-container>
				</v-card-text>
			</v-card>
		</v-container>
		<v-container>
			<v-card>
				<v-card-text>
					<v-container>
						<v-layout>
							<v-flex>
								<v-card-text class="display-1 font-weight-light"
									>Requirements</v-card-text
								>
							</v-flex>
						</v-layout>

						<v-layout>
							<v-flex sm1>
								<v-select
									v-model="requirementToAdd.type"
									label="Unit"
									:items="[
										'Hours',
										'Shifts',
										'Attending weeks',
										'Nursing weeks'
									]"
								></v-select>
							</v-flex>
							<v-flex sm2 class="title">required per</v-flex>
							<v-flex sm2>
								<v-select
									v-model="requirementToAdd.per"
									label="period"
									:items="['week', '2 weeks', 'month', 'quarter', 'year']"
								></v-select>
							</v-flex>
							<v-flex sm1 class="title">-</v-flex>
							<v-flex sm2>
								<v-text-field
									v-model="requirementToAdd.amount"
									label="Amount"
								></v-text-field>
							</v-flex>
							<v-flex sm1 class="title">
								<v-checkbox
									v-model="requirementToAdd.flexible"
									label="flexible"
								></v-checkbox>
							</v-flex>
							<v-flex sm1></v-flex>
							<v-flex sm1>
								<v-btn color="success" @click="addRequirement">
									<v-icon>add</v-icon>
								</v-btn>
							</v-flex>
						</v-layout>
						<v-layout
							v-for="(requirement, index) in roleData.required"
							@input="updateRequirement(index)"
							:key="requirement.requirementID"
						>
							<v-flex sm1>
								<v-select
									v-model="requirement.type"
									label="Unit"
									:items="[
										'Hours',
										'Shifts',
										'Attending weeks',
										'Nursing weeks'
									]"
								></v-select>
							</v-flex>
							<v-flex sm2 class="title">required per</v-flex>
							<v-flex sm2>
								<v-select
									v-model="requirement.per"
									label="period"
									:items="['week', '2 weeks', 'month', 'quarter', 'year']"
								></v-select>
							</v-flex>
							<v-flex sm1 class="title">-</v-flex>
							<v-flex sm2>
								<v-text-field
									v-model="requirement.amount"
									label="Amount"
								></v-text-field>
							</v-flex>
							<v-flex sm1 class="title">
								<v-checkbox
									v-model="requirement.flexible"
									label="flexible"
								></v-checkbox>
							</v-flex>
							<v-flex sm1></v-flex>
							<v-flex sm1>
								<v-btn color="error" @click="removeRequirement(index)">
									<v-icon>remove</v-icon>
								</v-btn>
							</v-flex>
						</v-layout>
					</v-container>
				</v-card-text>
			</v-card>
		</v-container>
	</div>
</template>

<script>
import Swatches from "vue-swatches";

export default {
	name: "EditRole",
	components: { Swatches },
	data() {
		return {
			requirementToAdd: {
				type: null,
				per: null,
				amount: "",
				flexible: false
			}
		};
	},
	computed: {
		name: {
			get() {
				return this.roleData ? this.roleData.name : "";
			},
			set(newName) {
				if (newName.length > 0 && !this.$store.getters.roleExists(newName)) {
					const newRoleData = { ...this.roleData, name: newName };
					this.$store.commit("updateRole", { roleData: newRoleData });
				}
			}
		},
		isMoonlighter: {
			get() {
				return this.roleData ? !!this.roleData.isMoonlighter : false;
			},
			set(newValue) {
				const newRoleData = { ...this.roleData, isMoonlighter: newValue };
				this.$store.commit("updateRole", { roleData: newRoleData });
			}
		},
		roleData() {
			const roleID = Number(this.$route.params.roleID);
			const roles = this.$store.getters.roles.filter(
				role => role.roleID === roleID
			);
			let roleData = null;
			if (roles.length) {
				roleData = roles[0];
			} else {
				return null;
			}

			let changed = false;
			const newRoleData = { ...roleData };

			if (!roleData.color) {
				newRoleData.color = "#1FBC9C";
				changed = true;
			}

			if (!roleData.required) {
				newRoleData.required = [];
				changed = true;
			}

			if (changed) {
				this.$store.commit("updateRole", { roleData: newRoleData });
				return this.$store.getters.roles.filter(
					role => role.roleID === roleID
				)[0];
			}

			return roleData;
		},
		color: {
			get() {
				const roleID = Number(this.$route.params.roleID);
				const roles = this.$store.getters.roles.filter(
					role => role.roleID === roleID
				);
				let roleData = null;
				if (roles.length) {
					roleData = roles[0];
				} else {
					return null;
				}

				if (!roleData.color) {
					return "#1FBC9C";
				}
				return roleData.color;
			},
			set(newColor) {
				const newRoleData = { ...this.roleData, color: newColor };
				this.$store.commit("updateRole", { roleData: newRoleData });
			}
		},

		error() {
			return this.roleData ? null : "We couldn't find that role.";
		}
	},
	methods: {
		addRequirement() {
			if (!(this.requirementToAdd.type && this.requirementToAdd.per)) {
				return;
			}
			const newRoleData = { ...this.roleData };
			const requirementToAdd = { ...this.requirementToAdd };

			requirementToAdd.amount = Number(requirementToAdd.amount);
			if (!(requirementToAdd.amount >= 0)) {
				return;
			}
			newRoleData.required.push({
				requirementID: new Date().getTime() % Math.pow(2, 31),
				...requirementToAdd
			});

			this.$store.commit("updateRole", { roleData: newRoleData });
			this.requirementToAdd = {
				type: null,
				per: null,
				amount: "",
				flexible: false
			};
		},
		updateRequirement(index) {
			const newRequirementData = { ...this.roleData.required[index] };
			if (!(newRequirementData.type && newRequirementData.per)) {
				return;
			}

			newRequirementData.amount = Number(newRequirementData.amount);
			if (!(newRequirementData.amount >= 0)) {
				return;
			}

			const newRoleData = { ...this.roleData };
			newRoleData.required.splice(index, 1, newRequirementData);

			this.$store.commit("updateRole", { roleData: newRoleData });
		},
		removeRequirement(index) {
			const newRequired = [...this.roleData.required];
			newRequired.splice(index, 1);
			const newRoleData = { ...this.roleData, required: newRequired };
			this.$store.commit("updateRole", { roleData: newRoleData });
		}
	}
};
</script>

<style>
*:focus {
	outline: none;
}
.title {
	text-align: center;
	align-content: center;
	vertical-align: middle;
}
</style>
