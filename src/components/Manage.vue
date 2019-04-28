<template>
	<div>
		<div class="col1">
			<EmployeeTable />
		</div>
		<div class="col2">
			<RoleTable />
		</div>
	</div>
</template>

<script>
import EmployeeTable from "./EmployeeTable";
import RoleTable from "./RoleTable";

export default {
	name: "Manage",
	components: { EmployeeTable, RoleTable },
	data() {
		return {};
	}
};
</script>

<style>
.col1 {
	width: 50%;
	min-width: 700px;
	margin-right: 2%;
	display: inline-block;
	vertical-align: top;
}

.col2 {
	width: 45%;
	display: inline-block;
	vertical-align: top;
}
</style>
