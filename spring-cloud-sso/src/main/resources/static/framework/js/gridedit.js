// 双击单元格
const cellDblclick = function (row, column, cell, event) {
	current_cell.value = '_' + row.index + '_' + column.index;
	event.stopPropagation();
}

// 单元格样式名称
const cellClassName = function (record) {
	const row = record.row;
	const column = record.column;
	const rowIndex = record.rowIndex;
	const columnIndex = record.columnIndex;
	row.index = rowIndex;
	column.index = columnIndex;
};

// 单元格是否可编辑
const showEdit = function (scope) {
	return current_cell.value === '_' + scope.row.index + '_' + scope.column.index;
};