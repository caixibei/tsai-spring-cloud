Number.prototype.toFixed = function (d) {
    var s = this + "";
    if (!d) d = 0;
    if (s.indexOf(".") == -1) s += ".";
    s += new Array(d + 1).join("0");
    if (new RegExp("^(-|\\+)?(\\d+(\\.\\d{0," + (d + 1) + "})?)\\d*$").test(s)) {
        var s = "0" + RegExp.$2,
            pm = RegExp.$1,
            a = RegExp.$3.length,
            b = true;
        if (a == d + 2) {
            a = s.match(/\d/g);
            if (parseInt(a[a.length - 1]) > 4) {
                for (var i = a.length - 2; i >= 0; i--) {
                    a[i] = parseInt(a[i]) + 1;
                    if (a[i] == 10) {
                        a[i] = 0;
                        b = i != 1;
                    } else break;
                }
            }
            s = a.join("").replace(new RegExp("(\\d+)(\\d{" + d + "})\\d$"), "$1.$2");
        }
        if (b) s = s.substr(1);
        return (pm + s).replace(/\.$/, "");
    }
    return this + "";
};

const vNumber = {
    mounted(el, binding) {
        const input = el.tagName === 'INPUT' ? el : el.querySelector('input');
        if (!input) return;
        // 定义事件处理函数
        const handler = (event) => {
            const value = event.target.value;
            // 过滤和处理输入内容
            const newValue = value
                .replace(/[^-.\d]/g, '') // 只允许数字、小数点和负号
                .replace(/(?!^)-/g, '') // 移除所有不在开头的负号
                .replace(/(\..*?)\./g, '$1') // 确保小数点只能出现一次
                .replace(/^-\.(\d+)/, '-0.$1') // 处理 -.123 的情况
                .replace(/^\.(\d+)/, '0.$1') // 处理 .123 的情况
                .replace(/^(-)?0+(\d)/, '$1$2') // 移除开头的多余零
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