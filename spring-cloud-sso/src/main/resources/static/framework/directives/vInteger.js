const vInteger = {
    mounted(el, binding) {
        const input = el.tagName === 'INPUT' ? el : el.querySelector('input');
        if (!input) return;
        // 定义事件处理函数
        const handler = (event) => {
            const value = event.target.value;
            // 过滤和处理输入内容
            const newValue = value.replace(/[^\d]/g, '') // 只允许数字、小数点和负号
            // 如果值发生变化，更新输入框的值并触发 input 事件
            if (value !== newValue) {
                input.value = newValue;
                input.dispatchEvent(new Event('input'));
            }
        };
        // 添加事件监听器
        input.addEventListener('input', handler);
        // 保存事件处理函数以便后续清理
        el._numberHandler = handler;
    },
    unmounted(el) {
        // 获取输入框元素
        const input = el.tagName === 'INPUT' ? el : el.querySelector('input');
        if (input && el._numberHandler) {
            // 移除事件监听器
            input.removeEventListener('input', el._numberHandler);
            // 清理保存的事件处理函数
            delete el._numberHandler;
        }
    },
}