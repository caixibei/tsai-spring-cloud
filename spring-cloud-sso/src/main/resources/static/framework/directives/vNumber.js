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
}

const vNumber = {
    mounted(el, binding) {
        const input = el.tagName === 'INPUT' ? el : el.querySelector('input');
        if (!input) return;
        let composing = false;
        const handler = (event) => {
        	event.preventDefault();
        	if(composing) return;
            const value = event.target.value;
            const newValue = value
                .replace(/[^-.\d]/g, '') // 只允许数字、小数点和负号
                .replace(/(?!^)-/g, '') // 移除所有不在开头的负号
                .replace(/(\..*?)\./g, '$1') // 确保小数点只能出现一次
                .replace(/^-\.(\d+)/, '-0.$1') // 处理 -.123 的情况
                .replace(/^\.(\d+)/, '0.$1') // 处理 .123 的情况
                .replace(/^(-)?0+(\d)/, '$1$2') // 移除开头的多余零
            if (value !== newValue) {
                input.value = newValue;
                input.dispatchEvent(new Event('input'));
            }
        };
        const composingStartHandler = () => {
        	composing = true;
        }
        const composingEndHandler = (event) => {
        	composing = false;
        	handler(event)
        }
        input.addEventListener('input', handler);
        input.addEventListener('compositionstart', composingStartHandler);
        input.addEventListener('compositionend', composingEndHandler);
        el._numberHandler = handler;
        el._composingStartHandler = composingStartHandler;
        el._composingEndHandler = composingEndHandler;
    },
    unmounted(el) {
        const input = el.tagName === 'INPUT' ? el : el.querySelector('input');
        if (input) {
        	if(el._numberHandler){
        		input.removeEventListener('input', el._numberHandler);
                delete el._numberHandler;
        	}
        	if(el._composingStartHandler){
        		input.removeEventListener('compositionstart', el._composingStartHandler);
                delete el._composingStartHandler;
        	}
        	if(el._composingEndHandler){
        		input.removeEventListener('compositionend', el._composingEndHandler);
                delete el._composingEndHandler;
        	}
        }
    },
}