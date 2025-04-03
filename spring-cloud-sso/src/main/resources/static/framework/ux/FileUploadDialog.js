const templateFileUpload = `
    <el-dialog v-model="dialogVisible" class="tsai-upload-dialog" title="文件上传" width="26%" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="true" append-to-body>
        <el-form :model="form" ref="formRef" label-width="auto" label-position="right" label-suffix="：" size="small">
            <el-form-item class="upload-file-item" label="文件名称" prop="fileName">
                <el-upload
                    ref="uploadRef"
                    :action="action"
                    :on-success="handleSuccess"
                    :on-error="handleError"
                    :on-remove="handleRemove"
                    :on-change="handleChange"
                    :show-file-list="true" 
                    :auto-upload="false"
                    :accept="acceptType"
                    :limit="1"
                    :data="extraData"
                    :on-exceed="handleExceed"
                >
                    <el-space class="file-upload-space">
                        <el-input v-model="form.fileName" slot="trigger" placeholder="请选择文件" readonly></el-input>
                        <el-button slot="trigger" type="primary">选择文件</el-button>
                    </el-space>
                </el-upload>
            </el-form-item>
            <el-form-item>
                <el-link type="extra" @click="templateFun">下载模板</el-link>
            </el-form-item>
            <el-form-item>
                <div class="file-upload-tips">
                    <el-text type="warning"><b>温馨提示：</b></el-text>
                    <el-text type="warning">1.仅支持 {{acceptType}} 类型的文件，且文件大小不可超过 {{limitSize}} MB；</el-text>
                    <el-text type="warning">2.选择文件后，请点击保存按钮，否则文件将不会上传；</el-text>
                    <el-text type="warning">3.文件上传后，请勿重复上传；</el-text>
                </div>
            </el-form-item>
            <el-form-item>
                <div style="margin-left: auto;">
                    <el-button @click="close">关闭</el-button>
                    <el-button type="primary" @click="submit">保存</el-button>
                </div>
            </el-form-item>
        </el-form>
    </el-dialog>
`

const FileUploadDialog = {
    name: 'FileUploadDialog',
    template: templateFileUpload,
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        acceptType: {
            type: String,
            default: '.xls,.xlsx'
        },
        extraData: {
            type: Object,
            default: {}
        },
        action: {
            type: String,
            required: true
        },
        limitSize: {
            type: Number,
            default: 10
        },
        uploaded: {
            type: Function,
            default: () => {}
        },
        templateFun: {
            type: Function,
            default: () => {}
        }
    },
    setup(props, context) {
        const uploadRef = ref()
        const formRef = ref()
        const form = ref({
            fileName: undefined
        })

        const dialogVisible = computed({
            get() {
                return props.visible
            },
            set(value) {
                context.emit('update:visible', value)
            }
        })

        const handleSuccess = (response, file, fileList) => {
            ElementPlus.ElMessage({
                type: 'success',
                message: '上传成功'
            })
            formRef.value && formRef.value.resetFields()
            dialogVisible.value = false
            props.uploaded()
        }

        const handleError = (error, file, fileList) => {
            ElementPlus.ElMessage({
                type: 'warning',
                message: '上传失败'
            })
            formRef.value && formRef.value.resetFields()
        }

        const handleChange = (file, fileList) => {
            form.value.fileName = file.name
        }

        const close = () => {
            dialogVisible.value = false
        }

        const handleExceed = (files, fileList) => {
            uploadRef.value && uploadRef.value.clearFiles()
            const file = files[0]
            file.uid = ElementPlus.genFileId()
            uploadRef.value && uploadRef.value.handleStart(file)
        }

        const handleRemove = (file, fileList) => {
            formRef.value && formRef.value.resetFields()
        }

        const submit = () => {
            uploadRef.value && uploadRef.value.submit()
        }

        return {
            dialogVisible: dialogVisible,
            uploadRef: uploadRef,
            formRef: formRef,
            form: form,
            handleSuccess: handleSuccess,
            handleError: handleError,
            handleChange: handleChange,
            handleRemove: handleRemove,
            handleExceed: handleExceed,
            close: close,
            submit: submit
        }
    }
}