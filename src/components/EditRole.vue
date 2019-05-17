<template>
	<v-form>
		<v-container class="display-1 font-weight-light">
			<v-card v-if="error">
				<v-card-text>{{ error }}</v-card-text>
			</v-card>
			<v-card v-else>
				<v-card-text>
					<v-container>
						<v-layout>
							<v-flex>Employee Role: {{ roleData.name }}</v-flex>
						</v-layout>
					</v-container>
					<v-container>
						<v-layout>
							<v-flex>
								<v-card-text class="title">Colors</v-card-text>
							</v-flex>
						</v-layout>
						<v-layout>
							<v-flex>
								<swatches v-model="color" colors="basic" inline></swatches>
							</v-flex>
						</v-layout>
					</v-container>
					<v-container>
						<v-layout>
							<v-flex sm2>
								<v-select
									v-model="unit"
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
									v-model="per"
									label="period"
									:items="['week', '2 weeks', 'month', 'quarter', 'year']"
								></v-select>
							</v-flex>
							<v-flex sm1 class="title">-</v-flex>
							<v-flex sm2>
								<v-text-field v-model="amount" label="Amount"></v-text-field>
							</v-flex>
						</v-layout>
					</v-container>
				</v-card-text>
			</v-card>
		</v-container>
	</v-form>
</template>

<script>
import Swatches from "vue-swatches";

export default {
	name: "EditRole",
	components: { Swatches },
	data() {
		return {};
	},
	computed: {
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
