Model.getDatasources()[0].fetchVariable('VFB_00000001')
Model.getDatasources()[0].fetchVariable('FBbt_00100219')

Instances.getInstance("VFB_00000001.VFB_00000001_meta")
Instances.getInstance("FBbt_00100219.FBbt_00100219_meta")

var customHandler=function(node, path){var meta=path+"."+path+"_meta"; if(node!=undefined){var metanode= Instances.getInstance(meta);G.addWidget(1).setData(metanode).setName(node.getName()).addCustomNodeHandler(customHandler);}else{Model.getDatasources()[0].fetchVariable(path,function(){Instances.getInstance(meta);G.addWidget(1).setData(eval(meta)).setName(eval(path).getName()).addCustomNodeHandler(customHandler);});}};

G.addWidget(1).setData(FBbt_00100219.FBbt_00100219_meta).setName(FBbt_00100219.getName()).setPosition(587,110).addCustomNodeHandler(customHandler,'click');
G.addWidget(1).setData(VFB_00000001.VFB_00000001_meta).setName(VFB_00000001.getName()).setPosition(587,110).addCustomNodeHandler(customHandler,'click');

window.Model.getLibraries()[1].getTypes()[0].resolve()




