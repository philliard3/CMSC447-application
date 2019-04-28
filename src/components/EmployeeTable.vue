<template>
	<v-container>
		<v-container>
			<div class="display-1 font-weight-light" id="header-row-1">
				Employees
				<v-btn color="success" to="/manage/employees">
					<v-icon flat>person_add</v-icon>&nbsp;New Employee
				</v-btn>
			</div>
			<v-layout row wrap class="header-row-2" align-center>
				<v-flex xs2>
					<h2 class="title font-weight-light">Search by&nbsp;</h2>
				</v-flex>
				<v-flex xs3>
					<v-select
						v-model="searchBy"
						:items="headers.map(header => header.text)"
					></v-select> </v-flex
				>&nbsp;
				<v-flex xs5>
					<v-text-field v-model="searchText" clearable></v-text-field>
				</v-flex>
			</v-layout>
		</v-container>
		<v-data-table
			v-model="selected"
			:headers="headers"
			:items="filteredEmployees"
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
						<router-link to="/manage/employees">{{
							props.item.name
						}}</router-link>
					</td>
					<td class="text-xs-right">
						<span v-for="(role, index) in props.item.roles" :key="index">
							<router-link to="/manage/roles">{{ role }}</router-link>
							<span v-if="index < props.item.roles.length - 1">,&nbsp;</span>
						</span>
					</td>
					<td class="text-xs-right">{{ props.item.hours }}</td>
					<td class="text-xs-right">
						<v-btn color="primary" to="/manage/employees">Edit</v-btn>
					</td>
				</tr>
			</template>
		</v-data-table>
	</v-container>
</template>

<script>
export default {
	name: "EmployeeTable",
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
				{ text: "Roles", value: "roles" },
				{ text: "Hours this Pay Period", value: "hours" }
			],
			employees: [
				{
					name: "John Doe",
					roles: ["Doctor"],
					hours: 12
				},
				{
					name: "Sam Smith",
					roles: ["Nurse", "Nurse Practitioner"],
					hours: 15
				},
				{
					name: "Janet Mars",
					roles: ["Nurse"],
					hours: 20
				}
			],
			searchBy: "Name",
			searchText: ""
		};
	},

	computed: {
		filteredEmployees() {
			const termFilter = this.headers.filter(
				header => header.text === this.searchBy
			);

			const searchTerm = termFilter.length > 0 ? termFilter[0].value : null;

			if (searchTerm === "roles") {
				return this.employees.filter(employee =>
					employee.roles.some(role =>
						role.toUpperCase().includes(this.searchText.toUpperCase())
					)
				);
			}

			return this.employees.filter(employee =>
				String(employee[searchTerm])
					.toUpperCase()
					.includes(this.searchText.toUpperCase())
			);
		}
	},

	methods: {
		toggleAll() {
			if (this.selected.length) this.selected = [];
			else this.selected = this.employees.slice();
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
