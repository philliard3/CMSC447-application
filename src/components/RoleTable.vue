<template>
	<v-container>
		<v-card>
			<v-card-text>
				<v-container>
					<div class="display-1 font-weight-light" id="header-row-1">
						Roles
						<v-btn color="success" @click="createRole">
							<v-icon flat>add</v-icon>&nbsp;New Role
						</v-btn>
					</div>
				</v-container>
				<v-data-table
					v-model="selected"
					v-on:input="$emit('input', selected.map(el => el.roleID))"
					:headers="headers"
					:items="roles"
					:pagination.sync="pagination"
					select-all
					item-key="name"
					class="elevation-1 table"
				>
					<template v-slot:headers="props">
						<tr>
							<th>
								<v-checkbox
									:input-value="props.all"
									:indeterminate="props.indeterminate"
									primary
									hide-details
									@click.stop="toggleAll"
								></v-checkbox>
							</th>
							<th
								v-for="header in props.headers"
								:key="header.text"
								:class="[
									'column sortable',
									pagination.descending ? 'desc' : 'asc',
									header.value === pagination.sortBy ? 'active' : ''
								]"
								@click="changeSort(header.value)"
							>
								<v-icon small>arrow_upward</v-icon>
								{{ header.text }}
							</th>
						</tr>
					</template>
					<template v-slot:items="props">
						<tr
							:active="props.selected"
							@click="props.selected = !props.selected"
						>
							<td>
								<v-checkbox
									:input-value="props.selected"
									primary
									hide-details
								></v-checkbox>
							</td>
							<td>
								<router-link :to="'/manage/roles/' + props.item.roleID">
									{{ props.item.name }}
								</router-link>
							</td>
							<td class="text-xs-right">
								<v-icon :color="props.item.color">work</v-icon>
							</td>
							<td class="text-xs-right">
								<v-btn
									color="primary"
									:to="'/manage/roles/' + props.item.roleID"
									>Edit</v-btn
								>
							</td>
						</tr>
					</template>
				</v-data-table>
			</v-card-text>
		</v-card>
	</v-container>
</template>

<script>
export default {
	name: "RoleTable",
	props: { roleIDs: Array },
	data() {
		const roles = this.roleIDs
			? this.$store.getters.roles.map(role => ({
					assigned: this.roleIDs.includes(role.roleID),
					...role
			  }))
			: this.$store.getters.roles;
		return {
			pagination: {
				sortBy: "name"
			},
			selected: roles.filter(role => role.assigned),
			headers: [
				{
					text: "Name",
					align: "left",
					value: "name"
				},
				{ text: "Color", value: "color" }
			],
			roles
		};
	},

	methods: {
		toggleAll() {
			if (this.selected.length) this.selected = [];
			else this.selected = this.roles.slice();
		},
		changeSort(column) {
			if (this.pagination.sortBy === column) {
				this.pagination.descending = !this.pagination.descending;
			} else {
				this.pagination.sortBy = column;
				this.pagination.descending = false;
			}
		},
		createRole() {
			// create new role and get ID
			// const roleID = this.$store.dispatch("createEmployee")

			// placeholder while store functions are implemented
			const roleID = new Date().getTime() % Math.pow(2, 31);

			this.$store.commit("addRole", {
				roleData: {
					roleID,
					name: `New Role (${roleID})`,
					color: null,
					shifts: [],
					required: []
				}
			});

			this.$router.push("/manage/roles/" + roleID);
		}
	}
};
</script>
