// 获取对象
function getObject(objName) {
	if (document.getElementById(objName)) {
		return document.getElementById(objName);
	}
	else if(document.all) {
		return document.all[objName];
	}
	else if(document.layers) {
		return document.layers[objName];
	}
}

// 载入焦点
function onLoadFocus(objName) {
	getObject(objName).focus();
}