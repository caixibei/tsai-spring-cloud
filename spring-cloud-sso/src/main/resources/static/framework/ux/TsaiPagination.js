const domTemplate = `
	<div :class="{ 'pagination-left': align, 'pagination-right': !align }" class="tsai-pagination">
		<el-pagination
			v-model:current-page="currentPage"
			v-model:page-size="pageSize"
			:background="background"
			:pager-count="5"
			layout="sizes, prev, pager, next, jumper, total"
			:total="total"
			size="small"
			@size-change="handleSizeChange"
			@current-change="handleCurrentChange"
		/>
	</div>
`;

const TsaiPagination = {
	name: 'TsaiPagination',
	template: domTemplate,
	props: {
		align: {
			type: Boolean,
			default: false
		},
		start: {
			type: Number,
			default: 1
		},
		limit: {
			type: Number,
			default: 20
		},
		total: {
			type: Number,
			default: 0
		},
		background: {
			type: Boolean,
			default: true
		},
		callback: {
			type: Function,
			default: () => {}
		}
	},
	setup: function (props, context) {
		const currentPage = computed({
			get: function () {
				return props.start;
			},
			set: function (val) {
				context.emit('update:start', Number(val));
			}
		});

		const pageSize = computed({
			get: function () {
				return props.limit;
			},
			set: function (val) {
				context.emit('update:limit', Number(val));
			}
		});

		const handleSizeChange = function (val) {
			pageSize.value = Number(val);
			props.callback()
		};

		const handleCurrentChange = function (val) {
			currentPage.value = Number(val);
			props.callback()
		};

		return {
			handleSizeChange: handleSizeChange,
			handleCurrentChange: handleCurrentChange,
			pageSize: pageSize,
			currentPage: currentPage
		}
	}
}