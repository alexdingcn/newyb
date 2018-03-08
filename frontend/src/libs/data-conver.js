let dataConver = {

};

/*
把数组转化为iview中的简单树数据结构
attributes描述了每一个数组对象中的父子关联字段名、根值(当父字段值为null或者undefined时也认为是根)和显示名的字段
attributes = {
    rootId: null|undefined|value, //描述根节点中的父字段的值
    idKey: 'id',  //treeItem对象的id属性名
    titleKey: titleValue, //treeItem对象中的title属性名(不是值，是属性名, 请注意)
    parentKey: 'parentId', //数组对象中与childKey的值比较的属性名
    expand: true|false  //treeItem对象中的expand属性的值，默认为false
}
*/
dataConver.arryToIviewTreeData = function (arrs, attributes) {
    let resData = arrs;
    let tree = [];
    for (let i = 0; i < resData.length; i++) {
        if (attributes.rootId === null || attributes.rootId === undefined || resData[i][attributes.parentKey] === attributes.rootId) {
            let obj = {
                id: resData[i][attributes.idKey],
                title: resData[i][attributes.titleKey],
                expand: attributes.expand === true,
                children: []
            };
            tree.push(obj);
            resData.splice(i, 1);
            i--;
        }
    }
    run(tree);
    function run (chiArr) {
        if (resData.length !== 0) {
            for (let i = 0; i < chiArr.length; i++) {
                for (let j = 0; j < resData.length; j++) {
                    if (chiArr[i].id === resData[j][attributes.parentId]) {
                        let obj = {
                            id: resData[j][attributes.idKey],
                            title: resData[j][attributes.titleKey],
                            expand: attributes.expand === true,
                            children: []
                        };
                        chiArr[i].children.push(obj);
                        resData.splice(j, 1);
                        j--;
                    }
                }
                run(chiArr[i].children);
            }
        }
    }
    return tree;
};

export default dataConver;
