const createApp = Vue.createApp;
const ref = Vue.ref;
const reactive = Vue.reactive;
const computed = Vue.computed;
const onMounted = Vue.onMounted;
const defineEmits = Vue.defineEmits;
const defineProps = Vue.defineProps;
const markRaw = Vue.markRaw;
const createRouter = VueRouter.createRouter;
const createWebHashHistory = VueRouter.createWebHashHistory;
const routes = [];
const router = createRouter({
    history: createWebHashHistory(),
    routes: routes,
});

const app = createApp({
    components: {
        TsaiPagination: TsaiPagination,
        FileUploadDialog: FileUploadDialog
    },
    setup: function(){
        
        return {}
    }
})

app.use(ElementPlus, {
    locale: ElementPlusLocaleZhCn,
});
app.use(router);
app.directive('number', vNumber);
app.directive('integer', vInteger);
app.mount("#app");