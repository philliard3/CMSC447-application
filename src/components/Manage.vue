<template>
	<div>
		<!--
    <v-container>
      <v-card>
        <v-card-text>
          <v-layout>
            <v-flex>
              <v-btn color="success" flat outline>Manage Employees</v-btn>
            </v-flex>
            <v-flex>
              <v-btn color="primary" flat outline>Manage Roles</v-btn>
            </v-flex>
            <v-flex>
              <v-btn color="warning" flat outline>Manage Locations</v-btn>
            </v-flex>
          </v-layout>
        </v-card-text>
      </v-card>
    </v-container>
    -->
		<div class="col1">
			<EmployeeTable />
		</div>
		<div class="col2">
			<RoleTable />
		</div>
		<div class="col3">
			<LocationList />
		</div>
	</div>
</template>

<script>
import EmployeeTable from "./EmployeeTable";
import RoleTable from "./RoleTable";
import LocationList from "./LocationList";

export default {
	name: "Manage",
	components: { EmployeeTable, RoleTable, LocationList },
	data() {
		return {};
	}
};
</script>

<style>
.col1 {
	min-width: 50%;
	display: inline-block;
	vertical-align: top;
}

.col2 {
	min-width: 45%;
	display: inline-block;
	vertical-align: top;
}

.col3 {
	min-width: 45%;
	display: inline-block;
	vertical-align: top;
}
</style>
