<template>
	<v-container>
		<v-container>
			<div class="display-1 font-weight-light" id="header-row-1">
				Roles
				<v-btn color="success" to="/manage/roles">
					<v-icon flat>add</v-icon>&nbsp;New Role
				</v-btn>
			</div>
		</v-container>
		<v-data-table
			v-model="selected"
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
				<tr :active="props.selected" @click="props.selected = !props.selected">
					<td>
						<v-checkbox
							:input-value="props.selected"
							primary
							hide-details
						></v-checkbox>
					</td>
					<td>
						<router-link to="/manage/roles">{{ props.item.name }}</router-link>
					</td>
					<td class="text-xs-right">
						<v-icon :color="props.item.color">work</v-icon>
					</td>
					<td class="text-xs-right">
						<v-btn color="primary" to="/manage/roles">Edit</v-btn>
					</td>
				</tr>
			</template>
		</v-data-table>
	</v-container>
</template>

<script>
export default {
	name: "RoleTable",
	data() {
		return {
			pagination: {
				sortBy: "name"
			},
			selected: [],
			headers: [
				{
					text: "Name",
					align: "left",
					value: "name"
				},
				{ text: "Color", value: "color" }
			],
			roles: [
				{
					name: "Doctor",
					color: "#3cb371"
				},
				{
					name: "Nurse",
					color: "#ee82ee"
				},
				{
					name: "Nurse Practitioner",
					color: "#6a5acd"
				}
			]
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
		}
	}
};
</script>
