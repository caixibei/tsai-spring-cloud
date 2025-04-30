const vInteger = {
    mounted(el, binding) {
        const input = el.tagName === 'INPUT' ? el : el.querySelector('input');
        if (!input) return;
        let composing = false;
        const handler = (event) => {
        	event.preventDefault();
        	if(composing) return;
            const value = event.target.value;
            const newValue = value.replace(/[^\d]/g, '')
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