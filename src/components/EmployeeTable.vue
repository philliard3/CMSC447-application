<template>
	<v-container>
		<v-card>
			<v-card-text>
				<v-container>
					<div class="display-1 font-weight-light" id="header-row-1">
						Employees
						<v-btn color="success" @click="createEmployee">
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
								<router-link
									:to="'/manage/employees/' + props.item.employeeID"
									>{{ props.item.name }}</router-link
								>
							</td>
							<td class="text-xs-right">
								<span v-for="(role, index) in props.item.roles" :key="index">
									<router-link :to="'/manage/roles/' + role.roleID">
										{{ role.name }}
									</router-link>
									<span v-if="index < props.item.roles.length - 1"
										>,&nbsp;</span
									>
								</span>
							</td>
							<td class="text-xs-right">
								<v-btn
									color="primary"
									:to="'/manage/employees/' + props.item.employeeID"
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
			employees: this.$store.getters.employees,
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
						role.name.toUpperCase().includes(this.searchText.toUpperCase())
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
		},
		createEmployee() {
			// create new employee and get ID
			// const employeeID = this.$store.dispatch("createEmployee")

			// placeholder while store functions are implemented
			const employeeID = new Date().getTime() % Math.pow(2, 31);

			this.$store.commit("addEmployee", {
				employeeData: {
					employeeID,
					name: `New Employee (${employeeID})`,
					roles: []
				}
			});

			this.$router.push("/manage/employees/" + employeeID);
		}
	}
};
</script>
